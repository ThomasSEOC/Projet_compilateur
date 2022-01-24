package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.*;

/**
 * Class responsible for Classes management and code generation
 */
public class ClassManager {
    private final CodeGenBackend backend;
    private final List<AbstractClassObject> classList;
    private final Map<String, AbstractClassObject> classMap;
    private int vtableOffset;
    private boolean isInstanceofUsed = false;
    private Label currentMethodEnd = null;
    private ClassObject currentObject = null;

    /**
     * constructor for ClassManager
     * @param backend global code generation backend
     */
    public ClassManager(CodeGenBackend backend) {
        this.backend = backend;
        vtableOffset = 1;
        classList = new ArrayList<>();
        classMap = new HashMap<>();
        classList.add(new DefaultObject(this));
        classMap.put("Object", classList.get(0));
    }

    /**
     * setter for current in processing object
     * @param object class object
     */
    public void setCurrentObject(ClassObject object) {
        this.currentObject = object;
    }

    /**
     * getter for current object
     * @return current in processing object
     */
    public ClassObject getCurrentObject() {
        return currentObject;
    }

    /**
     * getter for backend
     * @return code generation backend
     */
    public CodeGenBackend getBackend() {
        return backend;
    }

    /**
     * getter for VTableOffset
     * @return Vtable genration current offset
     */
    public int getVtableOffset() {
        return vtableOffset;
    }

    /**
     * add class to class manager
     * @param nameClass class to add
     * @param superClass super class
     * @param methods methods for class to add
     * @param fields fields for class to add
     */
    public void addClass(AbstractIdentifier nameClass, AbstractIdentifier superClass, ListDeclMethod methods, ListDeclField fields) {
        ClassObject classObject = new ClassObject(this, nameClass, superClass, methods, fields);
        classList.add(classObject);
        classMap.put(nameClass.getName().getName(), classObject);
    }

    /**
     * get AbstractClassObject related to nameClass
     * @param nameClass class Identifier
     * @return class object corresponding to nameClass
     */
    public AbstractClassObject getClassObject(AbstractIdentifier nameClass) {
        return classMap.get(nameClass.getName().getName());
    }

    /**
     * get AbstractClassObject related to nameClass
     * @param nameClass class Identifier name
     * @return class object corresponding to nameClass
     */
    public AbstractClassObject getClassObject(String nameClass) {
        return classMap.get(nameClass);
    }

    /**
     * generate Vtable creation code
     */
    public void VTableCodeGen() {
        backend.getCompiler().addComment("VTABLE INIT");

        for (AbstractClassObject classObject : classList) {
            classObject.VTableCodeGen(vtableOffset);
            vtableOffset += classObject.getVTableSize();
        }

        backend.writeInstructions();
    }

    /**
     * generate code methods
     */
    public void methodsCodeGen() {
        backend.getCompiler().addComment("METHODS");
        for (AbstractClassObject classObject : classList) {
            classObject.methodsCodeGen();
        }
        if (isInstanceofUsed) {
            instanceOfCodeGen();
        }
        backend.writeInstructions();
    }

    /**
     * set current method label to which jump after return
     * @param endLabel end of method label
     */
    public void setCurrentMethodEnd(Label endLabel) {
        currentMethodEnd = endLabel;
    }

    /**
     * getter for end of method label
     * @return get label to which jump after a return
     */
    public Label getCurrentMethodEnd() {
        return currentMethodEnd;
    }

    /**
     * getter for instanceof method label
     * @return label to which jump to call instanceof
     */
    public Label getInstanceofLabel() {
        isInstanceofUsed = true;
        return new Label("Code.InstanceOf");
    }

    /**
     * generate code for instanceof method
     */
    public void instanceOfCodeGen() {
        // create internal labels
        Label start = new Label("InstanceOf_start");
        Label endSucess = new Label("InstanceOf_sucess");
        Label endFailure = new Label("InstanceOf_failure");

        // add call label
        backend.addLabel(new Label("Code.InstanceOf"));

        // load the two operands in R0 and R1
        backend.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(1)));
        backend.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), GPRegister.getR(0)));

        // add start label
        backend.addLabel(start);

        // loop until success or failure
        backend.addInstruction(new LOAD(new RegisterOffset(0, GPRegister.getR(1)), GPRegister.getR(1)));
        backend.addInstruction(new CMP(GPRegister.getR(0), GPRegister.getR(1)));
        backend.addInstruction(new BEQ(endSucess));
        backend.addInstruction(new CMP(new NullOperand(), GPRegister.getR(1)));
        backend.addInstruction(new BEQ(endFailure));
        backend.addInstruction(new BRA(start));

        // success
        backend.addLabel(endSucess);
        backend.addInstruction(new LOAD(new ImmediateInteger(1), GPRegister.getR(0)));
        backend.addInstruction(new RTS());

        // failure
        backend.addLabel(endFailure);
        backend.addInstruction(new LOAD(new ImmediateInteger(0), GPRegister.getR(0)));
        backend.addInstruction(new RTS());
    }
}
