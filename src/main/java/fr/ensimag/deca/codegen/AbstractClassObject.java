package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.AbstractIdentifier;

public abstract class AbstractClassObject {
    private final ClassManager classManager;
    private int VTableOffset;

    public AbstractClassObject(ClassManager classManager) {
        this.classManager = classManager;
    }

    protected ClassManager getClassManager() {
        return classManager;
    }

    abstract public void VTableCodeGen(int offset);

    abstract public void StructureInitCodeGen();

    abstract public int getVTableSize();

    abstract public int getStructureSize();

    abstract public void methodsCodeGen();

    abstract public void callMethod(AbstractDeclMethod abstractMethod);

    abstract public int getMethodOffset(AbstractDeclMethod abstractMethod);

    public AbstractIdentifier getClassName() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    protected void setVTableOffset(int VTableOffset) {
        this.VTableOffset = VTableOffset;
    }

    public int getVTableOffset() {
        return VTableOffset;
    }
}
