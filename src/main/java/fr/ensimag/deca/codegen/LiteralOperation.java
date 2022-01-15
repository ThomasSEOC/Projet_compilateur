package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.*;

public class LiteralOperation extends AbstractOperation {

    public LiteralOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        if (getExpression() instanceof IntLiteral) {
            IntLiteral expr = (IntLiteral) getExpression();
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(expr.getValue()));
            r.setInt();
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
        else if (getExpression() instanceof FloatLiteral) {
            FloatLiteral expr = (FloatLiteral) getExpression();
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateFloat(expr.getValue()));
            r.setFloat();
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
        else if (getExpression() instanceof BooleanLiteral) {
            BooleanLiteral expr = (BooleanLiteral) getExpression();
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(expr.getValue());
            r.setInt();
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
        else if (getExpression() instanceof StringLiteral) {
            StringLiteral expr = (StringLiteral) getExpression();
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateString(expr.getValue()));
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
    }

    @Override
    public void print() {
        if (getExpression() instanceof IntLiteral) {
            IntLiteral expr = (IntLiteral) getExpression();
            getCodeGenBackEnd().getCompiler().addInstruction(new LOAD(new ImmediateInteger(expr.getValue()), GPRegister.getR(1)));
            getCodeGenBackEnd().getCompiler().addInstruction(new WINT());
        }
        else if (getExpression() instanceof FloatLiteral) {
            FloatLiteral expr = (FloatLiteral) getExpression();
            getCodeGenBackEnd().getCompiler().addInstruction(new LOAD(new ImmediateFloat(expr.getValue()), GPRegister.getR(1)));

            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().getCompiler().addInstruction(new WFLOATX());
            }
            else {
                getCodeGenBackEnd().getCompiler().addInstruction(new WFLOAT());
            }
        }
        else if (getExpression() instanceof BooleanLiteral) {
            BooleanLiteral expr = (BooleanLiteral) getExpression();
            if (expr.getValue()) {
                getCodeGenBackEnd().getCompiler().addInstruction(new WSTR(new ImmediateString("True")));
            }
            else {
                getCodeGenBackEnd().getCompiler().addInstruction(new WSTR(new ImmediateString("False")));
            }
        }
        else if (getExpression() instanceof StringLiteral) {
            StringLiteral expr = (StringLiteral) getExpression();
            String toDisplay = expr.getValue().substring(1, expr.getValue().length()-1);
            getCodeGenBackEnd().getCompiler().addInstruction(new WSTR(new ImmediateString(toDisplay)));
        }
    }


}
