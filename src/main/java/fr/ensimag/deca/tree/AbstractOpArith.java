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
		Type typeLOp = lOp.verifyExpr(compiler, localEnv, currentClass);
		Type typeROp = rOp.verifyExpr(compiler, localEnv, currentClass);

		//On vérifie si les deux opérandes sont soit des int soit des float
		//Quatre cas sont possibles
		if ((typeLOp.isInt() || typeLOp.isFloat()) && (typeROp.isInt() || typeROp.isFloat())) {

  		        //Si les 2 sont des int, retourne int
			if (typeLOp.isInt() && typeROp.isInt()) {
				setType(typeLOp);
				return typeLOp; 
			}
			//Si les 2 sont des float, retourne float
			else if (typeLOp.isFloat() && typeROp.isFloat()) {
				setType(typeLOp);
				return typeLOp;
			}			
			//Si l'opérande de gauche est un flottant, convertit celle de droite en ConvFloat et retourne un flottant
			else if (typeLOp.isFloat() && typeROp.isInt()) {
				setType(typeLOp);
				setRightOperand(new ConvFloat(getRightOperand()));
				getRightOperand().verifyExpr(compiler, localEnv, currentClass);
				return typeLOp;
			}
			//Si l'opérande de droite est un flottant, convertit celle de gauche en ConvFloat et retourne un flottant
			else {
				setType(typeROp);
				setLeftOperand(new ConvFloat(getLeftOperand()));
				getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
				return typeROp;
			}
		}

		throw new ContextualError("Both binary arithmetic operators need to be either an int or a float", getLocation());
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        BinaryArithmOperation operator = new BinaryArithmOperation(compiler.getCodeGenBackend(), this);
        operator.print();
    }


}
