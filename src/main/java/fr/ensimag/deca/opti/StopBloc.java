package fr.ensimag.deca.opti;

import fr.ensimag.ima.pseudocode.Label;

public class StopBloc extends AbstractCodeBloc {
    public StopBloc(int id) {
        super(id);
        outArcs = null;
    }

    @Override
    public String toString() {
        return "Stop bloc #" + getId() + " : " + inArcs.size() + " in arcs";
    }

    @Override
    public void codeGen(ControlFlowGraph graph) {
        // add bloc label
        graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

        super.codeGen(graph);
    }
}
