package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.FieldSelectOperation;
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

    public AbstractExpr getExpr() {
        return expr;
    }

    public AbstractIdentifier getFieldIdent() {
        return fieldIdent;
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

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        FieldSelectOperation operator = new FieldSelectOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
        operator.print();
    }


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        FieldSelectOperation operator = new FieldSelectOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }
}
