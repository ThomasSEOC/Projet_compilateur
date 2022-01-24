package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * class responsible for literal operations
 */
public class LiteralOperation extends AbstractOperation {

    /**
     * constructor for {@link LiteralOperation}
     * @param backend global code generation backend
     * @param expression expression related to operation
     */
    public LiteralOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * method called to generate code for literal usage
     */
    @Override
    public void doOperation() {
        // separate according to literal type
        if (getExpression() instanceof IntLiteral) {
            // cast to IntLiteral
            IntLiteral expr = (IntLiteral) getExpression();

            // request integer immediate virtual register
            VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(expr.getValue()));

            // set data type
            r.setInt();

            // push to operation stack
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
    }

    /**
     * method called to generate code form literal print
     */
    @Override
    public void print() {
        // separate according to literal type
        if (getExpression() instanceof IntLiteral) {
            // cast to IntLiteral
            IntLiteral expr = (IntLiteral) getExpression();

            // load value into R1 and print it
            getCodeGenBackEnd().addInstruction(new LOAD(new ImmediateInteger(expr.getValue()), GPRegister.getR(1)));
            getCodeGenBackEnd().addInstruction(new WINT());
        }
        else if (getExpression() instanceof FloatLiteral) {
            FloatLiteral expr = (FloatLiteral) getExpression();
            getCodeGenBackEnd().addInstruction(new LOAD(new ImmediateFloat(expr.getValue()), GPRegister.getR(1)));

            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().addInstruction(new WFLOATX());
            }
            else {
                getCodeGenBackEnd().addInstruction(new WFLOAT());
            }
        }
        else if (getExpression() instanceof StringLiteral) {
            StringLiteral expr = (StringLiteral) getExpression();
            String toDisplay = expr.getValue().substring(1, expr.getValue().length()-1);
            getCodeGenBackEnd().addInstruction(new WSTR(new ImmediateString(toDisplay)));
        }
    }


}
