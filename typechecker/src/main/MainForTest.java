package main;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.ceylon.CeylonUtils;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.io.ClosableVirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.cmr.impl.LeakingLogger;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Module;

import java.io.File;

/**
 * Some hack before a proper unit test harness is put in place
 *
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 */
public class MainForTest {
    /**
     * Files that are not under a proper module structure are 
     * placed under a <nomodule> module.
     */
    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        
        RepositoryManager repositoryManager = CeylonUtils.repoManager()
                .systemRepo("../dist/dist/repo")
                .outRepo("test/modules")
                .logger(new LeakingLogger())
                .buildManager();
        
        TypeChecker typeChecker = new TypeCheckerBuilder()
                .statistics(true)
                .verbose(false)
                .addSrcDirectory( new File("test/main") )
                .setRepositoryManager(repositoryManager)
                .getTypeChecker();
        typeChecker.process();
        int errors = typeChecker.getErrors();
        Tree.CompilationUnit compilationUnit = 
                typeChecker.getPhasedUnitFromRelativePath(
                        "ceylon/language/Object.ceylon")
                    .getCompilationUnit();
        if ( compilationUnit == null ) {
            throw new RuntimeException(
                    "Failed to pass getCompilationUnitFromRelativePath for files in .src");
        }
        compilationUnit = 
                typeChecker.getPhasedUnitFromRelativePath(
                        "capture/Capture.ceylon")
                    .getCompilationUnit();
        if ( compilationUnit == null ) {
            throw new RuntimeException(
                    "Failed to pass getCompilationUnitFromRelativePath for files in real src dir");
        }
        compilationUnit = 
                typeChecker.getPhasedUnitFromRelativePath(
                        "com/redhat/sample/multisource/Boo.ceylon")
                    .getCompilationUnit();
        Module module = compilationUnit.getUnit().getPackage().getModule();
        if ( !"com.redhat.sample.multisource".equals( module.getNameAsString() ) ) {
            throw new RuntimeException("Unable to extract module name");
        }
        if ( !"0.2".equals( module.getVersion() ) ) {
            throw new RuntimeException("Unable to extract module version");
        }
        typeChecker = new TypeCheckerBuilder()
                .verbose(false)
                .addSrcDirectory( new File("test/main/capture") )
                .setRepositoryManager(repositoryManager)
                .getTypeChecker();
        typeChecker.process();
        errors += typeChecker.getErrors();
        compilationUnit = 
                typeChecker.getPhasedUnitFromRelativePath(
                        "Capture.ceylon")
                    .getCompilationUnit();
        if ( compilationUnit == null ) {
            throw new RuntimeException(
                    "Failed to pass getCompilationUnitFromRelativePath for top level files (no package) in real src dir");
        }

        typeChecker = new TypeCheckerBuilder()
                .verbose(false)
                .addSrcDirectory( new File("test/moduledep1") )
                .addSrcDirectory( new File("test/moduledep2") )
                .addSrcDirectory( new File("test/moduletest") )
                .addSrcDirectory( new File("test/restricted") )
                .setRepositoryManager(repositoryManager)
                .getTypeChecker();
        typeChecker.process();
        errors += typeChecker.getErrors();

        ClosableVirtualFile latestZippedLanguageSourceFile = 
                MainHelper.getLatestZippedLanguageSourceFile();
        typeChecker = new TypeCheckerBuilder()
                .verbose(false)
                .addSrcDirectory( latestZippedLanguageSourceFile )
                .setRepositoryManager(repositoryManager)
                .getTypeChecker();
        typeChecker.process();
        errors += typeChecker.getErrors();
        latestZippedLanguageSourceFile.close();
        System.out.println("Tests took " + ( (System.nanoTime()-start) / 1000000 ) + " ms");
        
        if (errors > 0) {
            System.exit(1);
        }
    }
}
