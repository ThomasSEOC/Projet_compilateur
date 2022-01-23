package fr.ensimag.deca.opti;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

public class StartBloc extends AbstractCodeBloc {

    public StartBloc(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "Start bloc #" + getId() + " : " + outArcs.size() + " out arcs";
    }

    @Override
    public void codeGen(ControlFlowGraph graph) {
        if (getInstructions().size() > 0) {
            // add bloc label
            graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

            super.codeGen(graph);
        }

//        graph.getBackend().addInstruction(new BRA(new Label("Code.Bloc." + getOutArcs().get(0).getStop().getId())));

        // normally only 1 out arc
        AbstractCodeBloc nextBloc = getOutArcs().get(0).getStop();
        if (!(graph.getDoneBlocs().contains(nextBloc))) {
            graph.addDoneBloc(nextBloc);
            nextBloc.codeGen(graph);
        }

    }
}
