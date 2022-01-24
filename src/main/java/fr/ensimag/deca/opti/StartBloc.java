package fr.ensimag.deca.opti;

import fr.ensimag.ima.pseudocode.Label;

/**
 * class representing the start bloc of control flow graph
 */
public class StartBloc extends AbstractCodeBloc {

    /**
     * constructor for StartBloc
     * @param id unique ID for the bloc
     */
    public StartBloc(int id) {
        super(id);
    }

    /**
     * generate code for start bloc and next
     * @param graph graph related to the bloc
     */
    @Override
    public void codeGen(ControlFlowGraph graph) {
        if (getInstructions().size() > 0) {
            // add bloc label
            graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

            // generate code for instructions
            super.codeGen(graph);
        }

        // normally only 1 out arc
        AbstractCodeBloc nextBloc = getOutArcs().get(0).getStop();
        if (!(graph.getDoneBlocs().contains(nextBloc))) {
            graph.addDoneBloc(nextBloc);
            nextBloc.codeGen(graph);
        }

    }

    /**
     * represent start bloc in a human-readable form
     * @return start bloc representation
     */
    @Override
    public String toString() {
        return "Start bloc #" + getId() + " : " + outArcs.size() + " out arcs";
    }
}
