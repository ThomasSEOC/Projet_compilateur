package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ListInst;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * class responsible for return operation code generation
 */
public class ReturnOperation extends AbstractOperation {

    /**
     * constructor for ReturnOperation
     * @param backend global code generation backend
     * @param expression operation related expression
     */
    public ReturnOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * generate code for return
     */
    @Override
    public void doOperation() {
        // generate code for operand
        ListInst inst = new ListInst();
        inst.add(getExpression());
        inst.codeGenListInst(getCodeGenBackEnd().getCompiler());

        // get result and load it to R0
        VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();
        getCodeGenBackEnd().addInstruction(new LOAD(result.requestPhysicalRegister(), GPRegister.getR(0)));
        result.destroy();

        // jump to end label if exists
        Label endLabel = getCodeGenBackEnd().getClassManager().getCurrentMethodEnd();
        if (endLabel != null) {
            getCodeGenBackEnd().addInstruction(new BRA(endLabel));
        }
    }

    /**
     * print return
     */
    @Override
    public void print() {
        throw new UnsupportedOperationException("operation not permitted");
    }
}
