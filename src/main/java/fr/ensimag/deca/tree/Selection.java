package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class Selection extends AbstractLValue{
    private AbstractExpr expr;
    private AbstractIdentifier fieldIdent;

    public Selection(AbstractExpr expr, AbstractIdentifier fieldIdent){
        Validate.notNull(expr);
        Validate.notNull(fieldIdent);
        this.expr = expr;
        this.fieldIdent = fieldIdent;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        return null;
    }

    @Override
    public void decompile(IndentPrintStream s) {

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s,prefix,false);
        fieldIdent.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        expr.iter(f);

    }
}
