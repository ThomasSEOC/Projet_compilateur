package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.*;

public class ClassManager {
    private final CodeGenBackend backend;
    private List<AbstractClassObject> classList;
    private final Map<AbstractIdentifier, AbstractClassObject> classMap;
    private int vtableOffset = 0;

    public ClassManager(CodeGenBackend backend) {
        this.backend = backend;
        vtableOffset = 1;
        classList = new ArrayList<>();
        classMap = new HashMap<AbstractIdentifier, AbstractClassObject>();
        classList.add(new DefaultObject(this));
    }

    public CodeGenBackend getBackend() {
        return backend;
    }

    public int getVtableOffset() {
        return vtableOffset;
    }

    public void addClass(AbstractIdentifier nameClass, AbstractIdentifier superClass, ListDeclMethod methods, ListDeclField fields) {
        ClassObject classObject = new ClassObject(this, nameClass, superClass, methods, fields);
        classList.add(classObject);
        classMap.put(nameClass, classObject);
    }

    public AbstractClassObject getClassObject(AbstractIdentifier nameClass) {
        return classMap.get(nameClass);
    }

    private List<List<AbstractClassObject>> orderClassObjects() {
        // need to optimize

        List<AbstractClassObject> done = new ArrayList<>();
        List<List<AbstractClassObject>> list = new ArrayList<>();

        // first element is default object
        List<AbstractClassObject> currentObjects = new ArrayList<AbstractClassObject>();
        AbstractClassObject defaultObject = classList.remove(0);
        currentObjects.add(defaultObject);
        list.add(currentObjects);
        done.add(defaultObject);

        while (classList.size() > 0) {
            List<AbstractClassObject> lastObjects = currentObjects;
            currentObjects = new ArrayList<>();
            List<AbstractClassObject> toRemove = new ArrayList<>();
            for (AbstractClassObject abstractClassObject : classList) {
                ClassObject classObject = (ClassObject) abstractClassObject;
                AbstractIdentifier superClassIdentifier = classObject.getSuperClass();

                boolean found = false;
                int i = 0;
                while ((i < lastObjects.size()) && (!found)) {
                    if (lastObjects.get(i).getClassName() == superClassIdentifier) {
                        found = true;
                    }
                }

                if (found) {
                    currentObjects.add(abstractClassObject);
                    done.add(abstractClassObject);
                    toRemove.add(abstractClassObject);
                }
            }

            for (AbstractClassObject AbstractClassObject : toRemove) {
                classList.remove(AbstractClassObject);
            }

            list.add(currentObjects);
        }

        classList = done;

        return list;
    }

    public void VTableCodeGen() {
        List<List<AbstractClassObject>> orderedClassList = orderClassObjects();

        backend.getCompiler().addComment("VTABLE INIT");

        for (List<AbstractClassObject> stage : orderedClassList) {
            for (AbstractClassObject classObject : stage) {
                classObject.VTableCodeGen(vtableOffset);
                vtableOffset += classObject.getVTableSize();
            }
        }

        backend.writeInstructions();
    }

    public void methodsCodeGen() {
        backend.getCompiler().addComment("METHODS");
        for (AbstractClassObject classObject : classList) {
            classObject.methodsCodeGen();
        }
        backend.writeInstructions();
    }

    public void instanceOfCodeGen(AbstractClassObject targetObject) {
        // version primitive non fonctionnelle
        Label start = new Label("InstanceOf" + targetObject.getClassName().getName().getName() + "_start");
        Label endSucess = new Label("InstanceOf" + targetObject.getClassName().getName().getName() + "_sucess");
        Label endFailure = new Label("InstanceOf" + targetObject.getClassName().getName().getName() + "_failure");
        Label end = new Label("InstanceOf" + targetObject.getClassName().getName().getName() + "_end");

        backend.addComment("instance of " + targetObject.getClassName().getName().getName());
        backend.addInstruction(new LEA(new RegisterOffset(targetObject.getVTableOffset(), Register.GB), GPRegister.getR(0)));
        VirtualRegister object = backend.getContextManager().operationStackPop();
        backend.addInstruction(new LOAD(object.requestPhysicalRegister(), GPRegister.getR(1)));
        backend.addLabel(start);
        backend.addInstruction(new LOAD(new RegisterOffset(0, GPRegister.getR(0)), GPRegister.getR(1)));
        backend.addInstruction(new CMP(GPRegister.getR(0), GPRegister.getR(1)));
        backend.addInstruction(new BEQ(endSucess));
        backend.addInstruction(new CMP(new NullOperand(), GPRegister.getR(1)));
        backend.addInstruction(new BEQ(endFailure));
        backend.addInstruction(new BRA(start));

        // pas sûr
        object.destroy();

        VirtualRegister result = backend.getContextManager().requestNewRegister();
        backend.addLabel(endSucess);
        backend.addInstruction(new LOAD(new ImmediateInteger(1), result.requestPhysicalRegister()));
        backend.addInstruction(new BRA(end));
        backend.addLabel(endFailure);
        backend.addInstruction(new LOAD(new ImmediateInteger(0), result.requestPhysicalRegister()));
        backend.addLabel(end);
    }

    public void destroyObjectCodeGen() {
        VirtualRegister objectPointer = backend.getContextManager().operationStackPop();
        backend.addComment("destroy allocated object");
        backend.addInstruction(new CMP(new NullOperand(), objectPointer.requestPhysicalRegister()));
        backend.addInstruction(new BEQ(backend.getErrorsManager().getDereferencementNullLabel()));
        backend.addInstruction(new DEL(objectPointer.requestPhysicalRegister()));
    }
}
