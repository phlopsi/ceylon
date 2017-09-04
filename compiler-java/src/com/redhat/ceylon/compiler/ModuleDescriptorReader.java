/*
 * Copyright Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the authors tag. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License version 2.
 * 
 * This particular file is subject to the "Classpath" exception as provided in the 
 * LICENSE file that accompanied this code.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.redhat.ceylon.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.api.RepositoryManagerBuilder;
import com.redhat.ceylon.common.ModuleDescriptorReader.NoSuchModuleException;
import com.redhat.ceylon.common.config.DefaultToolOptions;
import com.redhat.ceylon.common.log.Logger;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VFS;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.model.typechecker.model.Annotation;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;

class ModuleDescriptorReader {
    
    private static final class NullLogger implements Logger {
        @Override
        public void error(String str) {
            // Don't care
        }

        @Override
        public void warning(String str) {
            // Don't care
        }

        @Override
        public void info(String str) {
            // Don't care
        }

        @Override
        public void debug(String str) {
            // Don't care
        }
    }
    
    private final Module moduleDescriptor;

    public ModuleDescriptorReader(String moduleName, File srcDir) throws NoSuchModuleException {
        RepositoryManagerBuilder builder = new RepositoryManagerBuilder(new NullLogger(), DefaultToolOptions.getDefaultOffline(), (int)DefaultToolOptions.getDefaultTimeout(), DefaultToolOptions.getDefaultProxy());
        RepositoryManager repoManager = builder.buildRepository();
        VFS vfs = new VFS();
        Context context = new Context(repoManager, vfs);
        PhasedUnits pus = new PhasedUnits(context);
        List<String> name = ModuleManager.splitModuleName(moduleName);
        ModuleSourceMapper moduleSourceMapper = pus.getModuleSourceMapper();
        ModuleManager moduleManager = pus.getModuleManager();
        if(Module.DEFAULT_MODULE_NAME.equals(moduleName)){
            // visit every folder and skip modules
            boolean exists = findDefaultModuleSource(srcDir);
            if(!exists)
                throw new NoSuchModuleException("No source found for default module");
        }else{
            visitModule(vfs, pus, name, srcDir, vfs.getFromFile(srcDir), moduleSourceMapper);
        }
        for (PhasedUnit pu : pus.getPhasedUnits()) {
            pu.visitSrcModulePhase();
        }
        for (PhasedUnit pu : pus.getPhasedUnits()) {
            pu.visitRemainingModulePhase();
        }
        this.moduleDescriptor = moduleManager.getOrCreateModule(name, null);
    }
    
    private void visitModule(VFS vfs, PhasedUnits pus, List<String> name, File srcDir, VirtualFile virtualSourceDirectory, ModuleSourceMapper moduleSourceMapper) throws NoSuchModuleException {
        for(String part : name){
            File child = new File(srcDir, part);
            if(child.exists() && child.isDirectory()){
                moduleSourceMapper.push(part);
                srcDir = child;
            }else{
                throw new NoSuchModuleException("Failed to find module name part "+part+" of "+name+" in "+srcDir);
            }
        }
        File moduleFile = new File(srcDir, ModuleManager.MODULE_FILE);
        if(moduleFile.exists()){
            moduleSourceMapper.visitModuleFile();
            pus.parseUnit(vfs.getFromFile(moduleFile), virtualSourceDirectory);
        }else{
            throw new NoSuchModuleException("No module file in "+srcDir);
        }
    }

    private boolean findDefaultModuleSource(File sourceFile) {
        if(sourceFile.isDirectory()){
            File moduleFile = new File(sourceFile, ModuleManager.MODULE_FILE);
            // skip modules entirely
            if(moduleFile.exists())
                return false;
            // recurse down normal folders
            for(File f : sourceFile.listFiles()){
                boolean found = findDefaultModuleSource(f);
                if(found)
                    return true;
            }
            return false;
        }else{
            String name = sourceFile.getName().toLowerCase();
            // did we find a source file?
            return name.endsWith(".ceylon")
                || name.endsWith(".java")
                || name.endsWith(".js");
        }
    }

    /**
     * Gets the module version
     * @return The module version, or null if no version could be found
     */
    public String getModuleVersion() {
        return moduleDescriptor.getVersion();
    }
    
    /**
     * Gets the module name
     * @return The module name, or null if no name could be found
     */
    public String getModuleName() {
        return moduleDescriptor.getNameAsString();
    }
    
    /**
     * Gets the module label
     * @return The module label, or null if no label could be found
     */
    public String getModuleLabel() {
        for (Annotation ann : moduleDescriptor.getAnnotations()) {
            if (ann.getName().equals("label")) {
                List<String> args = ann.getPositionalArguments();
                if (args != null && !args.isEmpty()) {
                    return removeQuotes(args.get(0));
                }
            }
        }
        return null;
    }    
    
    /**
     * Gets the module backends
     * @return The name of the supported backend
     */
    public ArrayList<String> getModuleBackends() {
        ArrayList<String> backends = new ArrayList<String>();
        for (Annotation ann : moduleDescriptor.getAnnotations()) {
            if (ann.getName().equals("native")) {
                for (String backend : ann.getPositionalArguments()) {
                    backends.add(removeQuotes(backend));
                }
            }
        }
        return backends;
    }
    
    /**
     * Gets the module license
     * @return The module version, or null if no version could be found
     */
    public String getModuleLicense() {
        for (Annotation ann : moduleDescriptor.getAnnotations()) {
            if (ann.getName().equals("license")) {
                List<String> args = ann.getPositionalArguments();
                if (args != null && !args.isEmpty()) {
                    return removeQuotes(args.get(0));
                }
            }
        }
        return null;
    }
    
    private String removeQuotes(String string) {
        return string.replaceAll("^[\\\"]", "").replaceAll("[\\\"]$", "");
    }

    /**
     * Gets the module authors
     * @return The list of module authors, or empty list of no authors could be found
     */
    public List<String> getModuleAuthors() {
        ArrayList<String> authors = new ArrayList<String>();
        for (Annotation ann : moduleDescriptor.getAnnotations()) {
            if (ann.getName().equals("by")) {
                for (String author : ann.getPositionalArguments()) {
                    authors.add(removeQuotes(author));
                }
            }
        }
        return authors;
    }

    /**
     * Gets the module imports. Format is [repoType, name, version, optional, shared]
     */
    public List<Object[]> getModuleImports(){
        ArrayList<Object[]> imports = new ArrayList<Object[]>();
        for(ModuleImport dep : moduleDescriptor.getImports()){
            imports.add(new Object[]{
                    dep.getNamespace(),
                    dep.getModule().getNameAsString(), 
                    dep.getModule().getVersion(),
                    dep.isOptional(),
                    dep.isExport(),
                    dep.getNativeBackends()
            });
        }
        return imports;
    }
    
    public String getModuleGroupId(){
        return moduleDescriptor.getGroupId();
    }

    public String getModuleArtifactId(){
        return moduleDescriptor.getArtifactId();
    }
}
