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

    /**
     * remove dead code from the graph
     */
    public void process() {
        // prepare for graph read
        graph.clearDoneBlocs();
        graph.addDoneBloc(graph.getStopBloc());

        // init internal structure
        Stack<AbstractCodeBloc> toProcess = new Stack<>();
        toProcess.push(graph.getStartBloc());

        // use a stack to process each bloc
        while (toProcess.size() > 0) {
            AbstractCodeBloc bloc = toProcess.pop();
            // process only branches
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
                    // success, create a linear bloc to replace branch bloc
                    LinearCodeBloc newBloc = new LinearCodeBloc(branchCodeBloc.getId());
                    newBloc.setInstructions(branchCodeBloc.getInstructions());
                    // copy input arcs
                    for (Arc inArc : branchCodeBloc.getInArcs()) {
                        inArc.setStop(newBloc);
                        newBloc.addInArc(inArc);
                    }

                    // select right branch according to result of evaluation
                    Arc outArc;
                    if (constant.getValueBoolean()) {
                        outArc = branchCodeBloc.getOutArcs().get(0);
                        graph.removeArc(branchCodeBloc.getOutArcs().get(1));
                    }
                    else {
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
