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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.CmrRepository;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.spi.Node;
import com.redhat.ceylon.cmr.spi.OpenNode;
import com.redhat.ceylon.model.cmr.ArtifactResult;
import com.redhat.ceylon.model.cmr.ArtifactResultType;
import com.redhat.ceylon.model.cmr.RepositoryException;

/**
 * Maven repository.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MavenRepository extends AbstractRepository {
    public static final String NAMESPACE = "maven";

    protected MavenRepository(OpenNode root) {
        super(root);
    }

    @Override
    public String[] getArtifactNames(ArtifactContext context) {
        String name = context.getName();
        final int p = name.contains(":") ? name.lastIndexOf(":") : name.lastIndexOf(".");

        return getArtifactNames(p >= 0 ? name.substring(p + 1) : name, context.getVersion(), context.getSuffixes());
    }

    @Override
    protected ArtifactResult getArtifactResultInternal(RepositoryManager manager, final Node node) {
        ArtifactContext context = ArtifactContext.fromNode(node);
        return new MavenArtifactResult(this, manager, context.getName(), context.getVersion(), node);
    }

    @Override
    public String getDisplayString() {
        return "[Maven] " + super.getDisplayString();
    }

    private static class MavenArtifactResult extends AbstractCeylonArtifactResult {
        private Node node;

        private MavenArtifactResult(CmrRepository repository, RepositoryManager manager, String name, String version,
                Node node) {
            super(repository, manager, name, version);
            this.node = node;
        }
        
        @Override
        public String namespace() {
            return NAMESPACE;
        }
        
        @Override
        public ArtifactResultType type() {
            return ArtifactResultType.MAVEN;
        }

        @Override
        protected File artifactInternal() throws RepositoryException {
            try {
                return node.getContent(File.class);
            } catch (IOException e) {
                throw new RepositoryException(e);
            }
        }

        @Override
        public List<ArtifactResult> dependencies() throws RepositoryException {
            return Collections.emptyList(); // dunno how to grab deps
        }
        
        @Override
        public String repositoryDisplayString() {
            return NodeUtils.getRepositoryDisplayString(node);
        }
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    protected List<String> getDefaultParentPathInternal(ArtifactContext context) {
        return getParentPath(context);
    }
    
    public static List<String> getParentPath(ArtifactContext context) {
        final String name = context.getName();
        final int p = name.contains(":") ? name.lastIndexOf(":") : name.lastIndexOf(".");
        final List<String> tokens = new ArrayList<String>();
        if (p == -1) {
            tokens.addAll(Arrays.asList(name.split("\\.")));
        } else {
            tokens.addAll(Arrays.asList(name.substring(0, p).split("\\.")));
            tokens.add(name.substring(p + 1));
        }
        final String version = context.getVersion();
        if (!RepositoryManager.DEFAULT_MODULE.equals(name) && version != null)
            tokens.add(version); // add version
        return tokens;
    }

}
