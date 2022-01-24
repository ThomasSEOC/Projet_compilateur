package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * class responsible for method call code generation
 */
public class MethodCallOperation extends AbstractReadOperation {

    /**
     * constructor for method coll
     * @param backend global code generation backend
     * @param expression operation related
     */
    public MethodCallOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * generate code for method call
     */
    @Override
    public void doOperation() {
        // get object instance
        MethodCall methodCall = (MethodCall) getExpression();
        Identifier objectIdentifier = (Identifier) methodCall.getExpr();
        RegisterOffset structureOffset = getCodeGenBackEnd().getVariableRegisterOffset(objectIdentifier.getName().getName());

        // get method offset
        String methodName = objectIdentifier.getType().toString();
        int methodOffset = getCodeGenBackEnd().getClassManager().getClassObject(methodName).getMethodsOffsets().get(methodCall.getIdent().getName().getName());

        getCodeGenBackEnd().addComment("call method " + methodCall.getIdent().getName().getName());

        // reserve space for params in stack
        getCodeGenBackEnd().addInstruction(new ADDSP(methodCall.getListExpr().size() + 1));

        // check structure
        getCodeGenBackEnd().addInstruction(new LOAD(structureOffset, GPRegister.getR(0)));
        if (!getCodeGenBackEnd().getCompiler().getCompilerOptions().getNoCheckStatus()) {
            getCodeGenBackEnd().addInstruction(new CMP(new NullOperand(), GPRegister.getR(0)));
            getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getErrorsManager().getDereferencementNullLabel()));
        }

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
        getCodeGenBackEnd().addInstruction(new LOAD(structureOffset, GPRegister.getR(0)));
        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(0, GPRegister.getR(0)), GPRegister.getR(0)));
        getCodeGenBackEnd().addInstruction(new BSR(new RegisterOffset(methodOffset, GPRegister.getR(0))));

        // free space
        getCodeGenBackEnd().addInstruction(new SUBSP(methodCall.getListExpr().size() + 1));
    }

    /**
     * call method and print result
     */
    @Override
    public void print() {
        doOperation();

        // move result to R1
        getCodeGenBackEnd().addInstruction(new LOAD(GPRegister.getR(0), GPRegister.getR(1)));

        // get type
        MethodCall methodCall = (MethodCall) getExpression();
        if (methodCall.getType().isFloat()) {
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
