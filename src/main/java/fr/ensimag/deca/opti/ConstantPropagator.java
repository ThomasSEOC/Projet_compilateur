package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.*;

import java.util.*;

public class ConstantPropagator {
    private final ControlFlowGraph graph;
    private final List<SSAVariable> toProcess;
    private final Map<SSAVariable, Constant> constants;

    public ConstantPropagator(ControlFlowGraph graph) {
        this.graph = graph;
        toProcess = new ArrayList<>();
        constants = new HashMap<>();
    }

    private void foldDeclVar() {
        ListDeclVar newListDeclVar = new ListDeclVar();
        for (AbstractDeclVar var : graph.getDeclVariables().getList()) {
            DeclVar variable = (DeclVar) var;

            if (variable.getInitialization() instanceof NoInitialization) {
                newListDeclVar.add(variable);
            }

            Initialization initialization = (Initialization) variable.getInitialization();
            Constant constant = initialization.getExpression().getConstant(graph.getCompiler());
            if (constant == null) {
                newListDeclVar.add(variable);
            }

            Identifier variableIdentifier = (Identifier) variable.getVarName();
            constants.put(variableIdentifier.getSsaVariable(), constant);
            toProcess.add(variableIdentifier.getSsaVariable());
        }
        graph.setDeclVariables(newListDeclVar);
    }

    public void process() {
        foldDeclVar();

        while (toProcess.size() > 0) {
            SSAVariable toDelete = toProcess.remove(toProcess.size() - 1);
            Constant constant = constants.remove(toDelete);
            Set<InstructionIdentifiers> usages = graph.getSsaProcessor().getInstructionIdentifiers(toDelete);

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
                    }
                }
            }
        }
    }
}
