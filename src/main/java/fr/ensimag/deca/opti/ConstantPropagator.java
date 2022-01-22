package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.*;

import java.util.*;

public class ConstantPropagator {
    private final ControlFlowGraph graph;
    private final List<SSAVariable> toProcess;
    private final Map<SSAVariable, Constant> constants;
    private final Map<String, Integer> deletedSSAVariables;

    public ConstantPropagator(ControlFlowGraph graph) {
        this.graph = graph;
        toProcess = new ArrayList<>();
        constants = new HashMap<>();
        deletedSSAVariables = new HashMap<>();
    }

    private void foldAssign() {
        for (AbstractCodeBloc bloc : graph.getBlocs()) {
            ListInst newListInst = new ListInst();
            for (AbstractInst instruction : bloc.getInstructions().getList()) {
                if (instruction instanceof Assign) {
                    Assign assign = (Assign) instruction;

                    if (assign.getLeftOperand() instanceof Identifier) {
                        Constant constant = assign.getRightOperand().getConstant(graph.getCompiler());
                        if (constant != null) {
                            Identifier leftOperand = (Identifier) assign.getLeftOperand();
                            constants.put(leftOperand.getSsaVariable(), constant);
                            toProcess.add(leftOperand.getSsaVariable());
                            Set<InstructionIdentifiers> usages = graph.getSsaProcessor().getInstructionIdentifiers(leftOperand.getSsaVariable());
                            usages.removeIf(identifiers -> identifiers.getWriteIdentifier() == leftOperand);
                        }
                        else {
                            newListInst.add(instruction);
                        }
                    }
                    else {
                        newListInst.add(instruction);
                    }
                }
                else {
                    newListInst.add(instruction);
                }
            }
            bloc.setInstructions(newListInst);
        }
    }

    private void foldDeclVar() {
        ListDeclVar newListDeclVar = new ListDeclVar();
        for (AbstractDeclVar var : graph.getDeclVariables().getList()) {
            DeclVar variable = (DeclVar) var;

            if (variable.getInitialization() instanceof NoInitialization) {
                newListDeclVar.add(variable);
            }
            else {
                Initialization initialization = (Initialization) variable.getInitialization();
                Constant constant = initialization.getExpression().getConstant(graph.getCompiler());
                if (constant == null) {
                    newListDeclVar.add(variable);
                }
                else {
                    Identifier variableIdentifier = (Identifier) variable.getVarName();
                    constants.put(variableIdentifier.getSsaVariable(), constant);
                    toProcess.add(variableIdentifier.getSsaVariable());

                    Map<String, Set<SSAMerge>> merges = graph.getSsaProcessor().getMerges();
                    if (merges.get(variable.getVarName().getName().getName()).size() > 0) {
                        newListDeclVar.add(variable);
                    }
                    else {
                        newListDeclVar.add(new DeclVar(variable.getType(), variable.getVarName(), new NoInitialization()));
                    }
                }

            }
        }
        if (graph.getDeclVariables().size() > 0) {
            graph.setDeclVariables(newListDeclVar);
        }
    }

    public void process() {
        foldDeclVar();

        foldAssign();

        while (toProcess.size() > 0) {
            SSAVariable toDelete = toProcess.remove(toProcess.size() - 1);
            Constant constant = constants.remove(toDelete);
            Set<InstructionIdentifiers> usages = graph.getSsaProcessor().getInstructionIdentifiers(toDelete);

            if (!deletedSSAVariables.containsKey(toDelete.getName())) {
                deletedSSAVariables.put(toDelete.getName(), 1);
            }
            else {
                deletedSSAVariables.replace(toDelete.getName(), deletedSSAVariables.get(toDelete.getName()) + 1);
            }

            for (InstructionIdentifiers identifiers : usages) {
                for (Identifier identifer : identifiers.getReadIdentifiers()) {
                    if (identifer.getSsaVariable().equals(toDelete)) {
                        identifer.setConstant(constant);
                    }
                }

                AbstractInst instruction = identifiers.getInstruction();
                if (instruction instanceof Assign) {
                    Assign assign = (Assign) instruction;
                    Constant nextConstant = assign.getRightOperand().getConstant(graph.getCompiler());
                    if ((nextConstant != null) && (assign.getLeftOperand() instanceof Identifier)) {
                        Identifier variableIdentifier = (Identifier) assign.getLeftOperand();
                        constants.put(variableIdentifier.getSsaVariable(), constant);
                        toProcess.add(variableIdentifier.getSsaVariable());
                        usages.removeIf(ident -> ident.getWriteIdentifier() == variableIdentifier);
                        instruction.setUseless();
                    }
                }
            }

            // search for merges
            Set<SSAMerge> merges = graph.getSsaProcessor().getMerges().get(toDelete.getName());
            for (SSAMerge merge : merges) {
                if (merge.getOperands().contains(toDelete)) {
                    merge.removeOperand(toDelete);
                    if (merge.getOperands().size() == 1) {
                        SSAVariable operand = (SSAVariable) merge.getOperands().toArray()[0];
                        SSAVariable result = merge.getResult();
                        if (operand == result) {
                            constants.put(result, constant);
                            toProcess.add(result);
                            merges.remove(merge);
                        }
                    }
                }
            }
        }

        postProcess();
    }

    private void postProcess() {
        ListDeclVar newListDeclVar = new ListDeclVar();
        for (AbstractDeclVar var : graph.getDeclVariables().getList()) {
            String varName = ((DeclVar) var).getVarName().getName().getName();
            if (deletedSSAVariables.containsKey(varName)) {
                if (deletedSSAVariables.get(varName) != graph.getSsaProcessor().getSSAVariablesCount(varName)) {
                    newListDeclVar.add(var);
                }
            }
            else {
                newListDeclVar.add(var);
            }
        }
        if (graph.getDeclVariables().size() > 0) {
            graph.setDeclVariables(newListDeclVar);
        }
    }
}
