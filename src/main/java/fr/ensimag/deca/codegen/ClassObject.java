package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.ListDeclField;
import fr.ensimag.deca.tree.ListDeclMethod;

public class ClassObject extends AbstractClassObject {
    private AbstractIdentifier nameClass;
    private AbstractIdentifier superClass;
    private ListDeclMethod methods;
    private ListDeclField fields;

    public ClassObject(ClassManager classManager, AbstractIdentifier nameClass, AbstractIdentifier superClass, ListDeclMethod methods, ListDeclField fields) {
        super(classManager);
        this.nameClass = nameClass;
        this.superClass = superClass;
        this.methods = methods;
        this.fields = fields;
    }

    public AbstractIdentifier getNameClass() {
        return nameClass;
    }

    public AbstractIdentifier getSuperClass() {
        return superClass;
    }

    @Override
    public void VTableCodeGen(int offset) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public int getVTableSize() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void methodsCodeGen() {
        methods.codeGen(nameClass);
    }
}
