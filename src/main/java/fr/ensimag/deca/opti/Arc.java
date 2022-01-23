package fr.ensimag.deca.opti;

public class Arc {
    private AbstractCodeBloc start;
    private AbstractCodeBloc stop;

    public Arc(AbstractCodeBloc start, AbstractCodeBloc stop) {
        this.start = start;
        start.addOutArc(this);
        this.stop = stop;
        stop.addInArc(this);
    }

    public AbstractCodeBloc getStart() {
        return start;
    }

    public AbstractCodeBloc getStop() {
        return stop;
    }

    public void setStart(AbstractCodeBloc bloc) {
        this.start = bloc;
    }

    public void setStop(AbstractCodeBloc bloc) {
        this.stop = bloc;
    }

    @Override
    public String toString() {
        return "Arc : from " + start.getId() + " to " + stop.getId();
    }
}
