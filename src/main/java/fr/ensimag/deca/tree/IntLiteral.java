package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.LiteralOperation;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;

import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl54
 * @date 01/01/2022
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type intType = new IntType(compiler.getSymbolTable().create("int"));
        this.setType(intType);
        return intType;
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
//        compiler.addInstruction(new LOAD(new ImmediateInteger(value), GPRegister.getR(1)));
//        compiler.addInstruction(new WINT());
    }

    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
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
    public Constant getConstant(DecacCompiler compiler) {
        return new Constant(value);
    }

}
