package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.AbstractIdentifier;

/**
 * abstract class for deca class representation
 */
public abstract class AbstractClassObject {
    private final ClassManager classManager;
    private int VTableOffset;

    /**
     * constructor for {@link AbstractClassObject}
     * @param classManager global class manager
     */
    public AbstractClassObject(ClassManager classManager) {
        this.classManager = classManager;
    }

    /**
     * getter for class manger
     * @return related class manager
     */
    protected ClassManager getClassManager() {
        return classManager;
    }

    /**
     * generate Vtable creation code for current object
     * @param offset current Vtable offset
     * @param generateSuperPointer if this condition is set, this method will generate superClass pointer in method table
     */
    abstract public void VTableCodeGen(int offset, boolean generateSuperPointer);

    /**
     * generate code for Init method generation
     */
    abstract public void structureInitCodeGen();

    /**
     * getter for VTable size
     * @return occupied memory space by Vtable for this object
     */
    abstract public int getVTableSize();

    /**
     * getter for structure size
     * @return occupied memory by instance structure of this object
     */
    abstract public int getStructureSize();

    /**
     * generate code for methods of this object
     */
    abstract public void methodsCodeGen();

    /**
     * generate code for method call of this object
     * @param abstractMethod called method
     */
    abstract public void callMethod(AbstractDeclMethod abstractMethod);

    /**
     * getter for method offset in VTable
     * @param abstractMethod called method
     * @return offset for this method
     */
    abstract public int getMethodOffset(AbstractDeclMethod abstractMethod);

    /**
     * getter for className
     * @return Identifier corresponding to this object class name
     */
    abstract public AbstractIdentifier getClassName();

    /**
     * setter for VTable offset
     * @param VTableOffset new VTable offset for this object in VTable
     */
    protected void setVTableOffset(int VTableOffset) {
        this.VTableOffset = VTableOffset;
    }

    /**
     * getter for VTable offset
     * @return offset for this object in VTable
     */
    public int getVTableOffset() {
        return VTableOffset;
    }

    /**
     * generate code for object instantiation
     */
    abstract public void createObjectCodeGen();
}
