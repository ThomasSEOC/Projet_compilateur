package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractIdentifier;
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
        compiler.addComment("Code for default equals method");
        compiler.addLabel(codeObjectEquals);

        // first parameter is current Object address
        // second parameter is other object address
        compiler.addInstruction(new TSTO(2));
        compiler.addInstruction(new BOV(compiler.getCodeGenBackend().getErrorsManager().getStackOverflowLabel()));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(0)));
        compiler.addInstruction(new CMP(new RegisterOffset(-3, Register.LB), GPRegister.getR(0)));
        compiler.addInstruction(new SEQ(GPRegister.getR(0)));
        compiler.addInstruction(new RTS());
    }

    @Override
    public void VTableCodeGen(int offset) {
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();
        compiler.addComment("init vtable for default object");
        compiler.addInstruction(new LOAD(new NullOperand(), GPRegister.getR(0)));
        compiler.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, GPRegister.GB)));
        compiler.addInstruction(new LOAD(new LabelOperand(codeObjectEquals), GPRegister.getR(0)));
        compiler.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset + 1, GPRegister.GB)));
    }

    @Override
    public int getVTableSize() {
        return 2;
    }

    @Override
    public void methodsCodeGen() {
        codeObjectEqualsCodeGen();
    }
}
