package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class Fields {
    private final ClassObject object;

    public Fields(ClassObject object) {
        this.object = object;
    }

    public void codeGenDecl(int offset) {
        CodeGenBackend backend = object.getClassManager().getBackend();

        VirtualRegister objectStructurePointer = object.getClassManager().getBackend().getContextManager().operationStackPop();

        int i = offset;
        for (AbstractDeclField abstractField : object.getFields().getList()) {
            DeclField field = (DeclField) abstractField;
            backend.addComment("init " + object.getNameClass().getName().getName() + "." + field.getField().getName().getName());
            if (field.getInit() instanceof Initialization) {
                // use assign
                // risque de bug
                Assign assign = new Assign(field.getField(), ((Initialization) field.getInit()).getExpression());
                AssignOperation operator = new AssignOperation(object.getClassManager().getBackend(), assign);
                operator.doOperation();
            }
            else {
                // copy 0 to field
                backend.addInstruction(new LOAD(new ImmediateInteger(0), GPRegister.getR(0)));
                backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(i, objectStructurePointer.requestPhysicalRegister())));
            }
            i++;
        }

        objectStructurePointer.destroy();
    }

    public void codeGenAssign(String fieldName) {
        // risque de bug
        int offset = object.getFieldOffset(fieldName);

        CodeGenBackend backend = object.getClassManager().getBackend();
        VirtualRegister result = backend.getContextManager().operationStackPop();
        VirtualRegister addressRegister = backend.getContextManager().operationStackPop();

        backend.addInstruction(new LOAD(new RegisterOffset(offset, addressRegister.requestPhysicalRegister()), result.requestPhysicalRegister()));
        result.destroy();
        addressRegister.destroy();
    }

    public void codeGenRead(String fieldName) {
        int offset = object.getFieldOffset(fieldName);

        CodeGenBackend backend = object.getClassManager().getBackend();
        VirtualRegister register = backend.getContextManager().operationStackPop();

        backend.addInstruction(new LOAD(new RegisterOffset(offset, register.requestPhysicalRegister()), register.requestPhysicalRegister()));

        backend.getContextManager().operationStackPush(register);
    }
}
