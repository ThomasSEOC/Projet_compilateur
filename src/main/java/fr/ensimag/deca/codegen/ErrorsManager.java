package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

/**
 * Class responsible for error messages management and code generation
 *
 * @author gl54
 * @date 10/01/2022
 */
public class ErrorsManager {
    private final CodeGenBackend backend;
    private final Label stackOverflowLabel = new Label("stack_overflow_error");
    private final Label dereferencementNullLabel = new Label("dereferencement_null_error");
    private final Label heapOverflowLabel = new Label("heap_overflow_error");
    private boolean isDereferencementNullLabelUsed = false;
    private boolean isHeapOverflowLabelUsed = false;

    /**
     * create Error manager, must only be call once by CodeGenBackend
     * @param backend compiler CodeGenBackend backend
     */
    public ErrorsManager(CodeGenBackend backend) {
        this.backend = backend;
    }

    /**
     * add assembly for error handlers, must be called after main code generation
     */
    public void addErrors() {
        backend.getCompiler().addComment("###############################################################");
        backend.getCompiler().addComment("ERRORS");

        // this error is always present
        addSTackOverflowError();

        // these errors are only added if needed
        if (isDereferencementNullLabelUsed) {
            addDereferencementNullError();
        }
        if (isHeapOverflowLabelUsed) {
            addHeapOverflowError();
        }
    }

    /**
     * getter for label to which jump to enter stack overflow error
     * @return stackOverflowLabel
     */
    public Label getStackOverflowLabel() { return stackOverflowLabel; }

    /**
     * getter for label to which jump in case of null pointer exception
     * @return dereferencementNullLabel
     */
    public Label getDereferencementNullLabel() {
        isDereferencementNullLabelUsed = true;
        return dereferencementNullLabel;
    }

    /**
     * getter for label to which jump in case of heap overflow
     * @return heapOverflowLabel
     */
    public Label getHeapOverflowLabel() {
        isHeapOverflowLabelUsed = true;
        return heapOverflowLabel;
    }

    /**
     * add assembly code for stack overflow error handler
     */
    private void addSTackOverflowError() {
        DecacCompiler compiler = backend.getCompiler();
        compiler.addLabel(stackOverflowLabel);
        compiler.addInstruction(new WSTR("Erreur : dépassement de pile"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }

    /**
     * add assembly code for null pointer exception handler
     */
    private void addDereferencementNullError() {
        DecacCompiler compiler = backend.getCompiler();
        compiler.addLabel(dereferencementNullLabel);
        compiler.addInstruction(new WSTR("Erreur : dereferencement de null"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }

    /**
     * add assembly code for heap overflow error handler
     */
    private void addHeapOverflowError() {
        DecacCompiler compiler = backend.getCompiler();
        compiler.addLabel(heapOverflowLabel);
        compiler.addInstruction(new WSTR("Erreur : le tas est plein"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
}
