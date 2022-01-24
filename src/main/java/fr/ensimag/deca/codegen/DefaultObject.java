package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.DeclMethod;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * class responsible for default Object representation
 */
public class DefaultObject extends AbstractClassObject {
    private final Label codeObjectEquals;

    /**
     * constructor for DefaultObject
     * @param classManager code generation class manager
     */
    public DefaultObject(ClassManager classManager) {
        super(classManager);
        codeObjectEquals = new Label("code.Object.equals");
    }

    /**
     * generate code for default equals method
     */
    private void codeObjectEqualsCodeGen() {
        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("equals method");
        backend.addLabel(codeObjectEquals);

        // first parameter is current Object address
        // second parameter is other object address
        backend.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(0)));
        backend.addInstruction(new CMP(new RegisterOffset(-3, Register.LB), GPRegister.getR(0)));
        backend.addInstruction(new SEQ(GPRegister.getR(0)));
        backend.addInstruction(new RTS());
    }

    /**
     * generate code of VTable creation for default Object
     * @param offset current Vtable offset
     */
    @Override
    public void VTableCodeGen(int offset) {
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();
        setVTableOffset(offset);
        compiler.getCodeGenBackend().addComment("init vtable for default object");
        compiler.getCodeGenBackend().addInstruction(new LOAD(new NullOperand(), GPRegister.getR(0)));
        compiler.getCodeGenBackend().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, GPRegister.GB)));
        compiler.getCodeGenBackend().addInstruction(new LOAD(new LabelOperand(codeObjectEquals), GPRegister.getR(0)));
        compiler.getCodeGenBackend().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset + 1, GPRegister.GB)));
    }

    /**
     * generation code for default Object structure initialization
     */
    @Override
    public void structureInitCodeGen() {
        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("structure init");
        backend.addLabel(new Label("Code.Object.Init"));
        backend.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(1)));
        backend.addInstruction(new LEA(new RegisterOffset(getVTableOffset(), Register.GB), GPRegister.getR(0)));
        backend.addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(0, GPRegister.getR(1))));
        backend.addInstruction(new RTS());
    }

    /**
     * getter for VTable size
     * @return occupied VTable memory by default Object
     */
    @Override
    public int getVTableSize() {
        return 2;
    }

    /**
     * getter for default Object instance structure size
     * @return occupied memory space by default Object instance structure
     */
    @Override
    public int getStructureSize() {
        return 1;
    }

    /**
     * generate code for default object methods
     */
    @Override
    public void methodsCodeGen() {
        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("Code for methods of Object :");
        structureInitCodeGen();
        codeObjectEqualsCodeGen();

        backend.writeInstructions();
    }

    /**
     * getter for offset in method table for specified method
     * @param abstractMethod called method
     * @return offset from default Object pointer base in VTable
     */
    @Override
    public int getMethodOffset(AbstractDeclMethod abstractMethod) {
        DeclMethod method = (DeclMethod) abstractMethod;
        if (Objects.equals(method.getName().getName().getName(), "equals")) {
            return 1;
        }
        else {
            throw new UnsupportedOperationException("error method doesn't exists");
        }
    }

    /**
     * getter for className
     * @return identifier corresponding to default Object
     */
    @Override
    public AbstractIdentifier getClassName() {
        return new Identifier(getClassManager().getBackend().getCompiler().getSymbolTable().create("Object"));
    }

    /**
     * generate code for default Object instantiation
     */
    @Override
    public void createObjectCodeGen() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * generate code for default object fields
     */
    @Override
    public void codeGenFieldDecl() {
        // nothing
    }

    /**
     * create method labels map
     * @return method labels map
     */
    protected Map<String,Label> VTableSearchLabels() {
        if (methodsLabels == null) {
            methodsLabels = new HashMap<>();
            methodsOffsets = new HashMap<>();
            methodsLabels.put("equals", codeObjectEquals);
            methodsOffsets.put("equals", 1);
        }

        return methodsLabels;
    }
}
