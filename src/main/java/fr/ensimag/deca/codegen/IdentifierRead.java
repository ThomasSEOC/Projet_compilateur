package fr.ensimag.deca.codegen;

import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;

/**
 * class dedicated to identifier/global variable read
 */
public class IdentifierRead extends AbstractOperation {

    /**
     * constructor for IdentifierRead
     * @param backend global codegen backend
     * @param expression expression related to operation
     */
    public IdentifierRead(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * method called to generate code for declared variable read
     */
    @Override
    public void doOperation() {
        // cast to Identifier
        Identifier expr = (Identifier) getExpression();

        // request a new virtual register
        VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister();

        // get offset from GB for variable identified by expr
        int offset = getCodeGenBackEnd().getVariableOffset(expr.getName().getName());

        // load into physical register
        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(offset, Register.GB), r.requestPhysicalRegister()));

        // push virtual register to operation stack
        getCodeGenBackEnd().getContextManager().operationStackPush(r);
    }

    /**
     * method called to generate code for printing a declared variable
     */
    @Override
    public void print() {
        // cast to Identifier
        Identifier expr = (Identifier) getExpression();

        // get offset from GB
        int offset = getCodeGenBackEnd().getVariableOffset(expr.getName().getName());

        // load variable into R1
        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(offset, Register.GB), GPRegister.getR(1)));

        // separate according to type
        if (expr.getType() instanceof IntType) {
            getCodeGenBackEnd().addInstruction(new WINT());
        }
        else if (expr.getType() instanceof FloatType) {
            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().addInstruction(new WFLOATX());
            }
            else {
                getCodeGenBackEnd().addInstruction(new WFLOAT());
            }
        }
        else
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }
}
