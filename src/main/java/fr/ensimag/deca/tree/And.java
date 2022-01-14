package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.AssignOperation;

/**
 *
 * @author gl54
 * @date 01/01/2022
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        AssignOperation operator = new AssignOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }
}
