package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Stack;

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
    public void VTableCodeGen(int offset, boolean generateSuperPointer) {

        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("init vtable for " + getNameClass().getName().getName());
        AbstractClassObject superObject = getClassManager().getClassObject(superClass);
        superObject.VTableCodeGen(offset, false);

        if (generateSuperPointer) {
            setVTableOffset(offset);
            backend.addInstruction(new LEA(new RegisterOffset(superObject.getVTableOffset(), Register.GB), GPRegister.getR(0)));
            backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, Register.GB)));
        }

        int i = offset + superObject.getVTableSize();
        for (AbstractDeclMethod method : methods.getList()) {
            Label codeLabel = new Label("Code." + nameClass.getName().getName() + "." + method.toString());
            backend.addInstruction(new LOAD(new LabelOperand(codeLabel), GPRegister.getR(0)));
            backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(i++, Register.GB)));
        }
    }

    @Override
    public void StructureInitCodeGen() {
        CodeGenBackend backend = getClassManager().getBackend();
        AbstractClassObject superObject = getClassManager().getClassObject(superClass);
        backend.getCompiler().addComment("Code for init of " + getNameClass().getName().getName());
        for (AbstractDeclMethod abstractMethod : getMethods().getList()) {
            backend.addLabel(new Label("Code." + getNameClass().getName().getName() + ".Init"));
            backend.addComment("code for fields initialization");
            backend.createContext();

            // get address of method table object
            backend.addInstruction(new LEA(new RegisterOffset(getVTableOffset(), Register.GB), GPRegister.getR(0)));
            // get object pointer
            VirtualRegister objectStructurePointer = backend.getContextManager().requestNewRegister();
            backend.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), objectStructurePointer.requestPhysicalRegister()));
            // set first word as pointer to object method table entry
            backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(0, objectStructurePointer.requestPhysicalRegister())));

            backend.getContextManager().operationStackPush(objectStructurePointer);
            fields.codeGenDecl(getClassManager(), this, superObject.getStructureSize());

            // recursion mais pas vraiment
            backend.addInstruction(new PUSH(objectStructurePointer.requestPhysicalRegister()));
            backend.addInstruction(new BSR(new Label("Code." + getSuperClass().getName().getName() + ".Init")));
            backend.addInstruction(new SUBSP(-1));

            backend.getStartupManager().generateStartupCode();
            backend.writeInstructions();
            backend.popContext();
        }
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
        CodeGenBackend backend = getClassManager().getBackend();
        backend.getCompiler().addComment("Code for methods of " + getNameClass().getName().getName());
        for (AbstractDeclMethod abstractMethod : getMethods().getList()) {
            DeclMethod method = (DeclMethod) abstractMethod;
            backend.addLabel(new Label("Code." + getNameClass().getName().getName() + "." + method.getName().getName()));
            backend.addComment(method.getName().getName().getName());
            backend.createContext();

            // add params
            ListDeclParam params = method.getParams();
            for (int i = 0; i < params.size(); i++) {
                DeclParam param = (DeclParam) params.getList().get(i);
                backend.addParam(param.getName().getName().getName(), -2 - i);
            }

            method.getBody().codeGen(backend.getCompiler());
            backend.getStartupManager().generateStartupCode();
            backend.writeInstructions();
            backend.popContext();
        }
    }

    public void select(String objectVariableString) {
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();

        // get register offset
        RegisterOffset registerOffset = getClassManager().getBackend().getVariableRegisterOffset(objectVariableString);

        // request new virtual register
        VirtualRegister register = getClassManager().getBackend().getContextManager().requestNewRegister();

        // check null pointer
        compiler.getCodeGenBackend().addInstruction(new LOAD(registerOffset, register.requestPhysicalRegister()));
        compiler.getCodeGenBackend().addInstruction(new CMP(new NullOperand(), register.requestPhysicalRegister()));
        compiler.getCodeGenBackend().addInstruction(new BEQ(getClassManager().getBackend().getErrorsManager().getDereferencementNullLabel()));

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

    @Override
    public void callMethod(AbstractDeclMethod abstractMethod) {
        CodeGenBackend backend = getClassManager().getBackend();
        DeclMethod method = (DeclMethod) abstractMethod;

        // use operation stack to get object and params in reverse order
        int paramsCount = method.getParams().size();
        Stack<VirtualRegister> params = new Stack<>();
        for (int i = 0; i < paramsCount; i++) {
            params.push(backend.getContextManager().operationStackPop());
        }

        backend.addComment("call method " + method.getName().getName());

        // space reservation
        backend.addInstruction(new ADDSP(paramsCount+1));

        // check object pointer
        VirtualRegister objectReference = params.peek();
        backend.addInstruction(new LOAD(objectReference.requestPhysicalRegister(), GPRegister.getR(0)));
        backend.addInstruction(new CMP(new NullOperand(), GPRegister.getR(0)));
        backend.addInstruction(new BEQ(backend.getErrorsManager().getDereferencementNullLabel()));

        // add params
        for (int i = 0; i < paramsCount; i++) {
            backend.addInstruction(new STORE(params.pop().requestPhysicalRegister(), new RegisterOffset(-i, Register.SP)));
        }

        // jump
        int offset = getMethodOffset(method);
        backend.addInstruction(new BSR(new RegisterOffset(offset, GPRegister.getR(0))));

        // free space
        backend.addInstruction(new SUBSP(paramsCount+1));
    }

    @Override
    public int getMethodOffset(AbstractDeclMethod abstractMethod) {
        DeclMethod method = (DeclMethod) abstractMethod;
        String name = method.getName().getName().getName();
        int i = getClassManager().getClassObject(getSuperClass()).getVTableSize();
        for (AbstractDeclMethod aMethod : getMethods().getList()) {
            DeclMethod testMethod = (DeclMethod) aMethod;
            if (Objects.equals(testMethod.getName().getName().getName(), name)) {
                return i;
            }
            i++;
        }

        // recursion
        return getClassManager().getClassObject(getSuperClass()).getMethodOffset(abstractMethod);
    }

    @Override
    public AbstractIdentifier getClassName() {
        return nameClass;
    }

    public void createObjectCodeGen() {
        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("create instance of class " + getNameClass().getName().getName());
        backend.addInstruction(new NEW(getStructureSize(), GPRegister.getR(0)));
        backend.addInstruction(new BOV(backend.getErrorsManager().getHeapOverflowLabel()));
        backend.addInstruction(new PUSH(GPRegister.getR(0)));
        backend.addInstruction(new BSR(new Label("Code." + getNameClass().getName().getName() + ".Init")));
        VirtualRegister objectPointer = backend.getContextManager().requestNewRegister();
        backend.addInstruction(new POP(objectPointer.requestPhysicalRegister()));
    }
}
