package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;

public class ConversionOperation extends AbstractOperation {
    public ConversionOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        if (getExpression() instanceof ConvFloat) {
            ConvFloat expr = (ConvFloat) getExpression();

            // récursion
            AbstractExpr[] operand = {expr.getOperand()};
            ListCodeGen(operand);

            VirtualRegister r = getCodeGenBackEnd().getContextManager().operationStackPop();

            getCodeGenBackEnd().getCompiler().addInstruction(new FLOAT(r.requestPhysicalRegister(), r.requestPhysicalRegister()));

            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
    }
}
