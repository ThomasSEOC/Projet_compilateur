package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

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
        s.print("{");
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
}
