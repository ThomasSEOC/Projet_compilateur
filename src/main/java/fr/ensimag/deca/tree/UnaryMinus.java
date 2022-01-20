package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.codegen.UnaryMinusOperation;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * @author gl54
 * @date 01/01/2022
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        AbstractExpr op = getOperand();
        op.verifyExpr(compiler, localEnv, currentClass);
        Type typeOperand = op.getType();
        if (typeOperand.isInt() || typeOperand.isFloat()) {
            setType(typeOperand);
            return typeOperand;
        }
        throw new ContextualError("Unary minus operands needs to be either an int or a float", getLocation());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        UnaryMinusOperation operator = new UnaryMinusOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        UnaryMinusOperation operator = new UnaryMinusOperation(compiler.getCodeGenBackend(), this);
        operator.print();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("-");
        getOperand().decompile(s);
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    public Constant getConstant(DecacCompiler compiler) {
        UnaryMinusOperation operator = new UnaryMinusOperation(compiler.getCodeGenBackend(), this);
        return operator.getConstant(compiler);
    }
}
