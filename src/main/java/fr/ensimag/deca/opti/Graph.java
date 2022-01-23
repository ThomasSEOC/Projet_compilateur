package fr.ensimag.deca.opti;

import fr.ensimag.deca.DecacCompiler;

import java.util.ArrayList;
import java.util.List;

abstract class Graph {
    protected final DecacCompiler compiler;
    private List<AbstractCodeBloc> blocs;
    private List<Arc> arcs;
    private AbstractCodeBloc start;
    private AbstractCodeBloc stop;

    public Graph(DecacCompiler compiler) {
        this.compiler = compiler;
        blocs = new ArrayList<>();
        arcs = new ArrayList<>();
        start = new StartBloc(requestId());
        stop = new StopBloc(requestId());
    }

    protected int requestId() {
        return compiler.getCodeGenBackend().requestnewGraphId();
    }

    abstract public SSAProcessor getSsaProcessor();

    public void addCodeBloc(AbstractCodeBloc bloc) {
        if (!blocs.contains(bloc) && (bloc != getStartBloc()) && (bloc != getStopBloc())) {
            blocs.add(bloc);
        }
    }

    public void removeCodeBloc(AbstractCodeBloc bloc) {
        blocs.remove(bloc);
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

    public void removeArc(Arc arc) {
        arcs.remove(arc);
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
        sb.append("}\n\n");

        sb.append("DECLARED SSA VARIABLES :\n");
        sb.append("{ \n");
        SSAProcessor ssaProcessor = getSsaProcessor();
        for (String variableName : ssaProcessor.getVariablesNames()) {
            sb.append("\t");
            sb.append(variableName);
            sb.append("#1");
            sb.append("\n");
        }
        sb.append("}\n");

        sb.append("BLOCS INSTRUCTIONS SSA VARIABLES :\n");
        sb.append("{ \n");
        for (AbstractCodeBloc bloc : blocs) {
            sb.append("\t").append(bloc.toString()).append("\n");
            sb.append("\t").append(bloc.instructionstoString());
        }
        sb.append("}\n");

        return sb.toString();
    }
}
