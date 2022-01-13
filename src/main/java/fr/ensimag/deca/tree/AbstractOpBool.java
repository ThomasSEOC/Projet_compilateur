package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

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
	Type typeLOp = lOp.getType();
	Type typeROp = rOp.getType();
	if (typeLOp.isBoolean() && typeROp.isBoolean()) {
	    return typeLOp;
	}
	throw new ContextualError("Both binary boolean operators need to be a boolean", getLocation());
    }

}
