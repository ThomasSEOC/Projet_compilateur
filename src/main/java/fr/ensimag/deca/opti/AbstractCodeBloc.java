package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.ListInst;
import fr.ensimag.ima.pseudocode.Label;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractCodeBloc {
    private int id;
    protected ListInst instructions;
    protected List<Arc> inArcs;
    protected List<Arc> outArcs;

    public AbstractCodeBloc(int id) {
        this.instructions = new ListInst();
        this.inArcs = new ArrayList<>();
        this.outArcs = new ArrayList<>();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ListInst getInstructions() {
        return instructions;
    }

    public void addInstruction(AbstractInst instruction) {
        instructions.add(instruction);
    }

    public void addInArc(Arc arc) {
        inArcs.add(arc);
    }

    public void addOutArc(Arc arc) {
        outArcs.add(arc);
    }

    public List<Arc> getInArcs() {
        return inArcs;
    }

    public List<Arc> getOutArcs() {
        return outArcs;
    }

    public void codeGen(ControlFlowGraph graph) {
        // add bloc label
        graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

        // generate code for instructions of this bloc
        ListInst instructions = getInstructions();
        instructions.codeGenListInst(graph.getCompiler());
    }
}
