package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Backend for codegen which include commonly used fields and methods across the entire codegen step
 *
 * @author gl54
 * @date 10/01/2022
 */
public class CodeGenBackend {
    private int maxStackSize = 0;
    private int maxGlobalVariablesSize = 0;

    private final Map<String, Integer> variables;

    private final ErrorsManager errorsManager;
    private final StartupManager startupManager;
    private final DecacCompiler compiler;
    private final ContextManager contextManager;
    private final ClassManager classManager;

    private int ifStatementsCount = 0;
    private int whileStatementsCount = 0;
    private int orLabelsCount = 0;
    private final Stack<Label> trueBooleanLabel;
    private final Stack<Label> falseBooleanLabel;
    private boolean branchCondition;

    private boolean printHex;

    /**
     * create backend for specified compiler, must be called only once at the beginning of code generation step
     * @param compiler current compiler
     */
    public CodeGenBackend(DecacCompiler compiler) {
        this.compiler = compiler;

        variables = new HashMap<>();

        errorsManager = new ErrorsManager(this);
        startupManager = new StartupManager(this);
        contextManager = new ContextManager(this);
        classManager = new ClassManager(this);
        trueBooleanLabel = new Stack<>();
        falseBooleanLabel = new Stack<>();
        branchCondition = false;

        printHex = false;
    }

    /**
     * increment the number of if statements
     */
    public void incIfStatementCount() {
        ifStatementsCount++;
    }

    /**
     * increment the number of while statements
     */
    public void incWhileStatementCount() { whileStatementsCount++; }

    /**
     * increment the number of Or labels
     */
    public void incOrLabelsCount() {
        orLabelsCount++;
    }

    /**
     * getter for ifStatementsCount
     * @return the number of if statements
     */
    public int getIfStatementsCount() {
        return ifStatementsCount;
    }

    /**
     * getter for whileStatementsCount
     * @return the number of while statements
     */
    public int getWhileStatementsCount() { return whileStatementsCount; }

    /**
     * getter for orLabelsCount
     * @return the number of Or labels
     */
    public int getOrLabelsCount() {
        return orLabelsCount;
    }

    /**
     * getter for top of trueBooleanLabel stack
     * @return the current true condition label to branch on
     */
    public Label getCurrentTrueBooleanLabel() {
        return trueBooleanLabel.peek();
    }

    /**
     * getter for top of falseBooleanLabel stack
     * @return the current false condition label to branch on
     */
    public Label getCurrentFalseBooleanLabel() {
        return falseBooleanLabel.peek();
    }

    /**
     * remove the last element of trueBooleanLabel stack
     */
    public void popCurrentTrueBooleanLabel() {
        trueBooleanLabel.pop();
    }

    /**
     * remove the last element of falseBooleanLabel stack
     */
    public void popCurrentFalseBooleanLabel() {
        falseBooleanLabel.pop();
    }

    /**
     * push a label to trueBooleanLabel stack
     * @param label label to branch on in case of true condition
     */
    public void trueBooleanLabelPush(Label label) {
        trueBooleanLabel.push(label);
    }

    /**
     * push a label to falseBooleanLabel stack
     * @param label label to branch on in case of false condition
     */
    public void falseBooleanLabelPush(Label label) {
        falseBooleanLabel.push(label);
    }

    /**
     * set if branch must be done in case of true or false condition
     * @param branchCondition result of a condition to branch
     */
    public void setBranchCondition(Boolean branchCondition) {
        this.branchCondition = branchCondition;
    }

    /**
     * getter for branchCondition
     * @return the result of a condition to branch
     */
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
     * @return maxGlobalVariablesSize
     */
    public int getMaxGlobalVAriablesSize() { return maxGlobalVariablesSize; }

    /**
     * add a declared global variable
     * @param name string used to identify variable
     */
    public void addVariable(String name) {
        variables.put(name, ++maxGlobalVariablesSize);
    }

    /**
     * get the offset count from GB for the specified global variable
     * @param name string used to identify variable
     * @return offset count form GB
     */
    public int getVariableOffset(String name) {
        return variables.get(name);
    }

    /**
     * increment the maximum stack size
     */
    public void incMaxStackSize() { maxStackSize++; }

    /**
     * getter for printHex
     * @return true if float must be displayed in hex format, false otherwise
     */
    public boolean getPrintHex() { return printHex; }

    /**
     * set if float must be displayed in hex format
     * @param printHex true if float must be displayed in hex format, false otherwise
     */
    public void setPrintHex(boolean printHex) { this.printHex = printHex; }

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

    public ClassManager getClassManager() { return classManager; }
}
