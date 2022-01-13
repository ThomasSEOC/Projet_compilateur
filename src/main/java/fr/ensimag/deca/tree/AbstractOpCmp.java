package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BinaryBoolOperation;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

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
	Type typeLOp = lOp.getType();
	Type typeROp = rOp.getType();
	if ((typeLOp.isInt() || typeLOp.isFloat()) && (typeLOp.isInt() || typeLOp.isFloat())) {
	    Type boolType = new BooleanType(compiler.getSymbolTable().create("boolean"));
	    setType(boolType);
	    return boolType;
	}
	throw new ContextualError("Both binary arithmetic operators need to be either an int or a float", getLocation());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        BinaryBoolOperation operator = new BinaryBoolOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }
}
