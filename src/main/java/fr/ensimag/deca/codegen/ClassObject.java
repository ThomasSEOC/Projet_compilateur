package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * class responsible for non default Object representation
 */
public class ClassObject extends AbstractClassObject {
    private final AbstractIdentifier nameClass;
    private final AbstractIdentifier superClass;
    private final ListDeclMethod methods;
    private final ListDeclField fields;

    /**
     * constructor for class ClassObject
     * @param classManager code generation class manager
     * @param nameClass class identifier
     * @param superClass super class identifier
     * @param methods methods related to this class
     * @param fields fields related to this class
     */
    public ClassObject(ClassManager classManager, AbstractIdentifier nameClass, AbstractIdentifier superClass, ListDeclMethod methods, ListDeclField fields) {
        super(classManager);
        this.nameClass = nameClass;
        this.superClass = superClass;
        this.methods = methods;
        this.fields = fields;
    }

    /**
     * getter for class identifier
     * @return identifier of represented class
     */
    public AbstractIdentifier getNameClass() {
        return nameClass;
    }

    /**
     * getter for super class identifier
     * @return identifier of super class
     */
    public AbstractIdentifier getSuperClass() {
        return superClass;
    }

    /**
     * getter for methods of this class
     * @return list of methods related to this represented class
     */
    public ListDeclMethod getMethods() { return methods; }

    /**
     * getter for field of this class
     * @return list of fields related to this represented class
     */
    public ListDeclField getFields() { return fields; }

