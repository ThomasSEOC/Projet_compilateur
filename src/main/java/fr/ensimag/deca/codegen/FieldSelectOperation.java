package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.Selection;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

public class FieldSelectOperation extends AbstractOperation {

    public FieldSelectOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    private void selectVariable() {
        Selection selection = (Selection) getExpression();
        String variableName = ((Identifier)selection.getExpr()).getName().getName();

        RegisterOffset variablePointer = getCodeGenBackEnd().getVariableRegisterOffset(variableName);

        getCodeGenBackEnd().addInstruction(new LOAD(variablePointer, GPRegister.getR(0)));
    }

    @Override
    public void doOperation() {
        selectVariable();

        Selection selection = (Selection) getExpression();
        String className = selection.getExpr().getType().toString();
        String field = selection.getFieldIdent().getName().getName();

        ClassObject object = (ClassObject) getCodeGenBackEnd().getClassManager().getClassObject(className);
        int offset = object.getFieldOffset(field);

        VirtualRegister register = getCodeGenBackEnd().getContextManager().requestNewRegister();
        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(offset, GPRegister.getR(0)), register.requestPhysicalRegister()));

        getCodeGenBackEnd().getContextManager().operationStackPush(register);
    }

    @Override
    public void print() {
        selectVariable();

        Selection selection = (Selection) getExpression();
        String className = selection.getExpr().getType().toString();
        String field = selection.getFieldIdent().getName().getName();

        ClassObject object = (ClassObject) getCodeGenBackEnd().getClassManager().getClassObject(className);
        int offset = object.getFieldOffset(field);

        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(offset, GPRegister.getR(0)), GPRegister.getR(1)));

        if (selection.getType().isFloat()) {
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
