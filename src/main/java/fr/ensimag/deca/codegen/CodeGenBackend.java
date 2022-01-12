package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;

import java.util.Stack;

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

    private int ifStatementsCount = 0;
    private int whileStatementsCount = 0;
    private int orLabelsCount = 0;
    private Stack<Label> trueBooleanLabel;
    private Stack<Label> falseBooleanLabel;
    private boolean branchCondition;

    /**
     * create backend for specified compiler, must be called only once at the beginning of code generation step
     * @param compiler current compiler
     */
    public CodeGenBackend(DecacCompiler compiler) {
        errorsManager = new ErrorsManager(this);
        startupManager = new StartupManager(this);
        contextManager = new ContextManager(this, compiler.getCompilerOptions().getRegistersCount());
        trueBooleanLabel = new Stack<>();
        falseBooleanLabel = new Stack<>();
        branchCondition = false;
        this.compiler = compiler;
    }

    public void incIfStatementCount() {
        ifStatementsCount++;
    }

    public void incWhileStatementCount() { whileStatementsCount++; }

    public void incOrLabelsCount() {
        orLabelsCount++;
    }

    public int getIfStatementsCount() {
        return ifStatementsCount;
    }

    public int getWhileStatementsCount() { return whileStatementsCount; }

    public int getOrLabelsCount() {
        return orLabelsCount;
    }

    public Label getCurrentTrueBooleanLabel() {
        return trueBooleanLabel.peek();
    }

    public Label getCurrentFalseBooleanLabel() {
        return falseBooleanLabel.peek();
    }

    public void popCurrentTrueBooleanLabel() {
        trueBooleanLabel.pop();
    }

    public void popCurrentFalseBooleanLabel() {
        falseBooleanLabel.pop();
    }

    public void trueBooleanLabelPush(Label label) {
        trueBooleanLabel.push(label);
    }

    public void falseBooleanLabelPush(Label label) {
        falseBooleanLabel.push(label);
    }

    public void setBranchCondition(Boolean branchCondition) {
        this.branchCondition = branchCondition;
    }

    public boolean getBranchCondition() {
        return branchCondition;
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
     * increment the maximum stack size
     */
    public void incMaxStackSize() { maxStackSize++; }

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

    /**
     * getter for program ContextManager
     * @return contextManager
     */

    public ContextManager getContextManager(){ return contextManager; }
}
