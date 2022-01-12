package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;

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
        else if (getExpression() instanceof BooleanLiteral) {
            BooleanLiteral expr = (BooleanLiteral) getExpression();
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(expr.getValue());
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
        else if (getExpression() instanceof StringLiteral) {
            StringLiteral expr = (StringLiteral) getExpression();
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateString(expr.getValue()));
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
    }


}
