package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodCallOperation;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class MethodCall extends AbstractExpr{

    final private AbstractExpr expr;
    final private AbstractIdentifier ident;
    final private ListExpr listExpr;

    public MethodCall(AbstractExpr expr, AbstractIdentifier ident, ListExpr listExpr){
        Validate.notNull(expr);
        Validate.notNull(ident);
        Validate.notNull(listExpr);
        this.expr = expr;
        this.ident = ident;
        this.listExpr = listExpr;
    }

    public AbstractExpr getExpr() {
        return expr;
    }

    public AbstractIdentifier getIdent() {
        return ident;
    }

    public ListExpr getListExpr() {
        return listExpr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        return null;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(".");
        ident.decompile(s);
        s.print("(");
        listExpr.decompile(s);
        s.print(")");

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s,prefix,false);
        ident.prettyPrint(s,prefix,false);
        listExpr.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        ident.iter(f);
        listExpr.iter(f);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        MethodCallOperation operator = new MethodCallOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
        operator.print();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        MethodCallOperation operator = new MethodCallOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }
}
