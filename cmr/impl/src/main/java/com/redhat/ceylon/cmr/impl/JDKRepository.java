/*
 * Copyright 2012 Red Hat inc. and third party contributors as noted 
 * by the author tags.
 * 
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

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.ContentFinderDelegate;
import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleVersionArtifact;
import com.redhat.ceylon.cmr.api.ModuleVersionDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionQuery;
import com.redhat.ceylon.cmr.api.ModuleVersionResult;
import com.redhat.ceylon.cmr.api.Overrides;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.spi.Node;
import com.redhat.ceylon.cmr.spi.OpenNode;
import com.redhat.ceylon.model.cmr.ArtifactResult;
import com.redhat.ceylon.model.loader.JdkProvider;

/**
 * Repository that provides ContentFinder implementation for JDK modules
 *
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class JDKRepository extends AbstractRepository {


    public static final String JDK_REPOSITORY_DISPLAY_STRING = "JDK modules repository";
    
    public static final String NAMESPACE = "jdk";

    private static final String JAVA_ORIGIN = "Java Runtime";

	private JdkProvider jdkProvider;

    public JDKRepository() {
        super(new JDKRoot());
        // FIXME: probably has to become an option
        this.jdkProvider = new JdkProvider();
        ((JDKRoot)getRoot()).finishSetupYouDumbass(jdkProvider);
    }

    @Override
    protected ArtifactResult getArtifactResultInternal(RepositoryManager manager, Node node) {
        return null;
    }

    public static class JDKRoot extends DefaultNode implements ContentFinderDelegate {
        private static final long serialVersionUID = 3536040774400941766L;
        
        private JdkProvider jdkProvider;
		private SortedSet<String> sortedModuleNames;

        public JDKRoot() {
            addService(ContentFinderDelegate.class, this);
        }

        private void finishSetupYouDumbass(JdkProvider jdkProvider){
        	this.jdkProvider = jdkProvider;
        	sortedModuleNames = new TreeSet<>();
        	sortedModuleNames.addAll(jdkProvider.getJDKModuleNames());
        }
        
        @Override
        public String getDisplayString() {
            return JDK_REPOSITORY_DISPLAY_STRING;
        }

        @Override
        public boolean isSearchable() {
            return true;
        }

        @Override
        public void completeModules(ModuleQuery query, ModuleSearchResult result, Overrides overrides) {
            // abort if not JVM
            if (!query.getType().includes(ArtifactContext.JAR))
                return;
            if (query.getMemberName() != null && !query.isMemberSearchPackageOnly()) {
                // We don't support member searches, only package
                return;
            }
            String name = query.getName();
            if (name == null)
                name = "";
            for (String module : sortedModuleNames) {
                if (module.startsWith(name)) {
                    ModuleVersionDetails mvd = getResult(module, query);
                    if (mvd != null) {
                        result.addResult(module, mvd);
                    }
                }
            }
        }

        private String doc(String module) {
            return "JDK module " + module;
        }

        @Override
        public void completeVersions(ModuleVersionQuery query, ModuleVersionResult result, Overrides overrides) {
            // abort if not JVM
            if (!query.getType().includes(ArtifactContext.JAR))
                return;
            if (query.getName() == null || !jdkProvider.isJDKModule(query.getName()))
                return;
            String jdkVersion = jdkProvider.getJDKVersion();
            if (query.getVersion() != null && !query.getVersion().equals(jdkVersion))
                return;
            final ModuleVersionDetails newVersion = result.addVersion(NAMESPACE, query.getName(), jdkVersion);
            newVersion.setDoc(doc(query.getName()));
            newVersion.getArtifactTypes().add(new ModuleVersionArtifact(ArtifactContext.JAR, null, null));
            newVersion.setVersion(jdkVersion);
            newVersion.setRemote(false);
            newVersion.setOrigin(JAVA_ORIGIN);
        }

        @Override
        public void searchModules(ModuleQuery query, ModuleSearchResult result, Overrides overrides) {
            // abort if not JVM
            if (!query.getType().includes(ArtifactContext.JAR))
                return;
            if (query.getMemberName() != null && !query.isMemberSearchPackageOnly()) {
                // We don't support member searches, only package
                return;
            }
            String name = query.getName();
            if (name == null)
                name = "";
            name = name.toLowerCase();
            boolean stopSearching = false;
            int found = 0;
            for (String module : sortedModuleNames) {
                // does it match?
                if (module.contains(name)) {
                    // check if we were already done but were checking for a next results
                    if (stopSearching) {
                        // we already found enough results but were checking if there
                        // were more results to be found for paging, so record that
                        // and stop
                        result.setHasMoreResults(true);
                        return;
                    }
                    ModuleVersionDetails mvd = getResult(module, query);
                    if (mvd != null) {
                        if (query.getStart() == null || found++ >= query.getStart()) {
                            result.addResult(module, mvd);
                            // stop if we're done searching
                            if (query.getStart() != null
                                    && query.getCount() != null
                                    && found >= query.getStart() + query.getCount()) {
                                // we're done, but we want to see if there's at least one more result
                                // to be found so we can tell clients there's a next page
                                stopSearching = true;
                            }
                        }
                    }
                }
            }
        }
        
        private ModuleVersionDetails getResult(String module, ModuleQuery query) {
            ModuleVersionDetails mvd = new ModuleVersionDetails(NAMESPACE, module, jdkProvider.getJDKVersion(), null, null);
            mvd.setDoc(doc(module));
            mvd.getArtifactTypes().add(new ModuleVersionArtifact(ArtifactContext.JAR, null, null));
            mvd.setRemote(false);
            mvd.setOrigin(JAVA_ORIGIN);
            if (query.getMemberName() != null) {
                Set<String> jdkMembers = jdkProvider.getJDKPackages(module);
                if (jdkMembers == null) {
                	// Should not happen, but just in case
                	return null;
                }
                Set<String> matchedMembers = matchNames(jdkMembers, query, true);
                if (matchedMembers.isEmpty()) {
                    // No matching members were found
                    return null;
                }
                mvd.setMembers(matchedMembers);
            }
            return mvd;
        }
    }

    @Override
    public Node findParent(ArtifactContext context) {
        return null;
    }

    @Override
    public OpenNode createParent(ArtifactContext context) {
        return null;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

}
