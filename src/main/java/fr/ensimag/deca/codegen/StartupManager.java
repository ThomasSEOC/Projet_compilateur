package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Class responsible for code generation of startup sequence
 *
 * @author gl54
 * @date 10/01/2022
 */
public class StartupManager {
    private final CodeGenBackend backend;

    /**
     * create Startup manager, must only be call once by CodeGenBackend
     * @param backend compiler CodeGenBackend backend
     */
    public StartupManager(CodeGenBackend backend) {
        this.backend = backend;
    }

    /**
     * method responsible for main block stack check and initialisation, must be called after main block code generation
     */
    public void generateStartupCode() {
        // check stack overflow
        DecacCompiler compiler = backend.getCompiler();
        compiler.addFirst(new ADDSP(backend.getMaxStackSize()));
        compiler.addFirst(new BOV(backend.getErrorsManager().getStackOverflowLabel()));
        compiler.addFirst(new TSTO(backend.getMaxGlobalVAriablesSize()));
    }
}
