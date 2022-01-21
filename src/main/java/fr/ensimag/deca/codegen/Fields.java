package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * class responsible for deca class field handling
 */
public class Fields {
    private final ClassObject object;

    /**
     * constructor for Fields
     * @param object related AbstractClassObject
     */
    public Fields(ClassObject object) {
        this.object = object;
    }

    /**
     * generate code for field declaration recursively
     * @param object related object
     * @param offset current offset in instance structure
     * @return new offset after field initialization
     */
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
                field.getField().getFieldDefinition().setOperand(new RegisterOffset(offset, objectStructurePointer.requestPhysicalRegister()));

                // use assign
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

    /**
     * generate code for field instantiation when instantiating a class
     */
    public void codeGenDecl() {
        codeGencDeclRecur(object, 1);

        CodeGenBackend backend = object.getClassManager().getBackend();

        VirtualRegister objectStructurePointer = backend.getContextManager().operationStackPop();

        objectStructurePointer.destroy();
    }

    /**
     * generate code for field assign
     * @param fieldName string representing field
     */
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

    /**
     * generate code for field read
     * @param fieldName string representing field
     */
    public void codeGenRead(String fieldName) {
        int offset = object.getFieldOffset(fieldName);

        CodeGenBackend backend = object.getClassManager().getBackend();
        VirtualRegister register = backend.getContextManager().operationStackPop();

        backend.addInstruction(new LOAD(new RegisterOffset(offset, register.requestPhysicalRegister()), register.requestPhysicalRegister()));

        backend.getContextManager().operationStackPush(register);
    }
}
