package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractDeclField;
import fr.ensimag.deca.tree.DeclField;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.util.*;

/**
 * Backend for codegen which include commonly used fields and methods across the entire codegen step
 *
 * @author gl54
 * @date 10/01/2022
 */
public class CodeGenBackend {
    private int maxStackSize = 0;
    private int maxGlobalVariablesSize = 0;

    private final Map<String, Integer> globalVariables;
    private final Stack<Map<String, Integer>> localVariables;
    private final Stack<Integer> localVariableSize;
    private final Stack<Integer> tempUseStackSize;

    private final List<Instruction> instructions;
    private final List<String> instructionsComments;
    private final List<String> comments;
    private final List<Label> labels;

    private final ErrorsManager errorsManager;
    private final StartupManager startupManager;
    private final DecacCompiler compiler;
    private final Stack<ContextManager> contextManagers;
    private final ClassManager classManager;

    private int ifStatementsCount = 0;
    private int whileStatementsCount = 0;
    private int orLabelsCount = 0;
    private final Stack<Label> trueBooleanLabel;
    private final Stack<Label> falseBooleanLabel;
    private boolean branchCondition;

    private boolean printHex;

    private int nextGraphId = 0;

    /**
     * create backend for specified compiler, must be called only once at the beginning of code generation step
     * @param compiler current compiler
     */
    public CodeGenBackend(DecacCompiler compiler) {
        this.compiler = compiler;

        globalVariables = new HashMap<>();
        localVariables = new Stack<>();
        localVariableSize = new Stack<>();
        tempUseStackSize = new Stack<>();
        instructions = new ArrayList<>();
        comments = new ArrayList<>();
        instructionsComments = new ArrayList<>();
        labels = new ArrayList<>();

        errorsManager = new ErrorsManager(this);
        startupManager = new StartupManager(this);
        contextManagers = new Stack<>();
        contextManagers.add(new ContextManager(this));
        classManager = new ClassManager(this);
        trueBooleanLabel = new Stack<>();
        falseBooleanLabel = new Stack<>();
        branchCondition = false;

        printHex = false;
    }

    public boolean NeedRegisterSave(int index) {
        for (int i = 0; i < contextManagers.size(); i++) {
            ContextManager contextManager = contextManagers.get(i);
            if (contextManager.isRegisterUsed(index)) {
                return true;
            }
        }
        return false;
    }

