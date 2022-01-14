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
        backend.getCompiler().addComment("error messages");
        addSTackOverflowError();
    }

    /**
     * getter for label to which jump to enter stack overflow error
     * @return stackOverflowLabel
     */
    public Label getStackOverflowLabel() { return stackOverflowLabel; }

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
}
