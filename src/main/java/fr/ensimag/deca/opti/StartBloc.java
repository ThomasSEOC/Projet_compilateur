package fr.ensimag.deca.opti;

public class StartBloc extends AbstractCodeBloc {
    public StartBloc(int id) {
        super(id);
        inArcs = null;
    }

    @Override
    public String toString() {
        return "Start bloc #" + getId() + " : " + outArcs.size() + " out arcs";
    }
}
