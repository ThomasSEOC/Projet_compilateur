package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.opti.ControlFlowGraph;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.IOException;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.HALT;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.deca.context.*;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;


/**
 * @author gl54
 * @date 01/01/2022
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables, ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        SymbolTable.Symbol voidSymbol =  compiler.getSymbolTable().getMap().get("void");
        declVariables.verifyListDeclVariable(compiler, compiler.getExpPredef(), null);
        insts.verifyListInst(compiler, compiler.getExpPredef(), null, compiler.getTypes().get(voidSymbol).getType());
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // A FAIRE: traiter les déclarations de variables.

        if (compiler.getCompilerOptions().getOptimize() == 2) {
            // create control flow graph;
            ControlFlowGraph graph = new ControlFlowGraph(compiler, declVariables, insts);

            LOG.debug(graph);
            if (compiler.getCompilerOptions().getCreateGraphFile()) {
                try {
                    graph.createDotGraph();
                } catch (IOException ex) {
                    System.out.println("IO error while creating ");
                }
            }
            graph.codeGen();
        }
        else {
            declVariables.codeGenListDeclVar(compiler);
            compiler.getCodeGenBackend().addComment("Beginning of main instructions:");
            insts.codeGenListInst(compiler);
            compiler.getCodeGenBackend().addInstruction(new HALT());
        }


//        compiler.getCodeGenBackend().getStartupManager().generateStartupCode();

        // end of the program
//        compiler.getCodeGenBackend().addInstruction(new HALT());

        compiler.getCodeGenBackend().getStartupManager().generateStartupCode();

//        compiler.getCodeGenBackend().writeInstructions();

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
