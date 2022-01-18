package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

public class DefaultObject extends AbstractClassObject {
    private final Label codeObjectEquals;

    public DefaultObject(ClassManager classManager) {
        super(classManager);
        codeObjectEquals = new Label("code.Object.equals");
    }

    private void codeObjectEqualsCodeGen() {
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();
        compiler.getCodeGenBackend().addComment("Code for default equals method");
        compiler.getCodeGenBackend().addLabel(codeObjectEquals);

        // first parameter is current Object address
        // second parameter is other object address
        compiler.getCodeGenBackend().addInstruction(new TSTO(2));
        compiler.getCodeGenBackend().addInstruction(new BOV(compiler.getCodeGenBackend().getErrorsManager().getStackOverflowLabel()));
        compiler.getCodeGenBackend().addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(0)));
        compiler.getCodeGenBackend().addInstruction(new CMP(new RegisterOffset(-3, Register.LB), GPRegister.getR(0)));
        compiler.getCodeGenBackend().addInstruction(new SEQ(GPRegister.getR(0)));
        compiler.getCodeGenBackend().addInstruction(new RTS());
    }

    @Override
    public void VTableCodeGen(int offset) {
        setVTableOffset(offset);
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();
        compiler.getCodeGenBackend().addComment("init vtable for default object");
        compiler.getCodeGenBackend().addInstruction(new LOAD(new NullOperand(), GPRegister.getR(0)));
        compiler.getCodeGenBackend().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, GPRegister.GB)));
        compiler.getCodeGenBackend().addInstruction(new LOAD(new LabelOperand(codeObjectEquals), GPRegister.getR(0)));
        compiler.getCodeGenBackend().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset + 1, GPRegister.GB)));
    }

    @Override
    public void StructureCodeGen(int offset) {
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();
        compiler.getCodeGenBackend().addInstruction(new LOAD(new RegisterOffset(getVTableOffset(), Register.GB), GPRegister.getR(0)));
        compiler.getCodeGenBackend().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, GPRegister.LB)));
    }

    @Override
    public int getVTableSize() {
        return 2;
    }

    @Override
    public int getStructureSize() {
        return 1;
    }

    @Override
    public void methodsCodeGen() {
        codeObjectEqualsCodeGen();
    }
}
