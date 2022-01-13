package fr.ensimag.deca.opti;

public class LinearCodeBloc extends AbstractCodeBloc {
    public LinearCodeBloc(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "Linear bloc #" + getId() + " : " + instructions.size() + " instruction, " + inArcs.size() + " in arcs, " + outArcs.size() + " out arcs";
    }
}
