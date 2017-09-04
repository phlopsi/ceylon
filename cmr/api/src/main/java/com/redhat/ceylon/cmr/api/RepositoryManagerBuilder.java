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

package com.redhat.ceylon.cmr.api;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.Proxy;
import java.util.List;

import com.redhat.ceylon.cmr.spi.ContentTransformer;
import com.redhat.ceylon.cmr.spi.MergeStrategy;
import com.redhat.ceylon.common.log.Logger;

/**
 * RepositoryManager builder API.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class RepositoryManagerBuilder {
    private final static String PROP_CEYLON_MODULE_RESOLVER_BUILDER = "ceylon.module.resolver.builder";
    private final static String DEFAULT_DELEGATE = "com.redhat.ceylon.cmr.impl.RepositoryManagerBuilderImpl";

    private RepositoryManagerBuilder delegate;

    protected RepositoryManagerBuilder() {
    }

    public RepositoryManagerBuilder(Logger log) {
        this(log, true, 0, Proxy.NO_PROXY, (String)null);
    }

    public RepositoryManagerBuilder(Logger log, String currentDirectory) {
        this(log, true, 0, Proxy.NO_PROXY, currentDirectory);
    }

    public RepositoryManagerBuilder(Logger log, boolean offline, int timeout, Proxy proxy) {
        this(log, offline, timeout, proxy, null, null);
    }

    public RepositoryManagerBuilder(Logger log, boolean offline, int timeout, Proxy proxy, String currentDirectory) {
        this(log, offline, timeout, proxy, currentDirectory, null);
    }

    public RepositoryManagerBuilder(Logger log, boolean offline, int timeout, Proxy proxy, Overrides overrides) {
        this(log, offline, timeout, proxy, null, overrides, true);
    }

    public RepositoryManagerBuilder(Logger log, boolean offline, int timeout, Proxy proxy, String currentDirectory, Overrides overrides) {
        this(log, offline, timeout, proxy, currentDirectory, overrides, true);
    }
    
    public RepositoryManagerBuilder(Logger log, boolean offline, int timeout, Proxy proxy, Overrides overrides, boolean upgradeDist) {
        this(log, offline, timeout, proxy, null, overrides, upgradeDist);
    }
    
    public RepositoryManagerBuilder(Logger log, boolean offline, int timeout, Proxy proxy, String currentDirectory, Overrides overrides, boolean upgradeDist) {
        try {
            Constructor<? extends RepositoryManagerBuilder> ctor = getDelegateClass().getConstructor(Logger.class, boolean.class, int.class, Proxy.class, String.class, Overrides.class, boolean.class);
            delegate = ctor.newInstance(log, offline, timeout, proxy, currentDirectory, overrides, upgradeDist);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable tex = e.getTargetException();
            if (tex instanceof RuntimeException) {
                throw (RuntimeException)tex;
            } else {
                throw new IllegalArgumentException(e);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public RepositoryManagerBuilder(File mainRepository, Logger log, boolean offline, int timeout, Proxy proxy) {
        this(mainRepository, log, offline, timeout, proxy, (String)null);
    }

    public RepositoryManagerBuilder(File mainRepository, Logger log, boolean offline, int timeout, Proxy proxy, String currentDirectory) {
        this(mainRepository, log, offline, timeout, proxy, currentDirectory, null);
    }

    public RepositoryManagerBuilder(File mainRepository, Logger log, boolean offline, int timeout, Proxy proxy, String currentDirectory, Overrides overrides) {
        this(mainRepository, log, offline, timeout, proxy, currentDirectory, overrides, true);
    }
    
    public RepositoryManagerBuilder(File mainRepository, Logger log, boolean offline, int timeout, Proxy proxy, Overrides overrides) {
        this(mainRepository, log, offline, timeout, proxy, null, overrides, true);
    }
    
    public RepositoryManagerBuilder(File mainRepository, Logger log, boolean offline, int timeout, Proxy proxy, Overrides overrides, boolean upgradeDist) {
        this(mainRepository, log, offline, timeout, proxy, null, overrides, upgradeDist);
    }

    public RepositoryManagerBuilder(File mainRepository, Logger log, boolean offline, int timeout, Proxy proxy, String currentDirectory, Overrides overrides, boolean upgradeDist) {
        try {
            Constructor<? extends RepositoryManagerBuilder> ctor = getDelegateClass().getConstructor(File.class, Logger.class, boolean.class, int.class, Proxy.class, String.class, Overrides.class, boolean.class);
            delegate = ctor.newInstance(mainRepository, log, offline, timeout, proxy, currentDirectory, overrides, upgradeDist);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable tex = e.getTargetException();
            if (tex instanceof RuntimeException) {
                throw (RuntimeException)tex;
            } else {
                throw new IllegalArgumentException(tex);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends RepositoryManagerBuilder> getDelegateClass() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        final String builderClassName = System.getProperty(PROP_CEYLON_MODULE_RESOLVER_BUILDER);
        if (builderClassName != null && builderClassName.length() > 0) {
            Class<?> clazz = classLoader.loadClass(builderClassName);
            if (RepositoryManagerBuilder.class.isAssignableFrom(clazz) == false) {
                throw new IllegalArgumentException(String.format("%s is not an instance of RepositoryManagerBuilder!", clazz.getName()));
            }
            return (Class<? extends RepositoryManagerBuilder>) clazz;
        } else {
            return (Class<? extends RepositoryManagerBuilder>) classLoader.loadClass(DEFAULT_DELEGATE);
        }
    }

    protected RepositoryManagerBuilder getDelegate() {
        if (delegate == null)
            throw new IllegalArgumentException("Missing method impl / override!");
        return delegate;
    }

    public RepositoryBuilder repositoryBuilder() {
        return getDelegate().repositoryBuilder();
    }

    public RepositoryManagerBuilder mergeStrategy(MergeStrategy strategy) {
        getDelegate().mergeStrategy(strategy);
        return this;
    }

    public RepositoryManagerBuilder contentTransformer(ContentTransformer transformer) {
        getDelegate().contentTransformer(transformer);
        return this;
    }

    public RepositoryManagerBuilder cacheContent() {
        getDelegate().cacheContent();
        return this;
    }

    public RepositoryManagerBuilder addRepository(CmrRepository external) {
        getDelegate().addRepository(external);
        return this;
    }

    public List<String> getRepositoriesDisplayString() {
        return getDelegate().getRepositoriesDisplayString();
    }

    public RepositoryManager buildRepository() {
        return getDelegate().buildRepository();
    }

    public boolean hasMavenRepository() {
        return getDelegate().hasMavenRepository();
    }

    public static Overrides parseOverrides(String overridesFileName) {
        if(overridesFileName != null){
            try {
                return Overrides.getDistOverrides().append(overridesFileName);
            }catch(IllegalArgumentException x){
                throw x;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }
}
