package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassManager {
    private final CodeGenBackend backend;
    private List<AbstractClassObject> classList;
    private int vtableOffset;

    public ClassManager(CodeGenBackend backend) {
        this.backend = backend;
        vtableOffset = 0;
        classList = new ArrayList<>();
        classList.add(new DefaultObject(this));
    }

    public CodeGenBackend getBackend() {
        return backend;
    }

    public int getVtableOffset() {
        return vtableOffset;
    }

    public void addClass(AbstractIdentifier nameClass, AbstractIdentifier superClass, ListDeclMethod methods, ListDeclField fields) {
        ClassObject classDefinition = new ClassObject(this, nameClass, superClass, methods, fields);
        classList.add(classDefinition);
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
        List<List<AbstractClassObject>> ordredClassList = orderClassObjects();

        for (List<AbstractClassObject> stage : ordredClassList) {
            for (AbstractClassObject classObject : stage) {
                classObject.VTableCodeGen(vtableOffset);
                vtableOffset += classObject.getVTableSize();
            }
        }
    }

    public void methodsCodeGen() {
        backend.getCompiler().addComment("code for methods");
        for (AbstractClassObject classObject : classList) {
            classObject.methodsCodeGen();
        }
    }
}
