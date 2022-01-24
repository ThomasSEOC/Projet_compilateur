package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractDeclVar;
import fr.ensimag.deca.tree.DeclVar;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.Initialization;

import java.util.*;

/**
 * class responsible for transformation of a control flow graph into SSA form
 */
public class SSAProcessor {
    private final ControlFlowGraph graph;
    private final Map<String,Integer> lastVariablesIds;
    private final Map<SSAVariable, Set<InstructionIdentifiers>> usages;
    private final Map<AbstractCodeBloc, Map<String,SSAMerge>> waitingFusionCodeBlocs;
    private final Map<String, Set<SSAMerge>> merges;

    /**
     * constructor for SSAProcessor
     * @param graph related control flow graph
     */
    public SSAProcessor(ControlFlowGraph graph) {
        this.graph = graph;
        this.lastVariablesIds = new HashMap<>();
        this.usages = new HashMap<>();
        this.waitingFusionCodeBlocs = new HashMap<>();
        this.merges = new HashMap<>();
    }

    /**
     * getter for the set of variables names
     * @return set of all variables names
     */
    public Set<String> getVariablesNames() {
        return lastVariablesIds.keySet();
    }

    /**
     * getter for the number of SSA variables for the specified variable name
     * @param varName variable name
     * @return number of SSA variables related to variable name
     */
    public int getSSAVariablesCount(String varName) {
        return lastVariablesIds.get(varName);
    }

    /**
     * getter for all generated merges (Phi functions)
     * @return Map of all merges ordered by variable name
     */
    public Map<String, Set<SSAMerge>> getMerges() {
        return merges;
    }

    /**
     * remove a merge form the merges
     * @param varName variable name
     * @param merge to remove
     */
    public void removeMerge(String varName, SSAMerge merge) {
        merges.get(varName).remove(merge);
    }

    /**
     * getter for Set of InstructionIdentifiers related to the specified SSA variable
     * @param variable SSA variable
     * @return Set of InstructionIdentifiers related to the specified SSA variable
     */
    public Set<InstructionIdentifiers> getInstructionIdentifiers(SSAVariable variable) {
        return usages.get(variable);
    }

    /**
     * remove a written identifier from usages of the specified SSA variable
     * @param variable SSA variable
     * @param identifier to remove from usages
     */
    public void removeIdentifier(SSAVariable variable, Identifier identifier) {
        usages.get(variable).removeIf(ident -> ident.getWriteIdentifier() == identifier);
    }

    /**
     * start a merge (Phi function)
     * @param bloc     branch bloc
     * @param localSSA current last SSA variables
     */
    private void startMerge(AbstractCodeBloc bloc, Map<String, SSAVariable> localSSA) {
        // add bloc to internal structure for merges not already finished
        waitingFusionCodeBlocs.put(bloc, new HashMap<>());

        // create a merge for each variable
        for (String variable : localSSA.keySet()) {
            SSAVariable oldVariable = localSSA.get(variable);

            lastVariablesIds.replace(variable, lastVariablesIds.get(variable) + 1);
            SSAVariable newVariable = new SSAVariable(variable, lastVariablesIds.get(variable));
            localSSA.replace(variable, newVariable);
            usages.put(newVariable, new HashSet<>());

            waitingFusionCodeBlocs.get(bloc).put(variable, new SSAMerge(oldVariable, newVariable));
        }
    }

    /**
     * continue a merge by adding a new input
     * @param bloc branch bloc
     * @param localSSA current last SSA variables
     */
    private void continueMerge(AbstractCodeBloc bloc, Map<String, SSAVariable> localSSA) {
        // add each SSA variable from bloc to merges operands
        for (String name : localSSA.keySet()) {
            waitingFusionCodeBlocs.get(bloc).get(name).addOperand(localSSA.get(name));
        }
    }

    /**
     * end a merge form a branch bloc
     * @param bloc branch bloc
     * @param localSSA current last SSA variables
     */
    private void endMerge(AbstractCodeBloc bloc, Map<String,SSAVariable> localSSA) {
        // add input
        continueMerge(bloc, localSSA);

        // end each merge
        for (String name : localSSA.keySet()) {
            SSAMerge merge = waitingFusionCodeBlocs.get(bloc).get(name);
            merges.get(name).add(merge);
        }

        // remove bloc from waiting blocs
        waitingFusionCodeBlocs.remove(bloc);
    }

