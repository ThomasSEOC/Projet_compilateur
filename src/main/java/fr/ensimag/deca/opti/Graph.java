package fr.ensimag.deca.opti;

import java.util.ArrayList;
import java.util.List;

abstract class Graph {
    private List<AbstractCodeBloc> blocs;
    private List<Arc> arcs;
    private AbstractCodeBloc start;
    private AbstractCodeBloc stop;
    private int nextId = 0;

    public Graph() {
        blocs = new ArrayList<AbstractCodeBloc>();
        arcs = new ArrayList<Arc>();
        start = new StartBloc(nextId++);
        stop = new StopBloc(nextId++);
    }

    protected int requestId() {
        return nextId++;
    }

    protected void addCodeBloc(AbstractCodeBloc bloc) {
        if (!blocs.contains(bloc) && (bloc != getStartBloc()) && (bloc != getStopBloc())) {
            blocs.add(bloc);
        }
    }

    protected boolean checkGraph() {
        if ((start == null) || (stop == null)) {
            return false;
        }

        //List<CodeBloc> connectedBlocs;
        // à faire

        return true;
    }

    protected void addArc(Arc arc) {
        arcs.add(arc);
        if (!(getBlocs().contains(arc.getStart()))) {
            addCodeBloc(arc.getStart());
        }
        if (!(getBlocs().contains(arc.getStop()))) {
            addCodeBloc(arc.getStop());
        }
    }

    protected List<Arc> getArcs() {
        return arcs;
    }

    protected AbstractCodeBloc getStartBloc() {
        return start;
    }

    protected AbstractCodeBloc getStopBloc() {
        return stop;
    }

    protected List<AbstractCodeBloc> getBlocs() {
        return blocs;
    }

//    protected void setStart(AbstractCodeBloc bloc){
//        this.start = bloc;
//        addCodeBloc(bloc);
//    }
//
//    protected void setStop(AbstractCodeBloc bloc) {
//        this.stop = bloc;
//        addCodeBloc(bloc);
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Control Flow Graph\n\n");

        sb.append("BLOCS :\n");
        sb.append("{ \n\t").append(start.toString()).append("\n");
        for (AbstractCodeBloc bloc : blocs) {
            sb.append("\t").append(bloc.toString()).append("\n");
        }
        sb.append("\t").append(stop.toString()).append("\n}\n");

        sb.append("ARCS :\n");
        sb.append("{ ");
        for (Arc arc : arcs) {
            sb.append("\t").append(arc.toString()).append("\n");
        }
        sb.append("}");

        return sb.toString();
    }
}
