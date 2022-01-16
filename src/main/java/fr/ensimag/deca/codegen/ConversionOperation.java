package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;

/**
 * Class responsible for type conversion
 */
public class ConversionOperation extends AbstractOperation {
    /**
     * constructor for ConversionOperation
     * @param backend global code generation backend
     * @param expression expression related to operation
     */
    public ConversionOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * method called to generate code for conversion
     */
    @Override
    public void doOperation() {
        if (getExpression() instanceof ConvFloat) {
            // cast to ConvFloat
            ConvFloat expr = (ConvFloat) getExpression();

            // generate code for operand
            AbstractExpr[] operand = {expr.getOperand()};
            ListCodeGen(operand);

            // get result out of operation stack
            VirtualRegister r = getCodeGenBackEnd().getContextManager().operationStackPop();

            // convert it
            getCodeGenBackEnd().getCompiler().addInstruction(new FLOAT(r.requestPhysicalRegister(), r.requestPhysicalRegister()));

            // push result to operation stack
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
        else {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    /**
     * method called to generate code for conversion print
     */
    @Override
    public void print() {
        if (getExpression() instanceof ConvFloat) {
            // do operation
            doOperation();

            // get result
            VirtualRegister r = getCodeGenBackEnd().getContextManager().operationStackPop();

            // print it according to Hex
            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().getCompiler().addInstruction(new WFLOATX());
            }
            else {
                getCodeGenBackEnd().getCompiler().addInstruction(new WFLOAT());
            }

            // free virtual register
            r.destroy();
        }
    }
}
