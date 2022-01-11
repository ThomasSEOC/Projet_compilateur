package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.codegen.BinaryArithmOperation;
import org.graalvm.compiler.lir.aarch64.AArch64ControlFlow;

/**
 * @author gl54
 * @date 01/01/2022
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
 

    @Override
    protected String getOperatorName() {
        return "+";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        BinaryArithmOperation operator = new BinaryArithmOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }
}
