package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.InstanceOf;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * class responsible for instanceof call code generation
 */
public class InstanceofOperation extends AbstractOperation {

    /**
     * constructor for Instanceof Operation
     * @param backend global code generation backend
     * @param expression operation related expression
     */
    public InstanceofOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * generate code for instanceof call
     */
    @Override
    public void doOperation() {
        InstanceOf expr = (InstanceOf) getExpression();

        // reserve space in stack
        getCodeGenBackEnd().addInstruction(new ADDSP(2));

        // get structure
        Identifier objectIdentifier = (Identifier) expr.getObjectE();
        int structureOffset = getCodeGenBackEnd().getVariableOffset(objectIdentifier.getName().getName());

        // check structure
        getCodeGenBackEnd().addInstruction(new LOAD(new RegisterOffset(structureOffset, Register.LB), GPRegister.getR(0)));
        if (!getCodeGenBackEnd().getCompiler().getCompilerOptions().getNoCheckStatus()) {
            getCodeGenBackEnd().addInstruction(new CMP(new NullOperand(), GPRegister.getR(0)));
            getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getErrorsManager().getDereferencementNullLabel()));
        }

        // store implicit param
        getCodeGenBackEnd().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(0, Register.SP)));

        // get target pointer
        Identifier targetIdentifier = (Identifier) expr.getTargetType();
        int VTableOffset = getCodeGenBackEnd().getClassManager().getClassObject(targetIdentifier.getName().getName()).getVTableOffset();

        // store target object
        getCodeGenBackEnd().addInstruction(new LEA(new RegisterOffset(VTableOffset, Register.GB), GPRegister.getR(0)));
        getCodeGenBackEnd().addInstruction(new STORE(GPRegister.getR(0), new RegisterOffset(-1, Register.SP)));

        // call method
        getCodeGenBackEnd().addInstruction(new BSR(getCodeGenBackEnd().getClassManager().getInstanceofLabel()));

        // delete used stack space
        getCodeGenBackEnd().addInstruction(new SUBSP(2));
    }

    /**
     * call instanceof and print result
     */
    @Override
    public void print() {
        doOperation();

        // move result from R0 to R1
        getCodeGenBackEnd().addInstruction(new LOAD(GPRegister.getR(0), GPRegister.getR(1)));

        // print
        getCodeGenBackEnd().addInstruction(new WINT());
    }
}