    /**
     * generate Vtable creation code for this represented class
     * @param offset current Vtable offset
     */
    @Override
    public void VTableCodeGen(int offset) {
        VTableSearchLabels();

        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("init vtable for " + getNameClass().getName().getName());
        AbstractClassObject superObject = getClassManager().getClassObject(superClass);

        setVTableOffset(offset);
        backend.addInstruction(new LEA(new RegisterOffset(superObject.getVTableOffset(), Register.GB), GPRegister.getR(0)));
        backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, Register.GB)));

        for (Map.Entry<String, Label> method : methodsLabels.entrySet()) {
            Label codeLabel = method.getValue();
            int index = offset + methodsOffsets.get(method.getKey());
            backend.addInstruction(new LOAD(new LabelOperand(codeLabel), GPRegister.getR(0)));
            backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(index, Register.GB)));
        }
    }

    /**
     * generate code for structure init code
     */
    @Override
    public void structureInitCodeGen() {
        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("Code for init of " + getNameClass().getName().getName());

        backend.addLabel(new Label("Code." + getNameClass().getName().getName() + ".Init"));

        backend.writeInstructions();

        backend.createContext();

        backend.addComment("store VTable pointer");

        // get object pointer
        VirtualRegister objectStructurePointer = backend.getContextManager().requestNewRegister();
        backend.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), objectStructurePointer.requestPhysicalRegister()));
        // get address of method table object
        backend.addInstruction(new LEA(new RegisterOffset(getVTableOffset(), Register.GB), GPRegister.getR(0)));
        // set first word as pointer to object method table entry
        backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(0,  objectStructurePointer.requestPhysicalRegister())));

        backend.getContextManager().operationStackPush(objectStructurePointer);

        codeGenFieldDecl();

        VirtualRegister returnedObjectStructurePointer = backend.getContextManager().operationStackPop();
        returnedObjectStructurePointer.destroy();

        backend.popContext();
    }

    /**
     * getter for Vtable size
     * @return occupied memory size on VTable for this represented class
     */
    @Override
    public int getVTableSize() {
        // attention si redefinition de equals
        return methodsLabels.size() + 1;
    }

    /**
     * getter for structure size
     * @return occupied memory size by structure when class is instantiated
     */
    @Override
    public int getStructureSize() {
        return fields.size() + getClassManager().getClassObject(superClass).getStructureSize();
    }

    /**
     * generate code for methods of the represented class
     */
    @Override
    public void methodsCodeGen() {
        getClassManager().setCurrentObject(this);

        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("Code for methods of " + getNameClass().getName().getName());

        // check if init is redefined

        boolean isInitOverride = false;
        for (AbstractDeclMethod abstractMethod : getMethods().getList()) {
            DeclMethod method = (DeclMethod) abstractMethod;
            if (Objects.equals(method.getName().getName().getName(), "init")) {
                isInitOverride = true;
                break;
            }
        }
        if (!isInitOverride) {
            structureInitCodeGen();
        }

        for (AbstractDeclMethod abstractMethod : getMethods().getList()) {
            DeclMethod method = (DeclMethod) abstractMethod;
            if (method.getBody() instanceof MethodBody) {

                Label endLabel;
                if (Objects.equals(method.getType().getName().getName(), "void")) {
                    endLabel = null;
                }
                else {
                    endLabel = new Label("Code.end." + getNameClass().getName().getName() + "." + method.getName().getName());
                }
                getClassManager().setCurrentMethodEnd(endLabel);

                backend.addLabel(new Label("Code." + getNameClass().getName().getName() + "." + method.getName().getName()));
                backend.addComment(method.getName().getName().getName());
                backend.writeInstructions();
                backend.createContext();

                // add params
                ListDeclParam params = method.getParams();
                for (int i = 0; i < params.size(); i++) {
                    DeclParam param = (DeclParam) params.getList().get(i);
                    backend.addParam(param.getName().getName().getName(), -3 - i);
                }

                method.getBody().codeGen(backend.getCompiler());

                if (endLabel != null) {
                    if (!backend.getCompiler().getCompilerOptions().getNoCheckStatus()) {
                        backend.addInstruction(new WSTR("Erreur : sortie de la méthode " + getNameClass().getName().getName() + "." + method.getName().getName() + " sans return"));
                        backend.addInstruction(new WNL());
                        backend.addInstruction(new ERROR());
                    }
                    backend.addLabel(endLabel);
                }

                backend.popContext();
            }
            else {
                // asm method
                method.getBody().codeGen(backend.getCompiler());
            }
        }

        getClassManager().setCurrentObject(null);
    }

    /**
     * getter for offset of the specified field
     * @param fieldName string corresponding to specified field
     * @return offset to apply to the class instance structure
     */
    public int getFieldOffset(String fieldName) {
        AbstractClassObject superObject = getClassManager().getClassObject(superClass);

        // if field in current class
        int i = 0;
        for (AbstractDeclField abstractField : fields.getList()) {
            DeclField field = (DeclField) abstractField;
            if (Objects.equals(field.getField().getName().getName(), fieldName)) {
                return i + superObject.getStructureSize();
            }
            i++;
        }

        return ((ClassObject)superObject).getFieldOffset(fieldName);
    }

    /**
     * getter for specified method offset
     * @param abstractMethod called method
     * @return offset from class object base in VTable
     */
    @Override
    public int getMethodOffset(AbstractDeclMethod abstractMethod) {
        DeclMethod method = (DeclMethod) abstractMethod;
        String name = method.getName().getName().getName();
        int i = getClassManager().getClassObject(getSuperClass()).getVTableSize();
        for (AbstractDeclMethod aMethod : getMethods().getList()) {
            DeclMethod testMethod = (DeclMethod) aMethod;
            if (Objects.equals(testMethod.getName().getName().getName(), name)) {
                return i;
            }
            i++;
        }

        // recursion
        return getClassManager().getClassObject(getSuperClass()).getMethodOffset(abstractMethod);
    }

    /**
     * getter for className
     * @return identifier related to the represented class
     */
    @Override
    public AbstractIdentifier getClassName() {
        return nameClass;
    }

    /**
     * generate code for object instantiation
     */
    @Override
    public void createObjectCodeGen() {
        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("create instance of class " + getNameClass().getName().getName());
        backend.addInstruction(new NEW(getStructureSize(), GPRegister.getR(0)));
        backend.addInstruction(new BOV(backend.getErrorsManager().getHeapOverflowLabel()));
        backend.addInstruction(new PUSH(GPRegister.getR(0)));
        backend.addInstruction(new BSR(new Label("Code." + getNameClass().getName().getName() + ".Init")));
        VirtualRegister objectPointer = backend.getContextManager().requestNewRegister();
        backend.addInstruction(new POP(objectPointer.requestPhysicalRegister()));
        backend.getContextManager().operationStackPush(objectPointer);
    }

    /**
     * create method labels map
     * @return method labels map
     */
    protected Map<String,Label> VTableSearchLabels() {
        if (methodsLabels == null) {
            // copy from super class
            methodsLabels = new HashMap<>(getClassManager().getClassObject(superClass).VTableSearchLabels());
            methodsOffsets = new HashMap<>(getClassManager().getClassObject(superClass).getMethodsOffsets());

            for (AbstractDeclMethod abstractMethod : methods.getList()) {
                DeclMethod method = (DeclMethod) abstractMethod;
                Label methodLabel = new Label("Code." + nameClass.getName().getName() + "." + method.getName().getName().getName());
                if (methodsLabels.containsKey(method.getName().getName().getName())) {
                    methodsLabels.replace(method.getName().getName().getName(), methodLabel);
                }
                else {
                    methodsLabels.put(method.getName().getName().getName(), methodLabel);
                    methodsOffsets.put(method.getName().getName().getName(), methodsOffsets.size()+1);
                }
            }
        }

        return methodsLabels;
    }

    /**
     * generate code for field instantiation when instantiating a class
     */
    public void codeGenFieldDecl() {
        CodeGenBackend backend = getClassManager().getBackend();

        AbstractClassObject superObject = getClassManager().getClassObject(superClass);
        superObject.codeGenFieldDecl();

        VirtualRegister objectStructurePointer = backend.getContextManager().operationStackPop();

        int offset = superObject.getStructureSize();
        for (AbstractDeclField abstractField : getFields().getList()) {
            DeclField field = (DeclField) abstractField;
            backend.addComment("init " + getNameClass().getName().getName() + "." + field.getField().getName().getName());
            if (field.getInit() instanceof Initialization) {
                field.getField().getFieldDefinition().setOperand(new RegisterOffset(offset, objectStructurePointer.requestPhysicalRegister()));

                // use assign
                Assign assign = new Assign(field.getField(), ((Initialization) field.getInit()).getExpression());
                AssignOperation operator = new AssignOperation(getClassManager().getBackend(), assign);
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
    }
}
