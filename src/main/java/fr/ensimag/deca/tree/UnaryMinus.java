package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.codegen.UnaryMinusOperation;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

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
	Type typeOperand = op.getType();
	if (typeOperand.isInt() || typeOperand.isFloat()) {
	    return typeOperand;
	}
	throw new ContextualError("-(" + op + ") : " + op + " is neither an int or a float", getLocation());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        UnaryMinusOperation operator = new UnaryMinusOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

}
