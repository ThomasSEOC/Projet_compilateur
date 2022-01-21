package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.DeclMethod;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.Objects;
import java.util.Stack;

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
//        compiler.getCodeGenBackend().addInstruction(new TSTO(2));
//        compiler.getCodeGenBackend().addInstruction(new BOV(compiler.getCodeGenBackend().getErrorsManager().getStackOverflowLabel()));
        backend.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(0)));
        backend.addInstruction(new CMP(new RegisterOffset(-3, Register.LB), GPRegister.getR(0)));
        backend.addInstruction(new SEQ(GPRegister.getR(0)));
        backend.addInstruction(new RTS());
    }

    /**
     * generate code of VTable creation for default Object
     * @param offset current Vtable offset
     * @param generateSuperPointer if this condition is set, this method will generate superClass pointer in method table
     */
    @Override
    public void VTableCodeGen(int offset, boolean generateSuperPointer) {
        DecacCompiler compiler = getClassManager().getBackend().getCompiler();
        if (generateSuperPointer) {
            setVTableOffset(offset);
            compiler.getCodeGenBackend().addComment("init vtable for default object");
            compiler.getCodeGenBackend().addInstruction(new LOAD(new NullOperand(), GPRegister.getR(0)));
            compiler.getCodeGenBackend().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(offset, GPRegister.GB)));
        }
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

    @Override
    public void methodsCodeGen() {
        CodeGenBackend backend = getClassManager().getBackend();
        backend.addComment("Code for methods of Object :");
        structureInitCodeGen();
        codeObjectEqualsCodeGen();
    }

    /**
     * generate code for default object method call
     * @param abstractMethod called method (only equals is accepted)
     */
    @Override
    public void callMethod(AbstractDeclMethod abstractMethod) {
        DeclMethod method = (DeclMethod) abstractMethod;
        if (Objects.equals(method.getName().getName().getName(), "equals")) {
            CodeGenBackend backend = getClassManager().getBackend();

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
            backend.addInstruction(new BSR(new RegisterOffset(1, Register.GB)));

            // free space
            backend.addInstruction(new SUBSP(paramsCount+1));
        }
        else {
            throw new UnsupportedOperationException("error method doesn't exists");
        }
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
}
