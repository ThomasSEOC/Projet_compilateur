package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.UnaryMinus;
import fr.ensimag.ima.pseudocode.instructions.OPP;

public class UnaryMinusOperation extends AbstractOperation {
    public UnaryMinusOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        UnaryMinus expr = (UnaryMinus) getExpression();
        AbstractExpr[] operand = {expr.getOperand()};
        ListCodeGen(operand);

        VirtualRegister r1 = getCodeGenBackEnd().getContextManager().operationStackPop();

        getCodeGenBackEnd().getCompiler().addInstruction(new OPP(r1.requestPhysicalRegister(), r1.requestPhysicalRegister()));

        getCodeGenBackEnd().getContextManager().operationStackPush(r1);
    }

}
