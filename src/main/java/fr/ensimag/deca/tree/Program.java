package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.codegen.*;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl54
 * @date 01/01/2022
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        this.classes.verifyListClass(compiler);
        this.classes.verifyListClassMembers(compiler);
        this.classes.verifyListClassBody(compiler);
        this.main.verifyMain(compiler);
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        // create codegen backend
        CodeGenBackend backend = compiler.getCodeGenBackend();

        // beginning
        compiler.addComment("Main program");

        if (classes.size() > 0) {
            compiler.addComment("###############################################################");

            // classes declare
            classes.codeGenDeclare(compiler);

            // vtable codegen
            backend.getClassManager().VTableCodeGen();
        }

        // generation of the main program
        compiler.addComment("###############################################################");
        main.codeGenMain(compiler);

        if (classes.size() > 0) {
            // methods codegen
            compiler.addComment("###############################################################");
            backend.getClassManager().methodsCodeGen();
        }

        // errors
        backend.getErrorsManager().addErrors();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
