package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.FloatLiteral;
import fr.ensimag.deca.tree.IntLiteral;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

public class LiteralOperation extends AbstractOperation {

    public LiteralOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        if (getExpression() instanceof IntLiteral) {
            IntLiteral expr = (IntLiteral) getExpression();
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(expr.getValue()));
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
        else if (getExpression() instanceof FloatLiteral) {
            FloatLiteral expr = (FloatLiteral) getExpression();
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateFloat(expr.getValue()));
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
    }


}
