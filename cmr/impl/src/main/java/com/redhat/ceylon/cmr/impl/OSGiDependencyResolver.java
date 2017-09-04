/*
 * Copyright 2014 Red Hat inc. and third party contributors as noted
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.ceylon.cmr.api.AbstractDependencyResolver;
import com.redhat.ceylon.cmr.api.DependencyContext;
import com.redhat.ceylon.cmr.api.DependencyResolver;
import com.redhat.ceylon.cmr.api.ModuleDependencyInfo;
import com.redhat.ceylon.cmr.api.ModuleInfo;
import com.redhat.ceylon.cmr.api.Overrides;
import com.redhat.ceylon.cmr.spi.Node;
import com.redhat.ceylon.common.Backends;
import com.redhat.ceylon.common.ModuleUtil;
import com.redhat.ceylon.model.cmr.ArtifactResult;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class OSGiDependencyResolver extends AbstractDependencyResolver {
    private static final Logger log = Logger.getLogger(OSGiDependencyResolver.class.getName());
    public static final DependencyResolver INSTANCE = new OSGiDependencyResolver();

    @Override
    public ModuleInfo resolve(DependencyContext context, Overrides overrides) {
        if (context.ignoreInner() == false) {
            ArtifactResult result = context.result();
            File mod = result.artifact();
            if (mod != null && IOUtils.isZipFile(mod)) {
                InputStream stream = IOUtils.findDescriptor(result, JarFile.MANIFEST_NAME);
                if (stream != null) {
                    try {
                        return resolveFromInputStream(stream, result.name(), result.version(), overrides);
                    } finally {
                        IOUtils.safeClose(stream);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ModuleInfo resolveFromFile(File file, String name, String version, Overrides overrides) {
        return null;
    }

    @Override
    public ModuleInfo resolveFromInputStream(InputStream stream, String name, String version, Overrides overrides) {
        if (stream == null) {
            return null;
        }

        try {
            Manifest manifest = new Manifest(stream);
            Attributes attributes = manifest.getMainAttributes();
            String requireBundle = attributes.getValue("Require-Bundle");
            if (requireBundle == null) {
                if (log.isLoggable(Level.FINE)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    manifest.write(baos);
                    log.fine(String.format("No OSGi Require-Bundle attribute, main-attributes: %s", new String(baos.toByteArray())));
                }
                // we must return null to allow other resolvers to try, it's valid to have
                // a manifest with no OSGi in there
                return null;
            } else {
                return parseRequireBundle(requireBundle, name, version, overrides);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.safeClose(stream);
        }
    }

    public Node descriptor(Node artifact) {
        return null;
    }

    private ModuleInfo parseRequireBundle(String requireBundle, String name, String version, Overrides overrides) {
        Set<ModuleDependencyInfo> infos = new HashSet<>();
        requireBundle = requireBundle.replaceAll(";bundle-version=\"\\[([^,]+),[^,]+(\\]|\\))\"", ";bundle-version=$1");
        String[] bundles = requireBundle.split(",");
        for (String bundle : bundles) {
            infos.add(parseModuleInfo(bundle));
        }
        ModuleInfo ret = new ModuleInfo(null, name, version,
                // FIXME: does OSGi store this?
                ModuleUtil.getMavenGroupIdIfMavenModule(name),
                ModuleUtil.getMavenArtifactIdIfMavenModule(name),
                ModuleUtil.getMavenClassifierIfMavenModule(name),
                null, infos);
        if(overrides != null)
            ret = overrides.applyOverrides(name, version, ret);
        return ret;
    }

    // very simplistic osgi parsing ...
    private ModuleDependencyInfo parseModuleInfo(String bundle) {
        int p = bundle.indexOf(';');
        String name = (p < 0) ? bundle : bundle.substring(0, p);
        String version = "0.0.0";
        boolean optional = false;
        boolean shared = false;
        if (p > 0) {
            String[] parameters = bundle.substring(p + 1).split(";");
            for (String param : parameters) {
                int d = param.indexOf(":=");
                if (d > 0) {
                    String[] directive = parseDirective(param);
                    String key = directive[0];
                    String value = directive[1];
                    if (key.equals("visibility") && value.equals("reexport")) {
                        shared = true;
                    }
                    if (key.equals("resolution") && value.equals("optional")) {
                        optional = true;
                    }
                    continue;
                }
                int a = param.indexOf("=");
                if (a > 0) {
                    String[] attribute = parseAttribute(param);
                    String key = attribute[0];
                    String value = attribute[1];
                    if (key.equals("bundle-version")) {
                        version = value;
                    }
                    continue;
                }
                log.warning(String.format("Parameter %s is not directive or attribute.", param));
            }
        }
        return new ModuleDependencyInfo(null, name, version, optional, shared, Backends.JAVA);
    }

    private String[] parseDirective(String parameter) {
        String[] split = parameter.split(":=");
        return new String[]{split[0].trim(), split[1].trim()};
    }

    private String[] parseAttribute(String parameter) {
        String[] split = parameter.split("=");
        return new String[]{split[0].trim(), split[1].trim()};
    }
}
