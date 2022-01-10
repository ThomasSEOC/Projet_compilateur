package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class Return extends AbstractExpr{

    private  AbstractExpr returnExpr;

    public Return(AbstractExpr returnExpr){
        this.returnExpr = returnExpr;
    }

    public void setReturnExpr (AbstractExpr returnExpr){
        Validate.notNull(returnExpr);
        this.returnExpr = returnExpr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        returnExpr.decompile(s);
        s.print(";");
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

}
