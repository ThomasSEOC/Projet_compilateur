package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class FieldSelectOperation extends AbstractOperation {

    public FieldSelectOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {

    }

    @Override
    public void print() {

    }

    // tmp

//    /**
//     * generate code for field assign
//     * @param fieldName string representing field
//     */
//    public void codeGenAssign(String fieldName) {
//        // risque de bug
//        int offset = object.getFieldOffset(fieldName);
//
//        CodeGenBackend backend = object.getClassManager().getBackend();
//        VirtualRegister result = backend.getContextManager().operationStackPop();
//        VirtualRegister addressRegister = backend.getContextManager().operationStackPop();
//
//        backend.addInstruction(new LOAD(new RegisterOffset(offset, addressRegister.requestPhysicalRegister()), result.requestPhysicalRegister()));
//        result.destroy();
//        addressRegister.destroy();
//    }
//
//    /**
//     * generate code for field read
//     * @param fieldName string representing field
//     */
//    public void codeGenRead(String fieldName) {
//        int offset = object.getFieldOffset(fieldName);
//
//        CodeGenBackend backend = object.getClassManager().getBackend();
//        VirtualRegister register = backend.getContextManager().operationStackPop();
//
//        backend.addInstruction(new LOAD(new RegisterOffset(offset, register.requestPhysicalRegister()), register.requestPhysicalRegister()));
//
//        backend.getContextManager().operationStackPush(register);
//    }
//
//    /**
//     * generate code for field selectiob
//     * @param objectVariableString string representing class instance
//     */
//    public void select(String objectVariableString) {
//        DecacCompiler compiler = getClassManager().getBackend().getCompiler();
//
//        // get register offset
//        RegisterOffset registerOffset = getClassManager().getBackend().getVariableRegisterOffset(objectVariableString);
//
//        // request new virtual register
//        VirtualRegister register = getClassManager().getBackend().getContextManager().requestNewRegister();
//
//        // check null pointer
//        compiler.getCodeGenBackend().addInstruction(new LOAD(registerOffset, register.requestPhysicalRegister()));
//        compiler.getCodeGenBackend().addInstruction(new CMP(new NullOperand(), register.requestPhysicalRegister()));
//        compiler.getCodeGenBackend().addInstruction(new BEQ(getClassManager().getBackend().getErrorsManager().getDereferencementNullLabel()));
//
//        getClassManager().getBackend().getContextManager().operationStackPush(register);
//    }
}
