package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.codegen.LiteralOperation;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 *
 * @author gl54
 * @date 01/01/2022
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type booleanType = new BooleanType(compiler.getSymbolTable().create("boolean"));
        this.setType(booleanType);
        return booleanType;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        LiteralOperation operator = new LiteralOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        LiteralOperation operator = new LiteralOperation(compiler.getCodeGenBackend(), this);
        operator.print();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

    //Not implemented yet
    /*@Override
    protected void codeGenInst(DecacCompiler compiler) {
        AssignOperation operator = new AssignOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }*/

    @Override
    public Constant getConstant(DecacCompiler compiler) {
        return new Constant(value);
    }
}
