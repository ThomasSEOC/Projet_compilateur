package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.HALT;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.deca.context.*;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

/**
 * @author gl54
 * @date 01/01/2022
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        // A FAIRE: Appeler méthodes "verify*" de ListDeclVarSet et ListInst.
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).

        declVariables.verifyListDeclVariable(compiler, compiler.getEnvPredef(), null);
        insts.verifyListInst(compiler, compiler.getEnvPredef(), null, compiler.getEnvPredef().get(compiler.getSymbolTable().getMap().get("void")).getType());
        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // A FAIRE: traiter les déclarations de variables.

        declVariables.codeGenListDeclVar(compiler);

        compiler.getCodeGenBackend().addComment("Beginning of main instructions:");
        insts.codeGenListInst(compiler);

        // end of the program
        compiler.getCodeGenBackend().addInstruction(new HALT());

        compiler.getCodeGenBackend().getStartupManager().generateStartupCode();

        compiler.getCodeGenBackend().addCommentFirst("start main program");
        compiler.getCodeGenBackend().addCommentFirst("###############################################################");

        compiler.getCodeGenBackend().writeInstructions();
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
