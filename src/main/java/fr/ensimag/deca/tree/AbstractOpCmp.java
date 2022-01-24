package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BinaryBoolOperation;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.BooleanType;
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
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
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

        // Verify if the operands are int or float
        if ((typeLOp.isInt() || typeLOp.isFloat()) && (typeROp.isInt() || typeROp.isFloat())) {
            // If one of the two operand is float, put a Convfloat
            if ((typeLOp.isInt() && typeROp.isFloat())) {
                ConvFloat convFloat = new ConvFloat(this.getLeftOperand());
                convFloat.verifyExpr(compiler, localEnv, currentClass);
                this.setLeftOperand(convFloat);
            } else if ((typeLOp.isFloat() && typeROp.isInt())) {
                ConvFloat convFloat = new ConvFloat(this.getRightOperand());
                convFloat.verifyExpr(compiler, localEnv, currentClass);
                this.setRightOperand(convFloat);
            }
        }

        // Verify if the two operands are boolean
        else if (!typeLOp.isBoolean() || !typeROp.isBoolean()){
            throw new ContextualError("Both binary arithmetic operators need to be either an int or a float", getLocation());
        }

        Type boolType = new BooleanType(compiler.getSymbolTable().create("boolean"));
        setType(boolType);
        return boolType;

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
