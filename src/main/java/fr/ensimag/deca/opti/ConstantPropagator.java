package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.*;

import java.util.*;

/**
 * class responsible for constant propagation on a control flow graph
 */
public class ConstantPropagator {
    private final ControlFlowGraph graph;
    private final List<SSAVariable> toProcess;
    private final Map<SSAVariable, Constant> constants;
    private final Map<String, Integer> deletedSSAVariables;

    /**
     * constructor for ConstantPropagator
     * @param graph related graph
     */
    public ConstantPropagator(ControlFlowGraph graph) {
        this.graph = graph;
        toProcess = new ArrayList<>();
        constants = new HashMap<>();
        deletedSSAVariables = new HashMap<>();
    }

    /**
     * fold constants of assigns
     */
    private void foldAssign() {
        // iterate on each bloc
        for (AbstractCodeBloc bloc : graph.getBlocs()) {
            // iterate on each instruction
            ListInst newListInst = new ListInst();
            for (AbstractInst instruction : bloc.getInstructions().getList()) {
                // process only assign
                if (instruction instanceof Assign) {
                    Assign assign = (Assign) instruction;
                    // process only local variable assign
                    if (assign.getLeftOperand() instanceof Identifier) {
                        Identifier leftOperand = (Identifier) assign.getLeftOperand();
                        if (!graph.getIsMethod() || graph.getBackend().isVariableLocal(leftOperand.getName().getName())) {
                            Constant constant = assign.getRightOperand().getConstant(graph.getCompiler());
                            if (constant != null) {
                                boolean isInMerge = false;
                                for (SSAMerge merge : graph.getSsaProcessor().getMerges().get(leftOperand.getName().getName())) {
                                    if (merge.getOperands().contains(leftOperand.getSsaVariable())) {
                                        isInMerge = true;
                                        break;
                                    }
                                }
                                if (!isInMerge) {
                                    // result is a constant
                                    constants.put(leftOperand.getSsaVariable(), constant);
                                    toProcess.add(leftOperand.getSsaVariable());
                                    graph.getSsaProcessor().removeIdentifier(leftOperand.getSsaVariable(), leftOperand);
                                }
                                else {
                                    newListInst.add(instruction);
                                }
                            } else {
                                newListInst.add(instruction);
                            }
                        }
                        else {
                            newListInst.add(instruction);
                        }
                    } else {
                        newListInst.add(instruction);
                    }
                } else {
                    newListInst.add(instruction);
                }
            }
            bloc.setInstructions(newListInst);
        }
    }

    /**
     * fold constants of variables declarations
     */
    private void foldDeclVar() {
        // iterate on each variable declaration
        ListDeclVar newListDeclVar = new ListDeclVar();
        for (AbstractDeclVar var : graph.getDeclVariables().getList()) {
            // only process variables with initialization
            DeclVar variable = (DeclVar) var;
            if (variable.getInitialization() instanceof NoInitialization) {
                newListDeclVar.add(variable);
                deletedSSAVariables.put(variable.getVarName().getName().getName(), 1);
            } else {
                Initialization initialization = (Initialization) variable.getInitialization();
                Constant constant = initialization.getExpression().getConstant(graph.getCompiler());
                if (constant == null) {
                    newListDeclVar.add(variable);
                    deletedSSAVariables.put(variable.getVarName().getName().getName(), 0);
                } else {
                    // initialization is a constant
                    Identifier variableIdentifier = (Identifier) variable.getVarName();
                    constants.put(variableIdentifier.getSsaVariable(), constant);
                    toProcess.add(variableIdentifier.getSsaVariable());

                    // process merges
                    Map<String, Set<SSAMerge>> merges = graph.getSsaProcessor().getMerges();
                    if (merges.get(variable.getVarName().getName().getName()).size() > 0) {
                        newListDeclVar.add(variable);
                    } else {
                        newListDeclVar.add(new DeclVar(variable.getType(), variable.getVarName(), new NoInitialization()));
                    }
                    deletedSSAVariables.put(variable.getVarName().getName().getName(), 1);
                }

            }
        }
        if (graph.getDeclVariables().size() > 0) {
            graph.setDeclVariables(newListDeclVar);
        }
    }

