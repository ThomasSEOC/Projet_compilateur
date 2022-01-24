package fr.ensimag.deca.opti;

import java.util.Stack;

/**
 * class responsible for dead code removing from a control flow graph
 */
public class DeadCodeRemover {
    private final ControlFlowGraph graph;

    /**
     * constructor for DeadCodeRemover
     * @param graph related control flow graph
     */
    public DeadCodeRemover(ControlFlowGraph graph) {
        this.graph = graph;
    }

    /**
     * add next blocs to process
     * @param bloc base bloc
     * @param stack processing stack
     */
    private void addNextBlocs(AbstractCodeBloc bloc, Stack<AbstractCodeBloc> stack) {
        for (Arc arc : bloc.getOutArcs()) {
            AbstractCodeBloc nextBloc = arc.getStop();
            if (!graph.getDoneBlocs().contains(nextBloc)) {
                stack.push(nextBloc);
            }
        }
    }

//    public void removeFrom(AbstractCodeBloc bloc) {
//        Set<AbstractCodeBloc> currentStage = new HashSet<>();
//        Set<Arc> arcs = new HashSet<>();
//        currentStage.add(bloc);
//
//        while (currentStage.size() > 0) {
//            for (AbstractCodeBloc currentBloc : currentStage) {
//                arcs.addAll(currentBloc.getOutArcs());
//                graph.removeCodeBloc(currentBloc);
//            }
//
//            currentStage.clear();
//
//            for (Arc arc : arcs) {
//                AbstractCodeBloc nextBloc = arc.getStop();
//                boolean canRemove = true;
//                for (Arc nextBlocArc : nextBloc.getInArcs()) {
//                    if (!arcs.contains(nextBlocArc)) {
//                        canRemove = false;
//                    }
//                }
//                if (canRemove) {
//                    currentStage.add(nextBloc);
//                }
//            }
//
//            for (Arc arc : arcs) {
//                graph.removeArc(arc);
//            }
//            arcs.clear();
//        }
//    }

    /**
     * remove dead code from the graph
     */
    public void process() {
        graph.clearDoneBlocs();

        graph.addDoneBloc(graph.getStopBloc());

        Stack<AbstractCodeBloc> toProcess = new Stack<>();
        toProcess.push(graph.getStartBloc());

        while (toProcess.size() > 0) {
            AbstractCodeBloc bloc = toProcess.pop();

            if (bloc instanceof BranchCodeBloc) {
                // cast bloc
                BranchCodeBloc branchCodeBloc = (BranchCodeBloc) bloc;

                // try get evaluate condition
                Constant constant = branchCodeBloc.getCondition().getConstant(graph.getCompiler());
                if (constant == null) {
                    graph.addDoneBloc(bloc);
                    addNextBlocs(bloc, toProcess);
                }
                else {
                    // success
                    LinearCodeBloc newBloc = new LinearCodeBloc(branchCodeBloc.getId());
                    newBloc.setInstructions(branchCodeBloc.getInstructions());
                    for (Arc inArc : branchCodeBloc.getInArcs()) {
                        inArc.setStop(newBloc);
                        newBloc.addInArc(inArc);
                    }

                    Arc outArc;
                    if (constant.getValueBoolean()) {
//                        removeFrom(branchCodeBloc.getElseBloc());
                        outArc = branchCodeBloc.getOutArcs().get(0);
                        graph.removeArc(branchCodeBloc.getOutArcs().get(1));
                    }
                    else {
//                        removeFrom(branchCodeBloc.getThenBloc());
                        outArc = branchCodeBloc.getOutArcs().get(1);
                        graph.removeArc(branchCodeBloc.getOutArcs().get(0));
                    }
                    outArc.setStart(newBloc);
                    newBloc.addOutArc(outArc);

                    // replace branch bloc
                    graph.removeCodeBloc(branchCodeBloc);
                    graph.addCodeBloc(newBloc);

                    graph.addDoneBloc(newBloc);
                    addNextBlocs(newBloc, toProcess);
                }

            }
            else {
                graph.addDoneBloc(bloc);
                addNextBlocs(bloc, toProcess);
            }
        }
    }
}
