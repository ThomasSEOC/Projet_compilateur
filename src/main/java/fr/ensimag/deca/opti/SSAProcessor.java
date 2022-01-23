package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractDeclVar;
import fr.ensimag.deca.tree.DeclVar;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.Initialization;

import java.util.*;

public class SSAProcessor {
    private final ControlFlowGraph graph;
    private final Map<String,Integer> lastVariablesIds;
    private final Map<SSAVariable, Set<InstructionIdentifiers>> usages;
    private final Map<AbstractCodeBloc, Map<String,SSAMerge>> waitingFusionCodeBlocs;
    private final Map<String, Set<SSAMerge>> merges;

    public SSAProcessor(ControlFlowGraph graph) {
        this.graph = graph;
        this.lastVariablesIds = new HashMap<>();
        this.usages = new HashMap<>();
        this.waitingFusionCodeBlocs = new HashMap<>();
        this.merges = new HashMap<>();

//        for (String variableName : graph.getBackend().getVariables()) {
//            lastVariablesIds.put(variableName, 1);
//        }
    }

    public Set<String> getVariablesNames() {
        return lastVariablesIds.keySet();
    }

    public int getSSAVariablesCount(String varName) {
        return lastVariablesIds.get(varName);
    }

    public Map<String, Set<SSAMerge>> getMerges() {
        return merges;
    }

    public void removeMerge(String varName, SSAMerge merge) {
        merges.get(varName).remove(merge);
    }

    public Set<InstructionIdentifiers> getInstructionIdentifiers(SSAVariable variable) {
        return usages.get(variable);
    }

    public void removeIdentifier(SSAVariable variable, Identifier identifier) {
        usages.get(variable).removeIf(ident -> ident.getWriteIdentifier() == identifier);
    }

    private void startMerge(AbstractCodeBloc bloc, Map<String,SSAVariable> localSSA) {
        waitingFusionCodeBlocs.put(bloc, new HashMap<>());

        for (String variable : localSSA.keySet()) {
            SSAVariable oldVariable = localSSA.get(variable);

            lastVariablesIds.replace(variable, lastVariablesIds.get(variable)+1);
            SSAVariable newVariable = new SSAVariable(variable, lastVariablesIds.get(variable));
            localSSA.replace(variable, newVariable);
            usages.put(newVariable, new HashSet<>());

            waitingFusionCodeBlocs.get(bloc).put(variable, new SSAMerge(oldVariable, newVariable));
        }
    }

    private void continueMerge(AbstractCodeBloc bloc, Map<String,SSAVariable> localSSA) {
        for (String name : localSSA.keySet()) {
            waitingFusionCodeBlocs.get(bloc).get(name).addOperand(localSSA.get(name));
        }
    }

    private void endMerge(AbstractCodeBloc bloc, Map<String,SSAVariable> localSSA) {
        continueMerge(bloc, localSSA);

        for (String name : localSSA.keySet()) {
            SSAMerge merge = waitingFusionCodeBlocs.get(bloc).get(name);
            merges.get(name).add(merge);
        }

        waitingFusionCodeBlocs.remove(bloc);
    }

    private void processInstructionIdentifiers(InstructionIdentifiers identifiers, Map<String,SSAVariable> localSSA) {
        // process read identifiers
        for (Identifier identifier : identifiers.getReadIdentifiers()) {
            identifier.setSsaVariable(localSSA.get(identifier.getName().getName()));
            usages.get(localSSA.get(identifier.getName().getName())).add(identifiers);
        }

        // process write identifier if exists
        Identifier writeIdentifier = identifiers.getWriteIdentifier();
        if (writeIdentifier != null) {
            String name = writeIdentifier.getName().getName();
            lastVariablesIds.replace(name, lastVariablesIds.get(name)+1);

            SSAVariable newVariable = new SSAVariable(name, lastVariablesIds.get(name));

            localSSA.replace(name, newVariable);
            usages.put(newVariable, new HashSet<>());
            usages.get(newVariable).add(identifiers);

            writeIdentifier.setSsaVariable(newVariable);
        }
    }

