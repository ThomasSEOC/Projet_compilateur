package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.opti.ControlFlowGraph;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.HALT;
import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.io.PrintStream;

public class MethodBody extends AbstractMethodBody{

    final private ListDeclVar vars;
    final private ListInst insts;

    public MethodBody(ListDeclVar vars, ListInst insts){
        Validate.notNull(vars);
        Validate.notNull(insts);
        this.insts = insts;
        this.vars = vars;
    }

    protected void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        vars.verifyListDeclVariable(compiler, localEnv, currentClass);
        insts.verifyListInst(compiler, localEnv, currentClass, returnType);
    }


    @Override
    public void decompile(IndentPrintStream s){
        s.println("{");
        s.indent();
        vars.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        vars.prettyPrint(s,prefix,false);
        insts.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f){
        insts.iter(f);
        vars.iter(f);
    }


    @Override
    public void codeGen(DecacCompiler compiler) {
        if (compiler.getCompilerOptions().getOptimize() == 2) {
            // create control flow graph;
            ControlFlowGraph graph = new ControlFlowGraph(compiler, vars, insts, true);

            graph.codeGen();
        }
        else {
            vars.codeGenListDeclVar(compiler);
            insts.codeGenListInst(compiler);
        }
    }
}
