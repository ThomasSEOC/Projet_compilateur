package fr.ensimag.deca.opti;

import fr.ensimag.deca.DecacCompiler;

import java.util.ArrayList;
import java.util.List;

/**
 * class representing an oriented graph
 */
abstract class Graph {
    protected final DecacCompiler compiler;
    private final List<AbstractCodeBloc> blocs;
    private final List<Arc> arcs;
    private final AbstractCodeBloc start;
    private final AbstractCodeBloc stop;

    /**
     * constructor for Graph
     * @param compiler global compiler
     */
    public Graph(DecacCompiler compiler) {
        this.compiler = compiler;
        blocs = new ArrayList<>();
        arcs = new ArrayList<>();
        start = new StartBloc(requestId());
        stop = new StopBloc(requestId());
    }

    /**
     * create a new unique bloc ID and return it
     * @return a unique bloc ID
     */
    protected int requestId() {
        return compiler.getCodeGenBackend().requestnewGraphId();
    }

    /**
     * getter for SSA processor
     * @return graph related SSA processor
     */
    abstract public SSAProcessor getSsaProcessor();

    /**
     * add a bloc to the graph
     * @param bloc to add
     */
    public void addCodeBloc(AbstractCodeBloc bloc) {
        if (!blocs.contains(bloc) && (bloc != getStartBloc()) && (bloc != getStopBloc())) {
            blocs.add(bloc);
        }
    }

    /**
     * remove a bloc from the graph
     * @param bloc to remove
     */
    public void removeCodeBloc(AbstractCodeBloc bloc) {
        blocs.remove(bloc);
    }

    /**
     * add an arc to the graph
     * @param arc to add
     */
    protected void addArc(Arc arc) {
        arcs.add(arc);
        if (!(getBlocs().contains(arc.getStart()))) {
            addCodeBloc(arc.getStart());
        }
        if (!(getBlocs().contains(arc.getStop()))) {
            addCodeBloc(arc.getStop());
        }
    }

    /**
     * remove an arc from the graph
     * @param arc to remove
     */
    public void removeArc(Arc arc) {
        arcs.remove(arc);
    }

    /**
     * getter for arcs of the graph
     * @return arcs related to the graph
     */
    protected List<Arc> getArcs() {
        return arcs;
    }

    /**
     * getter for start bloc
     * @return start bloc
     */
    protected AbstractCodeBloc getStartBloc() {
        return start;
    }

    /**
     * getter for stop bloc
     * @return stop bloc
     */
    protected AbstractCodeBloc getStopBloc() {
        return stop;
    }

    /**
     * getter for graph blocs
     * @return blocs related to the graph
     */
    protected List<AbstractCodeBloc> getBlocs() {
        return blocs;
    }

    /**
     * represent graph in a human-readable form
     * @return graph representation
     */
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
