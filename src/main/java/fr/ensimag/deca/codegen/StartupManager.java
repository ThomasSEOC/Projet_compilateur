package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.ArrayList;
import java.util.List;

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

    public void generateStartupCode(int contextSaveSpace) {
        // check stack overflow
        List<Instruction> instructions = new ArrayList<>();
        List<String> comments = new ArrayList<>();
        instructions.add(new TSTO(contextSaveSpace + backend.getMaxStackSize()));
        instructions.add(new BOV(backend.getErrorsManager().getStackOverflowLabel()));
        if (backend.getContextDataSize() > 0) {
            instructions.add(new ADDSP(backend.getContextDataSize()));
        }
        comments.add(null);
        comments.add(null);
        comments.add(null);

        backend.addInstructionFirst(instructions, comments);
    }

    /**
     * method responsible for main block stack check and initialisation, must be called after main block code generation
     */
    public void generateStartupCode() {
        generateStartupCode(0);
    }
}
