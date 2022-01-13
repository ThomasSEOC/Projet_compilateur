package fr.ensimag.deca.opti;

public class StopBloc extends AbstractCodeBloc {
    public StopBloc(int id) {
        super(id);
        outArcs = null;
    }

    @Override
    public String toString() {
        return "Stop bloc #" + getId() + " : " + inArcs.size() + " in arcs";
    }
}
