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
	Type typeOperand = operand.getType();
	if (typeOperand.isBoolean()) {
	    return typeOperand();
	}
	throw new ContextualError("not(" + operand + ") : " + operand + " is not a boolean");
    }

    
    @Override
    protected String getOperatorName() {
        return "!";
    }
}

@Override
    protected String getOperatorName() {
    return "!";
}
}