    private void processBloc(AbstractCodeBloc bloc, Map<String,SSAVariable> localSSA) {
        if (bloc instanceof BranchCodeBloc) {
            BranchCodeBloc branchCodeBloc = (BranchCodeBloc) bloc;
            processInstructionIdentifiers(branchCodeBloc.getConditionIdentifiers(), localSSA);
        }

        // check if there are not done in arcs
        if (bloc.getInArcs().size() > 1) {
            boolean needWait = false;
            for (int i = 0; i < bloc.getInArcs().size(); i++) {
                if (!graph.getDoneBlocs().contains(bloc.getInArcs().get(i).getStart())) {
                    needWait = true;
                    break;
                }
            }

            if (needWait) {
                // check if start fuse is already done
                if (!waitingFusionCodeBlocs.containsKey(bloc)) {
                    startMerge(bloc, localSSA);
                }
                else {
                    continueMerge(bloc, localSSA);
                }
            }
            else {
                // can end fuse
                endMerge(bloc, localSSA);
                graph.addDoneBloc(bloc);
            }
        }
        else {
            graph.addDoneBloc(bloc);
        }

        for (InstructionIdentifiers identifiers : bloc.getInstructionIdentifiersList()) {
            processInstructionIdentifiers(identifiers, localSSA);
        }

    }

    private Map<String,SSAVariable> processDeclVar() {
        Map<String,SSAVariable> initSSA = new HashMap<>();
        for (AbstractDeclVar var : graph.getDeclVariables().getList()) {
            DeclVar variable = (DeclVar) var;
            lastVariablesIds.put(variable.getVarName().getName().getName(), 1);

            Identifier lIdentifier = (Identifier) variable.getVarName();
            SSAVariable ssaVariable = new SSAVariable(lIdentifier.getName().getName(), 1);
            lIdentifier.setSsaVariable(ssaVariable);

            initSSA.put(lIdentifier.getName().getName(), ssaVariable);
            usages.put(ssaVariable, new HashSet<>());
            merges.put(lIdentifier.getName().getName(), new HashSet<>());

            if (variable.getInitialization() instanceof Initialization) {
                Initialization initialization = (Initialization) variable.getInitialization();
                InstructionIdentifiers identifiers = new InstructionIdentifiers(initialization.getExpression());
                processInstructionIdentifiers(identifiers, initSSA);
            }
        }
        return initSSA;
    }

    public void process() {
        Map<String,SSAVariable> initSSA = processDeclVar();

        graph.clearDoneBlocs();
        graph.addDoneBloc(graph.getStopBloc());

        Stack<AbstractCodeBloc> toProcessBlocs = new Stack<>();
        Stack<Map<String,SSAVariable>> localSSAs = new Stack<>();
        toProcessBlocs.push(graph.getStartBloc());
        localSSAs.push(initSSA);

//        localSSAs.push(new HashMap<>());
//        for (String variableName : graph.getBackend().getVariables()) {
//            SSAVariable variable = new SSAVariable(variableName, 1);
//            localSSAs.peek().put(variableName, variable);
//            usages.put(variable, new HashSet<>());
//            merges.put(variableName, new HashSet<>());
//        }

        while (toProcessBlocs.size() > 0) {
            AbstractCodeBloc bloc = toProcessBlocs.pop();
            Map<String,SSAVariable> localSSA = localSSAs.pop();

            processBloc(bloc, localSSA);

            for (int i = 0; i < bloc.getOutArcs().size(); i++) {
                AbstractCodeBloc toPushBloc = bloc.getOutArcs().get(i).getStop();
                if (!graph.getDoneBlocs().contains(toPushBloc)) {
                    toProcessBlocs.push(toPushBloc);
                    Map<String, SSAVariable> copy = new HashMap<>(localSSA);
                    localSSAs.push(copy);
                }
            }
        }
    }
}
