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
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    AbstractExpr op = getOperand();
	Type typeOperand = op.getType();
	if (typeOperand.isBoolean()) {
	    return typeOperand;
	}
	throw new ContextualError("not(" + op + ") : " + op + " is not a boolean",getLocation());
    }

    
    @Override
    protected String getOperatorName() {
        return "!";
    }
}