    /**
     * extract SSa variable from an instruction
     * @param identifiers instructions identifiers related to the instruction
     * @param localSSA current local last SSA variables
     */
    private void processInstructionIdentifiers(InstructionIdentifiers identifiers, Map<String,SSAVariable> localSSA) {
        // process read identifiers
        for (Identifier identifier : identifiers.getReadIdentifiers()) {
            if (!graph.getIsMethod() || graph.getBackend().isVariableLocal(identifier.getName().getName())) {
                if (usages.containsKey(localSSA.get(identifier.getName().getName()))) {
                    identifier.setSsaVariable(localSSA.get(identifier.getName().getName()));
                    usages.get(localSSA.get(identifier.getName().getName())).add(identifiers);
                }
            }
        }

        // process write identifier if exists
        Identifier writeIdentifier = identifiers.getWriteIdentifier();
        if (writeIdentifier != null) {
            if ((!graph.getIsMethod()) || graph.getBackend().isVariableLocal(writeIdentifier.getName().getName())) {
                // need to create a new SSA variable
                String name = writeIdentifier.getName().getName();
                lastVariablesIds.replace(name, lastVariablesIds.get(name)+1);

                SSAVariable newVariable = new SSAVariable(name, lastVariablesIds.get(name));

                localSSA.replace(name, newVariable);
                usages.put(newVariable, new HashSet<>());
                usages.get(newVariable).add(identifiers);

                writeIdentifier.setSsaVariable(newVariable);
            }
        }
    }

    /**
     * extract SSA variables from bloc
     * @param bloc to process
     * @param localSSA current local last SSA variable map
     */
    private void processBloc(AbstractCodeBloc bloc, Map<String,SSAVariable> localSSA) {
        // process condition if branch bloc
        if (bloc instanceof BranchCodeBloc) {
            BranchCodeBloc branchCodeBloc = (BranchCodeBloc) bloc;
            processInstructionIdentifiers(branchCodeBloc.getConditionIdentifiers(), localSSA);
        }

        // check if there are more than one in arc for the bloc
        if (bloc.getInArcs().size() > 1) {
            // check if at least one of these blocs is not processed
            boolean needWait = false;
            for (int i = 0; i < bloc.getInArcs().size(); i++) {
                if (!graph.getDoneBlocs().contains(bloc.getInArcs().get(i).getStart())) {
                    needWait = true;
                    break;
                }
            }

            if (needWait) {
                // need to manage merge
                if (!waitingFusionCodeBlocs.containsKey(bloc)) {
                    // start merge
                    startMerge(bloc, localSSA);
                } else {
                    // continue merge
                    continueMerge(bloc, localSSA);
                }
            } else {
                // end merge
                endMerge(bloc, localSSA);
                graph.addDoneBloc(bloc);
            }
        } else {
            graph.addDoneBloc(bloc);
        }

        // process instructions of the bloc
        for (InstructionIdentifiers identifiers : bloc.getInstructionIdentifiersList()) {
            processInstructionIdentifiers(identifiers, localSSA);
        }

    }

    /**
     * extract SSA variable from variables declaration
     * @return a map containing last SSA variable for each variable name
     */
    private Map<String,SSAVariable> processDeclVar() {
        // create map
        Map<String, SSAVariable> initSSA = new HashMap<>();

        // iterate on each variable declaration
        for (AbstractDeclVar var : graph.getDeclVariables().getList()) {
            // add entry in last ids map
            DeclVar variable = (DeclVar) var;
            lastVariablesIds.put(variable.getVarName().getName().getName(), 1);

            // create SSA variable
            Identifier lIdentifier = (Identifier) variable.getVarName();
            SSAVariable ssaVariable = new SSAVariable(lIdentifier.getName().getName(), 1);
            lIdentifier.setSsaVariable(ssaVariable);

            // init internal data structures
            initSSA.put(lIdentifier.getName().getName(), ssaVariable);
            usages.put(ssaVariable, new HashSet<>());
            merges.put(lIdentifier.getName().getName(), new HashSet<>());

            // process initialization
            if (variable.getInitialization() instanceof Initialization) {
                Initialization initialization = (Initialization) variable.getInitialization();
                InstructionIdentifiers identifiers = new InstructionIdentifiers(initialization.getExpression());
                processInstructionIdentifiers(identifiers, initSSA);
            }
        }

        return initSSA;
    }

    /**
     * transform control flow graph into SSA form
     */
    public void process() {
        // process variables declarations and create local SSA map
        Map<String,SSAVariable> initSSA = processDeclVar();

        // prepare for graph read
        graph.clearDoneBlocs();
        graph.addDoneBloc(graph.getStopBloc());

        // use stack to store which blocs to process
        Stack<AbstractCodeBloc> toProcessBlocs = new Stack<>();
        Stack<Map<String,SSAVariable>> localSSAs = new Stack<>();
        toProcessBlocs.push(graph.getStartBloc());
        localSSAs.push(initSSA);

        // process each bloc of the stack and add next blocs
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
