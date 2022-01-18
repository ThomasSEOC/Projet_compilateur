package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.Objects;

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

    public ListDeclMethod getMethods() { return methods; }

    public ListDeclField getFields() { return fields; }

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

        fields.codeGenDecl(getClassManager(), this, i);
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

    public void select(String objectVariableString) {
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();

        // get register offset
        RegisterOffset registerOffset = getClassManager().getBackend().getVariableRegisterOffset(objectVariableString);

        // request new virtual register
        VirtualRegister register = getClassManager().getBackend().getContextManager().requestNewRegister();

        // check null pointer
        compiler.addInstruction(new LOAD(registerOffset, register.requestPhysicalRegister()));
        compiler.addInstruction(new CMP(new NullOperand(), register.requestPhysicalRegister()));
        compiler.addInstruction(new BEQ(getClassManager().getBackend().getErrorsManager().getDereferencementNullLabel()));

        getClassManager().getBackend().getContextManager().operationStackPush(register);
    }

    public int getFieldOffset(String fieldName) {
        AbstractClassObject superObject = getClassManager().getClassObject(superClass);

        // if field in current class
        int i = 0;
        for (AbstractDeclField field : fields.getList()) {
            if (Objects.equals(field.toString(), fieldName)) {
                return i +  superObject.getStructureSize();
            }
            i++;
        }

        return ((ClassObject)superObject).getFieldOffset(fieldName);
    }
}
