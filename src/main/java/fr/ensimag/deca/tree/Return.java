package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ReturnOperation;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class Return extends AbstractInst{

    private AbstractExpr returnExpr;

    public Return(AbstractExpr returnExpr){
        this.returnExpr = returnExpr;
    }

    public void setReturnExpr (AbstractExpr returnExpr){
        Validate.notNull(returnExpr);
        this.returnExpr = returnExpr;
    }

    @Override
    public void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass, Type returnType) throws ContextualError {
        if (returnType.isVoid()) {
            throw new ContextualError("Return type must not be void", getLocation());
        }
        returnExpr.verifyRValue(compiler, localEnv, currentClass, returnType);
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        returnExpr.decompile(s);
        //s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        returnExpr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // l.170 dans Tree.java explique les paramètres de la fonction
        returnExpr.prettyPrint(s,prefix,true);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        ReturnOperation operator = new ReturnOperation(compiler.getCodeGenBackend(), returnExpr);
        operator.doOperation();
    }
}
