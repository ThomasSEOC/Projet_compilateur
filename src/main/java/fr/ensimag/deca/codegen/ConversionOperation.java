package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
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
        // try do evaluate it as a constant
        boolean opti = (getCodeGenBackEnd().getCompiler().getCompilerOptions().getOptimize() > 0);
        Constant constant = null;
        if (opti) {
            constant = getConstant(getCodeGenBackEnd().getCompiler());
        }
        if (constant != null) {
            VirtualRegister result;
            if (constant.getIsFloat()) {
                result = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateFloat(constant.getValueFloat()));
            }
            else {
                result = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(constant.getValueInt()));
            }
            getCodeGenBackEnd().getContextManager().operationStackPush(result);
            return;
        }

        if (getExpression() instanceof ConvFloat) {
            // cast to ConvFloat
            ConvFloat expr = (ConvFloat) getExpression();

            // generate code for operand
            AbstractExpr[] operand = {expr.getOperand()};
            ListCodeGen(operand);

            // get result out of operation stack
            VirtualRegister r = getCodeGenBackEnd().getContextManager().operationStackPop();

            // convert it
            getCodeGenBackEnd().addInstruction(new FLOAT(r.requestPhysicalRegister(), r.requestPhysicalRegister()));

            r.setFloat();

            // push result to operation stack
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
        else {
            throw new UnsupportedOperationException("operation not permitted");
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

    /**
     * try to evaluate operation as a constant
     * @param compiler global compiler
     * @return created constant, can be null
     */
    @Override
    public Constant getConstant(DecacCompiler compiler) {
        if (getExpression() instanceof ConvFloat) {
            // cast to UnaryMinus
            ConvFloat expr = (ConvFloat) getExpression();

            Constant cOp = expr.getOperand().getConstant(compiler);

            if (cOp == null) {
                return null;
            }

            float result = (float) cOp.getValueInt();
            return new Constant(result);
        }
        else {
            return null;
        }
    }
}
