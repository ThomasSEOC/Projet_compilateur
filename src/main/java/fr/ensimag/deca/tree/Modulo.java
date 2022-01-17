package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.codegen.BinaryArithmOperation;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl54
 * @date 11/01/2022
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {

        super(leftOperand, rightOperand);
    }


    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftOperandType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightOperandType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        //Vérifie si les deux opérandes sont de type int
        if (leftOperandType.isInt() && rightOperandType.isInt()) {
            this.setType(leftOperandType);
            return (leftOperandType);
        } else {
            throw new ContextualError ("There must be two intergers for the Modulo.", this.getLocation());

        }
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        BinaryArithmOperation operator = new BinaryArithmOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

}
