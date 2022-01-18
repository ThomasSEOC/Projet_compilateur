package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class Fields {
    private final ClassObject object;

    public Fields(ClassObject object) {
        this.object = object;
    }

    public void codeGenDecl(int offset) {
        DecacCompiler compiler = object.getClassManager().getBackend().getCompiler();
//        compiler.addInstruction(new LOAD(new RegisterOffset(offset, Register.LB), GPRegister.getR(1)));
        int i = offset;
        for (AbstractDeclField abstractField : object.getFields().getList()) {
            DeclField field = (DeclField) abstractField;
            compiler.getCodeGenBackend().addComment("init " + object.getNameClass().getName().getName() + "." + field.getField().getName().getName());
            if (field.getInit() instanceof Initialization) {
                // use assign
                Assign assign = new Assign(field.getField(), ((Initialization) field.getInit()).getExpression());
                AssignOperation operator = new AssignOperation(object.getClassManager().getBackend(), assign);
                operator.doOperation();
            }
            else {
                // copy 0 to field
                compiler.getCodeGenBackend().addInstruction(new LOAD(new ImmediateInteger(0), GPRegister.getR(0)));
                compiler.getCodeGenBackend().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(i, Register.LB)));
            }
            i++;
        }
    }

    public void codeGenAssign(String fieldName) {
        int offset = object.getFieldOffset(fieldName);

        DecacCompiler compiler = object.getClassManager().getBackend().getCompiler();
        VirtualRegister result = compiler.getCodeGenBackend().getContextManager().operationStackPop();
        VirtualRegister addressRegister = compiler.getCodeGenBackend().getContextManager().operationStackPop();

        compiler.getCodeGenBackend().addInstruction(new LOAD(new RegisterOffset(offset, addressRegister.requestPhysicalRegister()), result.requestPhysicalRegister()));
    }

    public void codeGenRead(String fieldName) {
        int offset = object.getFieldOffset(fieldName);

        DecacCompiler compiler = object.getClassManager().getBackend().getCompiler();
        VirtualRegister register = compiler.getCodeGenBackend().getContextManager().operationStackPop();

        compiler.getCodeGenBackend().addInstruction(new LOAD(new RegisterOffset(offset, register.requestPhysicalRegister()), register.requestPhysicalRegister()));

        compiler.getCodeGenBackend().getContextManager().operationStackPush(register);
    }
}
