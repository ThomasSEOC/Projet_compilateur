package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Assign;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class AssignOperation extends AbstractOperation {

    public AssignOperation(CodeGenBackend codegenbackend, AbstractExpr expression) {
        super(codegenbackend, expression);
    }

    @Override
    public void doOperation() {
        // cast expression
        Assign expr = (Assign) this.getExpression();

        // gen code for right operand
        AbstractExpr[] listExprs = {expr.getRightOperand()};
        this.ListCodeGen(listExprs);

        // get result of right operand computation
        VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();

        // generate address where to store result
        Identifier identifier = (Identifier) expr.getRightOperand();
        DAddr addr = identifier.getVariableDefinition().getOperand();

        // store result
        getCodeGenBackEnd().getCompiler().addInstruction(new STORE(result.requestPhysicalRegister(), addr));

        // destroy register
        result.destroy();
    }
}
