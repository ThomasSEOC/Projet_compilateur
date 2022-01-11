package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.codegen.BinaryArithmOperation;

/**
 *
 * @author gl54
 * @date 01/01/2022
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        BinaryArithmOperation operator = new BinaryArithmOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

}
