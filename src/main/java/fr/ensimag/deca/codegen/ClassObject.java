package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class ClassObject extends AbstractClassObject {
    private final AbstractIdentifier nameClass;
    private final AbstractIdentifier superClass;
    private final ListDeclMethod methods;
    private final ListDeclField fields;

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
        setVTableOffset(offset);

        DecacCompiler compiler = getClassManager().getBackend().getCompiler();
        AbstractClassObject superObject = getClassManager().getClassObject(superClass);
        superObject.VTableCodeGen(offset);

        compiler.addInstruction(new LEA(new RegisterOffset(superObject.getVTableOffset(), Register.GB), GPRegister.getR(0)));
        compiler.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, Register.GB)));

        int i = offset + superObject.getVTableSize();
        for (AbstractDeclMethod method : methods.getList()) {
            Label codeLabel = new Label("Code." + nameClass.getName().getName() + "." + method.toString());
            compiler.addInstruction(new LOAD(new LabelOperand(codeLabel), GPRegister.getR(0)));
            compiler.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(i++, Register.GB)));
        }
    }

    @Override
    public void StructureCodeGen(int offset) {
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();

        AbstractClassObject superObject = getClassManager().getClassObject(superClass);
        superObject.StructureCodeGen(offset);

        compiler.addInstruction(new LOAD(new RegisterOffset(getVTableOffset(), Register.GB), GPRegister.getR(0)));
        compiler.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, GPRegister.LB)));

        int i = offset + superObject.getStructureSize();

        fields.codeGen(getClassManager(), nameClass, i);
    }

    @Override
    public int getVTableSize() {
        // attention si redefinition de equals
        return methods.size() + getClassManager().getClassObject(superClass).getVTableSize();
    }

    @Override
    public int getStructureSize() {
        return fields.size() + getClassManager().getClassObject(superClass).getStructureSize();
    }

    @Override
    public void methodsCodeGen() {
        methods.codeGen(getClassManager(), nameClass);
    }
}
