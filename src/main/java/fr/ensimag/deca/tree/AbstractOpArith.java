package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl54
 * @date 01/01/2022
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
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
	    if (typeLOp.isInt() && typeLOp.isInt()) {
		return typeLOp;
	    }
	    if (typeLOp.isFloat()) {
		return typeLOp;
	    }
	    return typeROp;
	}
	throw new ContextualError("Both binary arithmetic operator need either an int or a float", getLocation());
    }
}