    /**
     * create a new graph bloc unique ID
     * @return new unique ID
     */
    public int requestnewGraphId() {
        return nextGraphId++;
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
    public int getMaxStackSize() {
        if (tempUseStackSize.size() != 0) {
            return localVariableSize.peek() + tempUseStackSize.peek();
        }
        return maxStackSize;
    }

    /**
     * getter for current global variables max size
     * @return maxGlobalVariablesSize
     */
    public int getContextDataSize() {
        if (tempUseStackSize.size() != 0) {
            return localVariableSize.peek();
        }
        return maxGlobalVariablesSize;
    }

    /**
     * add a declared variable
     * @param name variable name
     */
    public void addVariable(String name) {
        // if local context exists
        if (localVariables.size() != 0) {
            localVariableSize.push(localVariableSize.pop() + 1);
            localVariables.peek().put(name, localVariableSize.peek());
        }
        // add to global variables
        else {
            globalVariables.put(name, ++maxGlobalVariablesSize);
            maxStackSize++;
        }
    }

    /**
     * add a param in a local context
     * @param name param name
     * @param offset offset from LB
     */
    public void addParam(String name, int offset) {
        localVariables.peek().put(name, offset);
    }

    /**
     * get the offset count from GB for the specified global variable
     * @param name string used to identify variable
     * @return offset count from LB
     */
    public int getVariableOffset(String name) {
        // search in local context if exists
        if (localVariables.size() != 0) {
            if (localVariables.peek().containsKey(name)) {
                return localVariables.peek().get(name);
            }
        }
        // search in global context
        return classManager.getVtableOffset() + globalVariables.get(name) - 1;
    }

    /**
     * create register offset for the requested variable
     * @param name variable name
     * @return register offset for the specified variable
     */
    public RegisterOffset getVariableRegisterOffset(String name) {
        // search in local context if exists
        if (localVariables.size() != 0) {
            if (localVariables.peek().containsKey(name)) {
                return new RegisterOffset(localVariables.peek().get(name), Register.LB);
            }
        }

        // search if fields
        AbstractClassObject currentObject = classManager.getCurrentObject();
        if (currentObject != null) {
            while (!(currentObject instanceof DefaultObject)) {
                ClassObject object = (ClassObject) currentObject;
                for (AbstractDeclField abstractField : object.getFields().getList()) {
                    DeclField field = (DeclField) abstractField;
                    if (Objects.equals(field.getField().getName().getName(), name)) {
                        addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), GPRegister.getR(0)));
                        return new RegisterOffset(object.getFieldOffset(name), GPRegister.getR(0));
                    }
                }
                currentObject = getClassManager().getClassObject(((ClassObject) currentObject).getSuperClass());
            }
        }

        // search in global context
        return new RegisterOffset(classManager.getVtableOffset() + globalVariables.get(name) - 1, Register.GB);
    }

    /**
     * get if variable is in local context of a method or not
     * @param name variable name
     * @return true if variable is in local context
     */
    public boolean isVariableLocal(String name) {
        // search in local context if exists
        if (localVariables.size() != 0) {
            return localVariables.peek().containsKey(name);
        }
        return false;
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
    public ContextManager getContextManager(){ return contextManagers.peek(); }

    /**
     * getter for classManager
     * @return current class manager
     */
    public ClassManager getClassManager() { return classManager; }

    /**
     * create a new method context
     */
    public void createContext() {
        localVariableSize.push(0);
        localVariables.push(new HashMap<>());
        tempUseStackSize.push(0);
        contextManagers.push(new ContextManager(this));
    }

    /**
     * pop method context
     */
    public void popContext() {
        localVariableSize.pop();
        localVariables.pop();
        tempUseStackSize.pop();
        contextManagers.pop().destroy();
    }

    /**
     * add instruction to backend buffer
     * @param instruction to add
     */
    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
        instructionsComments.add(null);
        comments.add(null);
        labels.add(null);
    }

    /**
     * add commented instruction to backend buffer
     * @param instruction to add
     * @param comment to add
     */
    public void addInstruction(Instruction instruction, String comment) {
        instructions.add(instruction);
        instructionsComments.add(comment);
        comments.add(null);
        labels.add(null);

        if ((instruction instanceof BranchInstruction) || (instruction instanceof BSR)) {
            if (getContextManager().getLastStoreRegister() != null) {
                getContextManager().getLastStoreRegister().destroy(true);
                getContextManager().setLastStoreRegister(null, null);
            }
        }
    }

    /**
     * add instruction to beginning of backend buffer
     * @param instructionsArray instructions to add
     * @param commentsArray comments to add
     */
    public void addInstructionFirst(List<Instruction> instructionsArray, List<String> commentsArray) {
        for (int i = instructionsArray.size() - 1; i >= 0; i--) {
            instructions.add(0, instructionsArray.get(i));
            instructionsComments.add(0, commentsArray.get(i));
            comments.add(0, null);
            labels.add(0, null);
        }
    }

    /**
     * add comment to beginning to backend buffer
     * @param comment to add
     */
    public void addCommentFirst(String comment) {
        comments.add(0, comment);
        labels.add(0, null);
        instructions.add(0, null);
        instructionsComments.add(0, null);
    }

    /**
     * add label to backend buffer
     * @param label to add
     */
    public void addLabel(Label label) {
        labels.add(label);
        instructions.add(null);
        instructionsComments.add(null);
        comments.add(null);
    }

    /**
     * add comment to backend buffer
     * @param comment to add
     */
    public void addComment(String comment) {
        comments.add(comment);
        instructions.add(null);
        instructionsComments.add(null);
        labels.add(null);
    }

    /**
     * copy backend buffer to destination file
     */
    public void writeInstructions() {
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i) != null) {
                if (!Objects.equals(instructionsComments.get(i), null)) {
                    compiler.addInstruction(instructions.get(i), instructionsComments.get(i));
                }
                else {
                    compiler.addInstruction(instructions.get(i));
                }
            }
            else if (comments.get(i) != null) {
                compiler.addComment(comments.get(i));
            }
            else {
                compiler.addLabel(labels.get(i));
            }
        }
        instructions.clear();
        instructionsComments.clear();
        comments.clear();
        labels.clear();
    }
}
