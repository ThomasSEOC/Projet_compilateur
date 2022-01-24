package fr.ensimag.deca.opti;

/**
 * class representing an Arc for oriented graph
 */
public class Arc {
    private AbstractCodeBloc start;
    private AbstractCodeBloc stop;

    /**
     * constructor for Arc
     * @param start start bloc
     * @param stop stop bloc
     */
    public Arc(AbstractCodeBloc start, AbstractCodeBloc stop) {
        this.start = start;
        start.addOutArc(this);
        this.stop = stop;
        stop.addInArc(this);
    }

    /**
     * getter for start bloc
     * @return start bloc
     */
    public AbstractCodeBloc getStart() {
        return start;
    }

    /**
     * getter for stop bloc
     * @return stop bloc
     */
    public AbstractCodeBloc getStop() {
        return stop;
    }

    /**
     * setter for start bloc
     * @param bloc start bloc
     */
    public void setStart(AbstractCodeBloc bloc) {
        this.start = bloc;
    }

    /**
     * setter for stop bloc
     * @param bloc stop bloc
     */
    public void setStop(AbstractCodeBloc bloc) {
        this.stop = bloc;
    }

    /**
     * print arc
     * @return string representing human-readable arc
     */
    @Override
    public String toString() {
        return "Arc : from " + start.getId() + " to " + stop.getId();
    }
}
