package fr.ensimag.deca.opti;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.HALT;

public class StopBloc extends AbstractCodeBloc {
    public StopBloc(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "Stop bloc #" + getId() + " : " + inArcs.size() + " in arcs";
    }

    @Override
    public void codeGen(ControlFlowGraph graph) {
        // add bloc label
        graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

        graph.getBackend().addInstruction(new HALT());

        super.codeGen(graph);
    }
}
