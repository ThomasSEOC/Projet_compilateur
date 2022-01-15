package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BinaryArithmOperation;
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
		lOp.verifyExpr(compiler, localEnv, currentClass);
		rOp.verifyExpr(compiler, localEnv, currentClass);
		Type typeLOp = lOp.getType();
		Type typeROp = rOp.getType();
		if ((typeLOp.isInt() || typeLOp.isFloat()) && (typeROp.isInt() || typeROp.isFloat())) { // vérifie que les deux opérandes sont soit des int soit des float
			if (typeLOp.isInt() && typeROp.isInt()) {
				setType(typeLOp);
				return typeLOp; //si les 2 sont des int, retourne int
			}
			if (typeLOp.isFloat()) {
				throw new UnsupportedOperationException("ça va buggé ce truc");
				//return typeLOp; //si l'opérande de gauche est un flottant, retourne un flottant
			}
			throw new UnsupportedOperationException("ça va buggé ce truc aussi");
			//return typeROp; //si les opérandes ne sont pas toutes les deux des int et que l'opérande de gauche n'est pas un float, alors celle de droite l'est
		}

		throw new ContextualError("Both binary arithmetic operators need to be either an int or a float", getLocation());
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        BinaryArithmOperation operator = new BinaryArithmOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
        operator.print();
    }
}
