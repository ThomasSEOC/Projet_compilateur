package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

public class MethodCallOperation extends AbstractReadOperation {

    public MethodCallOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        // get object instance
        MethodCall methodCall = (MethodCall) getExpression();
        Identifier objectIdentifier = (Identifier) methodCall.getExpr();
        int structureOffset = getCodeGenBackEnd().getVariableOffset(objectIdentifier.getName().getName());

        // get method offset
        String nature = objectIdentifier.getClassDefinition().getNature();
        int methodOffset = getCodeGenBackEnd().getClassManager().getClassObject(nature).getMethodsOffsets().get(methodCall.getIdent().getName().getName());

        getCodeGenBackEnd().addComment("call method " + methodCall.getIdent().getName().getName());

        // check structure
        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(structureOffset, Register.LB), GPRegister.getR(0)));
        getCodeGenBackEnd().addInstruction(new CMP(new NullOperand(), GPRegister.getR(0)));
        getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getErrorsManager().getDereferencementNullLabel()));

        // reserve space for params in stack
        getCodeGenBackEnd().addInstruction(new ADDSP(methodCall.getListExpr().size() + 1));

        // add implicit param
        getCodeGenBackEnd().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(0, Register.SP)));

        // add other params
        for (int i = 0; i < methodCall.getListExpr().size(); i++) {
            AbstractExpr paramExpr = methodCall.getListExpr().getList().get(i);
            if (paramExpr instanceof Assign) {
                AssignOperation operator = new AssignOperation(getCodeGenBackEnd(), paramExpr);
                operator.doOperation(true);
            }
            else {
                ListInst insts = new ListInst();
                insts.add(paramExpr);
                insts.codeGenListInst(getCodeGenBackEnd().getCompiler());
            }

            VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();
            getCodeGenBackEnd().addInstruction(new STORE(result.requestPhysicalRegister(), new RegisterOffset(-(i+1), Register.SP)));
        }

        // jump
        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(structureOffset, Register.LB), GPRegister.getR(0)));
        getCodeGenBackEnd().addInstruction(new BSR(new RegisterOffset(methodOffset, GPRegister.getR(0))));

        // free space
        getCodeGenBackEnd().addInstruction(new SUBSP(methodCall.getListExpr().size() + 1));
    }

    @Override
    public void print() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
