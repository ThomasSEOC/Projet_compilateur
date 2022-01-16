package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Assign;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * class dedicated to assign operation code generation
 *
 * @author gl54
 * @date 11/01/2022
 */
public class AssignOperation extends AbstractOperation {

    /**
     * constructor of class AssignOperation
     * @param codegenbackend global codegen backend
     * @param expression expression related to current operation
     */
    public AssignOperation(CodeGenBackend codegenbackend, AbstractExpr expression) {
        super(codegenbackend, expression);
    }

    /**
     * method called to generate code for assignation
     */
    @Override
    public void doOperation() {
        // cast expression
        Assign expr = (Assign) this.getExpression();

        // generate code for right operand
        AbstractExpr[] listExprs = {expr.getRightOperand()};
        this.ListCodeGen(listExprs);

        // get result of right operand computation
        VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();

        // generate address where to store result
        Identifier identifier = (Identifier) expr.getLeftOperand();
        DAddr addr = identifier.getVariableDefinition().getOperand();

        // store result
        getCodeGenBackEnd().getCompiler().addInstruction(new STORE(result.requestPhysicalRegister(), addr));

        // destroy register
        result.destroy();
    }

    /**
     * method called to generate code for assignation print
     * this method is pretty useless
     */
    @Override
    public void print() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
