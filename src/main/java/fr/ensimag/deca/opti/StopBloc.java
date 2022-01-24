package fr.ensimag.deca.opti;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.HALT;

/**
 * class representing the stop bloc of a control flow graph
 */
public class StopBloc extends AbstractCodeBloc {

    /**
     * constructor of stop bloc
     * @param id unique ID for the bloc
     */
    public StopBloc(int id) {
        super(id);
    }

    /**
     * generate code for bloc and next
     * @param graph graph related to the bloc
     */
    @Override
    public void codeGen(ControlFlowGraph graph) {
        // add bloc label
        graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

        if (!graph.getIsMethod()) {
            graph.getBackend().addInstruction(new HALT());
        }

        super.codeGen(graph);
    }

    /**
     * represent start bloc in a human-readable form
     * @return start bloc representation
     */
    @Override
    public String toString() {
        return "Stop bloc #" + getId() + " : " + inArcs.size() + " in arcs";
    }
}
