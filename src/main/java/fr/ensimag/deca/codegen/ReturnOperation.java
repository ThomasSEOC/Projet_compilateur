package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ListInst;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class ReturnOperation extends AbstractOperation {

    public ReturnOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        ListInst inst = new ListInst();
        inst.add(getExpression());
        inst.codeGenListInst(getCodeGenBackEnd().getCompiler());

        VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();
        getCodeGenBackEnd().addInstruction(new LOAD(result.requestPhysicalRegister(), GPRegister.getR(0)));
        result.destroy();

        Label endLabel = getCodeGenBackEnd().getClassManager().getCurrentMethodEnd();
        if (endLabel != null) {
            getCodeGenBackEnd().addInstruction(new BRA(endLabel));
        }
    }

    @Override
    public void print() {
        // useless
    }
}
