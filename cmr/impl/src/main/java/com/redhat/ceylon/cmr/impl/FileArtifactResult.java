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

import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.model.cmr.Repository;
import com.redhat.ceylon.model.cmr.RepositoryException;

/**
 * Existing file.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class FileArtifactResult extends AbstractCeylonArtifactResult {
    private final File file;
    private final String repositoryDisplayString;

    protected FileArtifactResult(Repository repository, RepositoryManager manager, String name, String version,
            File file, String repositoryDisplayString) {
        super(repository, manager, name, version);
        this.file = file;
        this.repositoryDisplayString = repositoryDisplayString;
    }

    protected File artifactInternal() throws RepositoryException {
        return file;
    }

    @Override
    public String repositoryDisplayString() {
        return repositoryDisplayString;
    }
}

