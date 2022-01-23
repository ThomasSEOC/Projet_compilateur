package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.Selection;
import fr.ensimag.deca.tree.This;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

public class FieldSelectOperation extends AbstractOperation {

    public FieldSelectOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    private void selectVariable() {
        Selection selection = (Selection) getExpression();
        if (selection.getExpr() instanceof Identifier) {
            String variableName = ((Identifier)selection.getExpr()).getName().getName();
            RegisterOffset variablePointer = getCodeGenBackEnd().getVariableRegisterOffset(variableName);
            getCodeGenBackEnd().addInstruction(new LOAD(variablePointer, GPRegister.getR(0)));
        }
        else if (selection.getExpr() instanceof This) {
            getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(0)));
        }

        // check null pointer
        if (!getCodeGenBackEnd().getCompiler().getCompilerOptions().getNoCheckStatus()) {
            getCodeGenBackEnd().addInstruction(new CMP(new NullOperand(), GPRegister.getR(0)));
            getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getErrorsManager().getDereferencementNullLabel()));
        }
    }

    private int getOffset() {
        Selection selection = (Selection) getExpression();
        String field = selection.getFieldIdent().getName().getName();
        if (selection.getExpr() instanceof Identifier) {
            String className = selection.getExpr().getType().toString();
            ClassObject object = (ClassObject) getCodeGenBackEnd().getClassManager().getClassObject(className);
            return object.getFieldOffset(field);
        }
        else {
            ClassObject object = getCodeGenBackEnd().getClassManager().getCurrentObject();
            return object.getFieldOffset(field);
        }
    }

    public void write() {
        selectVariable();

        VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();

        int offset = getOffset();

        getCodeGenBackEnd().addInstruction(new STORE(result.requestPhysicalRegister(), new RegisterOffset(offset, GPRegister.getR(0))));
    }

    @Override
    public void doOperation() {
        selectVariable();

        int offset = getOffset();

        VirtualRegister register = getCodeGenBackEnd().getContextManager().requestNewRegister();
        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(offset, GPRegister.getR(0)), register.requestPhysicalRegister()));

        getCodeGenBackEnd().getContextManager().operationStackPush(register);
    }

    @Override
    public void print() {
        selectVariable();

        Selection selection = (Selection) getExpression();

        int offset = getOffset();

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
}
