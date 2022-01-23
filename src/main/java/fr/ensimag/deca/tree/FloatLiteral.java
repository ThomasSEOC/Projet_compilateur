package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.LiteralOperation;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import org.apache.commons.lang.Validate;

/**
 * Single precision, floating-point literal
 *
 * @author gl54
 * @date 01/01/2022
 */
public class FloatLiteral extends AbstractExpr {

    public float getValue() {
        return value;
    }

    private float value;

    public FloatLiteral(float value) {
        Validate.isTrue(!Float.isInfinite(value),
                "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value),
                "literal values cannot be NaN");
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type floatType = new FloatType(compiler.getSymbolTable().create("float"));
        this.setType(floatType);
        return floatType;
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
        s.print(java.lang.Float.toHexString(value));
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
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
