package fr.ensimag.deca.opti;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

public class LinearCodeBloc extends AbstractCodeBloc {
    public LinearCodeBloc(int id) {
        super(id);
    }

    @Override
    public void codeGen(ControlFlowGraph graph) {
        // add bloc label
        graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

        super.codeGen(graph);


        // normally only 1 out arc
        AbstractCodeBloc nextBloc = getOutArcs().get(0).getStop();
        if (!(graph.getDoneBlocs().contains(nextBloc))) {
            graph.addDoneBloc(nextBloc);
            nextBloc.codeGen(graph);
        }
        else {
            if (!(nextBloc instanceof StopBloc)) {
                graph.getBackend().addInstruction(new BRA(new Label("Code.Bloc." + getOutArcs().get(0).getStop().getId())));
            }
        }

    }

    @Override
    public String toString() {
        return "Linear bloc #" + getId() + " : " + instructions.size() + " instruction, " + inArcs.size() + " in arcs, " + outArcs.size() + " out arcs";
    }
}
