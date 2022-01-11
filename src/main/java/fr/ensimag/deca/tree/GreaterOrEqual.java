package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.codegen.BinaryBoolOperation;

/**
 * Operator "x >= y"
 * 
 * @author gl54
 * @date 01/01/2022
 */
public class GreaterOrEqual extends AbstractOpIneq {

    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">=";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        BinaryBoolOperation operator = new BinaryBoolOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

}
