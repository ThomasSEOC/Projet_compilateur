package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractIdentifier;

abstract class AbstractClassObject {
    private final ClassManager classManager;

    public AbstractClassObject(ClassManager classManager) {
        this.classManager = classManager;
    }

    protected ClassManager getClassManager() {
        return classManager;
    }

    abstract public void VTableCodeGen(int offset);

    abstract public int getVTableSize();

    abstract public void methodsCodeGen();

    public AbstractIdentifier getClassName() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
