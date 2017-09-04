/*
 * Copyright 2011 Red Hat inc. and third party contributors as noted
 * by the author tags.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.ceylon.cmr.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.ModuleDependencyInfo;
import com.redhat.ceylon.cmr.api.ModuleInfo;
import com.redhat.ceylon.cmr.api.Overrides;
import com.redhat.ceylon.cmr.api.PathFilterParser;
import com.redhat.ceylon.common.Backends;

/**
 * Read module info from module.xml.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
final public class XmlDependencyResolver extends ModulesDependencyResolver {
    public static final XmlDependencyResolver INSTANCE = new XmlDependencyResolver();

    private XmlDependencyResolver() {
        super(ArtifactContext.MODULE_XML);
    }

    @Override
    public ModuleInfo resolveFromInputStream(InputStream stream, String name, String version, Overrides overrides) {
        try {
            final Module module = parse(stream);
            final Set<ModuleDependencyInfo> infos = new LinkedHashSet<>();
            for (ModuleIdentifier mi : module.getDependencies()) {
                infos.add(new ModuleDependencyInfo(null, mi.getName(), mi.getSlot(), mi.isOptional(), mi.isExport(), Backends.JAVA));
            }
            ModuleInfo ret = new ModuleInfo(null, name, version, module.getGroupId(), module.getArtifactId(), null, module.getFilter(), infos);
            if(overrides != null)
                ret = overrides.applyOverrides(name, version, ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class ModuleIdentifier implements Comparable<ModuleIdentifier> {
        private String name;
        private String slot;
        private boolean optional;
        private boolean export;

        public ModuleIdentifier(String name, String slot, boolean optional, boolean export) {
            this.name = name;
            if (slot == null || slot.length() == 0)
                slot = "main";
            this.slot = slot;
            this.optional = optional;
            this.export = export;
        }

//        public static ModuleIdentifier create(String string) {
//            String[] split = string.split(":");
//            return new ModuleIdentifier(split[0], split.length > 1 ? split[1] : null, false, false);
//        }

        public String getName() {
            return name;
        }

        public String getSlot() {
            return slot;
        }

        public boolean isOptional() {
            return optional;
        }

        public boolean isExport() {
            return export;
        }

        public int compareTo(ModuleIdentifier o) {
            int diff = name.compareTo(o.getName());
            if (diff != 0)
                return diff;
            return slot.compareTo(o.getSlot());
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ModuleIdentifier that = (ModuleIdentifier) o;

            if (!name.equals(that.name)) return false;
            if (!slot.equals(that.slot)) return false;

            return true;
        }

        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + slot.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return name + ":" + slot;
        }
    }

    private static class Module {
        private ModuleIdentifier module;
        private Set<ModuleIdentifier> dependencies = new LinkedHashSet<>();
        private String filter;
        private String groupId;
        private String artifactId;

        public Module(ModuleIdentifier module) {
            this.module = module;
        }

        public void addDependency(ModuleIdentifier mi) {
            dependencies.add(mi);
        }

        public ModuleIdentifier getModule() {
            return module;
        }

        public Set<ModuleIdentifier> getDependencies() {
            return dependencies;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Module module1 = (Module) o;

            if (!module.equals(module1.module)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return module.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("\nModule: " + module + "\n");
            builder.append("Dependencies: ").append(dependencies.size()).append("\n");
            for (ModuleIdentifier dep : dependencies) {
                builder.append("\t").append(dep).append("\n");
            }
            builder.append("Filter: ").append(filter).append("\n");
            return builder.toString();
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public String getFilter() {
            return filter;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }

    protected static Module parse(InputStream is) throws Exception {
        try {
            Document document = parseXml(is);
            Element root = document.getDocumentElement();
            ModuleIdentifier main = getModuleIdentifier(root);
            Module module = new Module(main);
            Element properties = getChildElement(root, "properties");
            if (properties != null) {
                for (Element property : getElements(properties, "property")) {
                    String name = property.getAttribute("name");
                    if("groupId".equals(name))
                        module.setGroupId(property.getAttribute("value"));
                    else if("artifactId".equals(name))
                        module.setArtifactId(property.getAttribute("value"));
                }
            }
            Element dependencies = getChildElement(root, "dependencies");
            if (dependencies != null) {
                for (Element dependency : getElements(dependencies, "module")) {
                    module.addDependency(getModuleIdentifier(dependency));
                }
            }
            // filter
            Element filterNode = getChildElement(root, "exports");
            if (filterNode != null) {
                module.setFilter(PathFilterParser.convertNodeToString(filterNode));
            }
            return module;
        } finally {
            is.close();
        }
    }

    protected static ModuleIdentifier getModuleIdentifier(Element root) {
        return new ModuleIdentifier(root.getAttribute("name"), root.getAttribute("slot"), Boolean.parseBoolean(root.getAttribute("optional")), Boolean.parseBoolean(root.getAttribute("export")));
    }

    protected static Document parseXml(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputStream);
        doc.getDocumentElement().normalize();
        return doc;
    }

    protected static List<Element> getElements(Element parent, String tagName) {
        List<Element> elements = new ArrayList<>();
        NodeList nodes = parent.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node node = nodes.item(i);
            if (node instanceof Element) {
                elements.add(Element.class.cast(node));
            }
        }
        return elements;
    }

    protected static Element getChildElement(Element parent, String tagName) {
        List<Element> elements = getElements(parent, tagName);
        return (elements.size() > 0) ? elements.get(0) : null;
    }
}