    /**
     * propagate constants throw the graph
     */
    public void process() {
        // fold variables constants
        foldDeclVar();

        // fold assigns
        foldAssign();

        // use a stack for SSA variables to process
        while (toProcess.size() > 0) {
            // get SSA variable and constant from stacks
            SSAVariable toDelete = toProcess.remove(toProcess.size() - 1);
            Constant constant = constants.remove(toDelete);
            Set<InstructionIdentifiers> usages = graph.getSsaProcessor().getInstructionIdentifiers(toDelete);

            // increment deleted SSA variable for the related variable
            deletedSSAVariables.replace(toDelete.getName(), deletedSSAVariables.get(toDelete.getName()) + 1);

            // process usages
            for (InstructionIdentifiers identifiers : usages) {
                // propagate constant in read identifiers
                for (Identifier identifier : identifiers.getReadIdentifiers()) {
                    if (identifier.getSsaVariable().equals(toDelete)) {
                        identifier.setConstant(constant);
                    }
                }

                // check if instruction is an assign
                AbstractInst instruction = identifiers.getInstruction();
                if (instruction instanceof Assign) {
                    Assign assign = (Assign) instruction;
                    Constant nextConstant = assign.getRightOperand().getConstant(graph.getCompiler());
                    if ((nextConstant != null) && (assign.getLeftOperand() instanceof Identifier)) {
                        // a constant can be propagated
                        Identifier variableIdentifier = (Identifier) assign.getLeftOperand();
                        constants.put(variableIdentifier.getSsaVariable(), nextConstant);
                        toProcess.add(variableIdentifier.getSsaVariable());
                        graph.getSsaProcessor().removeIdentifier(variableIdentifier.getSsaVariable(), variableIdentifier);
                        instruction.setUseless();
                    }
                }
            }

            // search for merges and process it
            Set<SSAMerge> merges = graph.getSsaProcessor().getMerges().get(toDelete.getName());
            Set<SSAMerge> toRemove = new HashSet<>();
            for (SSAMerge merge : merges) {
                if (merge.getOperands().contains(toDelete)) {
                    merge.removeOperand(toDelete);
                    // check if merge can be removed and propagate constants in results
                    if (merge.getOperands().size() == 1) {
                        SSAVariable operand = (SSAVariable) merge.getOperands().toArray()[0];
                        SSAVariable result = merge.getResult();
                        if (operand == result) {
                            constants.put(result, constant);
                            toProcess.add(result);
                            toRemove.add(merge);
                        }
                    }
                    else if (merge.getOperands().size() == 0) {
                        SSAVariable result = merge.getResult();
                        constants.put(result, constant);
                        toProcess.add(result);
                        toRemove.add(merge);
                    }
                }
            }
            for (SSAMerge merge : toRemove) {
                graph.getSsaProcessor().removeMerge(toDelete.getName(), merge);
            }
        }

        // remove unused variables
        postProcess();
    }

    /**
     * remove unused variables
     */
    private void postProcess() {
        // iterate on each variable
        ListDeclVar newListDeclVar = new ListDeclVar();
        for (AbstractDeclVar var : graph.getDeclVariables().getList()) {
            // check if it can be removed
            String varName = ((DeclVar) var).getVarName().getName().getName();
            if (deletedSSAVariables.containsKey(varName)) {
                // remove it
                if (deletedSSAVariables.get(varName) != graph.getSsaProcessor().getSSAVariablesCount(varName)) {
                    newListDeclVar.add(var);
                }
            } else {
                newListDeclVar.add(var);
            }
        }
        if (graph.getDeclVariables().size() > 0) {
            graph.setDeclVariables(newListDeclVar);
        }
    }
}
