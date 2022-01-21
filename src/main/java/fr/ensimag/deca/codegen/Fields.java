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

    private int codeGencDeclRecur(ClassObject object, int offset) {
        CodeGenBackend backend = object.getClassManager().getBackend();

        AbstractClassObject superObject = object.getClassManager().getClassObject(object.getSuperClass());
        if (!(superObject instanceof DefaultObject)) {
            offset = codeGencDeclRecur((ClassObject) superObject, offset);
        }

        VirtualRegister objectStructurePointer = backend.getContextManager().operationStackPop();
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
                backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, objectStructurePointer.requestPhysicalRegister())));
            }
            offset++;
        }
        backend.getContextManager().operationStackPush(objectStructurePointer);

        return offset;
    }

    public void codeGenDecl() {
        codeGencDeclRecur(object, 0);

        CodeGenBackend backend = object.getClassManager().getBackend();

        VirtualRegister objectStructurePointer = backend.getContextManager().operationStackPop();

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
