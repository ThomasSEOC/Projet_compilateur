package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BinaryBoolOperation;
import fr.ensimag.deca.codegen.UnaryMinusOperation;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.opti.Constant;

/**
 *
 * @author gl54
 * @date 01/01/2022
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        AbstractExpr lOp = getLeftOperand();
        AbstractExpr rOp = getRightOperand();
        lOp.verifyExpr(compiler, localEnv, currentClass);
        rOp.verifyExpr(compiler, localEnv, currentClass);
        Type typeLOp = lOp.getType();
        Type typeROp = rOp.getType();

        // Verify if the two operands are booleans
        if (typeLOp.isBoolean() && typeROp.isBoolean()) {
            setType(typeLOp);
            return typeLOp;
        }
        throw new ContextualError("Both binary boolean operators need to be a boolean", getLocation());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        BinaryBoolOperation operator = new BinaryBoolOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    public Constant getConstant(DecacCompiler compiler) {
        BinaryBoolOperation operator = new BinaryBoolOperation(compiler.getCodeGenBackend(), this);
        return operator.getConstant(compiler);
    }
}
