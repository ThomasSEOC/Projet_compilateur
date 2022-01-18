package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.UnaryMinus;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * class responsible for unary minus operation code generation
 */
public class UnaryMinusOperation extends AbstractOperation {

    /**
     * constructor for {@link UnaryMinusOperation}
     * @param backend global code generation backend
     * @param expression expression related to operation
     */
    public UnaryMinusOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * method called to generate code for minus operation
     */
    @Override
    public void doOperation() {
        // cast to UnaryMinus
        UnaryMinus expr = (UnaryMinus) getExpression();

        // generate code for operand
        AbstractExpr[] operand = {expr.getOperand()};
        ListCodeGen(operand);

        // get result form oepration stack
        VirtualRegister r1 = getCodeGenBackEnd().getContextManager().operationStackPop();

        // add OPP instruction to take the opposite
        getCodeGenBackEnd().addInstruction(new OPP(r1.requestPhysicalRegister(), r1.requestPhysicalRegister()));

        // push result to operation stack
        getCodeGenBackEnd().getContextManager().operationStackPush(r1);
    }

    /**
     * method called to generate code for minus operation print
     */
    @Override
    public void print() {
        // do operation
        doOperation();

        // get result from operation stack
        VirtualRegister r = getCodeGenBackEnd().getContextManager().operationStackPop();

        // copy virtual register to R1
        getCodeGenBackEnd().addInstruction(new LOAD(r.getDVal(), GPRegister.getR(1)));

        // free virtual register
        r.destroy();

        // separate according to type and Hex
        if (r.getIsFloat()) {
            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().addInstruction(new WFLOATX());
            }
            else {
                getCodeGenBackEnd().addInstruction(new WFLOAT());
            }
        }
        else {
            getCodeGenBackEnd().addInstruction(new WINT());
        }

    }
}
