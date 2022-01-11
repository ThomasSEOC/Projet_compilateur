package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;

/**
 * Backend for codegen which include commonly used fields and methods across the entire codegen step
 *
 * @author gl54
 * @date 10/01/2022
 */
public class CodeGenBackend {
    private int maxStackSize = 0;
    private int maxGlobalVAriablesSize = 0;

    private final ErrorsManager errorsManager;
    private final StartupManager startupManager;
    private final DecacCompiler compiler;
    private final ContextManager contextManager;

    /**
     * create backend for specified compiler, must be called only once at the beginning of code generation step
     * @param compiler current compiler
     */
    public CodeGenBackend(DecacCompiler compiler) {
        errorsManager = new ErrorsManager(this);
        startupManager = new StartupManager(this);
        contextManager = new ContextManager(this, compiler.getCompilerOptions().getRegistersCount());
        this.compiler = compiler;
    }

    /**
     * getter for current max stack size
     * @return maxStackSize
     */
    public int getMaxStackSize() { return maxStackSize; }

    /**
     * getter for current global variables max size
     * @return maxGlobalVAriablesSize
     */
    public int getMaxGlobalVAriablesSize() { return maxGlobalVAriablesSize; }

    /**
     * increment the number of global variables
     */
    public void incMaxGlobalVAriablesSize() { maxGlobalVAriablesSize++; }

    /**
     * getter for program ErrorManager
     * @return errorsManager
     */
    public ErrorsManager getErrorsManager() { return errorsManager; }

    /**
     * getter for program StartupManager
     * @return startupManager
     */
    public StartupManager getStartupManager() { return startupManager; }

    /**
     * getter for program DecaCompiler
     * @return compiler
     */
    public DecacCompiler getCompiler() { return compiler; }
}
