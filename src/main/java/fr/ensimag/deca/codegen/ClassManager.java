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

    public AbstractClassObject getClassObject(String nameClass) {
        return classMap.get(nameClass);
    }

//    private List<List<AbstractClassObject>> orderClassObjects() {
//        // need to optimize
//
//        List<AbstractClassObject> done = new ArrayList<>();
//        List<List<AbstractClassObject>> list = new ArrayList<>();
//
//        // first element is default object
//        List<AbstractClassObject> currentObjects = new ArrayList<>();
//        AbstractClassObject defaultObject = classList.remove(0);
//        currentObjects.add(defaultObject);
//        list.add(currentObjects);
//        done.add(defaultObject);
//
//        while (classList.size() > 0) {
//            List<AbstractClassObject> lastObjects = currentObjects;
//            currentObjects = new ArrayList<>();
//            List<AbstractClassObject> toRemove = new ArrayList<>();
//            for (AbstractClassObject abstractClassObject : classList) {
//                ClassObject classObject = (ClassObject) abstractClassObject;
//                AbstractIdentifier superClassIdentifier = classObject.getSuperClass();
//
//                boolean found = false;
//                int i = 0;
//                while ((i < lastObjects.size()) && (!found)) {
//                    if (lastObjects.get(i).getClassName() == superClassIdentifier) {
//                        found = true;
//                    }
//                    i++;
//                }
//
//                if (found) {
//                    currentObjects.add(abstractClassObject);
//                    done.add(abstractClassObject);
//                    toRemove.add(abstractClassObject);
//                }
//            }
//
//            for (AbstractClassObject AbstractClassObject : toRemove) {
//                classList.remove(AbstractClassObject);
//            }
//
//            list.add(currentObjects);
//        }
//
//        classList = done;
//
//        return list;
//    }

    /**
     * generate Vtable creation code
     */
    public void VTableCodeGen() {


        backend.getCompiler().addComment("VTABLE INIT");

//        List<List<AbstractClassObject>> orderedClassList = orderClassObjects();
//
//        for (List<AbstractClassObject> stage : orderedClassList) {
//            for (AbstractClassObject classObject : stage) {
//                classObject.VTableCodeGen(vtableOffset);
//                vtableOffset += classObject.getVTableSize();
//            }
//        }

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

    public void setInstanceofUsed() {
        isInstanceofUsed = true;
    }

    // ####################################################################################

    public void instanceOfCodeGen() {
        Label start = new Label("InstanceOf_start");
        Label endSucess = new Label("InstanceOf_sucess");
        Label endFailure = new Label("InstanceOf_failure");

        backend.addLabel(new Label("Code.InstanceOf"));

        backend.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(1)));
        backend.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), GPRegister.getR(0)));

        backend.addLabel(start);
        backend.addInstruction(new LOAD(new RegisterOffset(0, GPRegister.getR(0)), GPRegister.getR(1)));
        backend.addInstruction(new CMP(GPRegister.getR(0), GPRegister.getR(1)));
        backend.addInstruction(new BEQ(endSucess));
        backend.addInstruction(new CMP(new NullOperand(), GPRegister.getR(1)));
        backend.addInstruction(new BEQ(endFailure));
        backend.addInstruction(new BRA(start));

        backend.addLabel(endSucess);
        backend.addInstruction(new LOAD(new ImmediateInteger(1), GPRegister.getR(0)));
        backend.addInstruction(new RTS());

        backend.addLabel(endFailure);
        backend.addInstruction(new LOAD(new ImmediateInteger(0), GPRegister.getR(0)));
        backend.addInstruction(new RTS());
    }

    public void destroyObjectCodeGen() {
        VirtualRegister objectPointer = backend.getContextManager().operationStackPop();
        backend.addComment("destroy allocated object");
        backend.addInstruction(new CMP(new NullOperand(), objectPointer.requestPhysicalRegister()));
        backend.addInstruction(new BEQ(backend.getErrorsManager().getDereferencementNullLabel()));
        backend.addInstruction(new DEL(objectPointer.requestPhysicalRegister()));
    }
}
