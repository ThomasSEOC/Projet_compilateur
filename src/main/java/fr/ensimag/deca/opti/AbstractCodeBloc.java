package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.ListInst;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract class for graph code bloc
 */
abstract class AbstractCodeBloc {
    private final int id;
    protected ListInst instructions;
    protected List<InstructionIdentifiers> instructionIdentifiers;
    protected List<Arc> inArcs;
    protected List<Arc> outArcs;

    /**
     * constructor for AbstractCodeBloc
     * @param id unique ID for code bloc
     */
    public AbstractCodeBloc(int id) {
        this.instructions = new ListInst();
        this.instructionIdentifiers = new ArrayList<>();
        this.inArcs = new ArrayList<>();
        this.outArcs = new ArrayList<>();
        this.id = id;
    }

    /**
     * getter for bloc ID
     * @return bloc unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * getter for instructions
     * @return list of instructions inside the bloc
     */
    public ListInst getInstructions() {
        return instructions;
    }

    /**
     * setter for instructions
     * @param instructions list of instructions which will go inside the bloc
     */
    public void setInstructions(ListInst instructions) {
        this.instructions = instructions;
    }

    /**
     * getter for instructionIdentifiers
     * @return list of instructionIdentifiers related to bloc instructions
     */
    public List<InstructionIdentifiers> getInstructionIdentifiersList() {
        return instructionIdentifiers;
    }

    /**
     * add instruction to this bloc
     * @param instruction instruction to add
     */
    public void addInstruction(AbstractInst instruction) {
        instructions.add(instruction);
        instructionIdentifiers.add(new InstructionIdentifiers(instruction));
    }

    /**
     * add arc which finish to the bloc
     * @param arc stop must be set to this bloc
     */
    public void addInArc(Arc arc) {
        inArcs.add(arc);
    }

    /**
     * add rc which start from this bloc
     * @param arc start must be set to this bloc
     */
    public void addOutArc(Arc arc) {
        outArcs.add(arc);
    }

    /**
     * getter for inArcs
     * @return list of arcs that enter this bloc
     */
    public List<Arc> getInArcs() {
        return inArcs;
    }

    /**
     * getter for outArcs
     * @return list of arcs that start from this bloc
     */
    public List<Arc> getOutArcs() {
        return outArcs;
    }

    /**
     * generate code for instructions of the bloc
     * @param graph graph related to the bloc
     */
    public void codeGen(ControlFlowGraph graph) {
        ListInst instructions = getInstructions();
        instructions.codeGenListInst(graph.getCompiler());
    }

    /**
     * print list of instructions
     * @return string representing the instructions from this bloc
     */
    public String instructionstoString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        for (InstructionIdentifiers identifiers : instructionIdentifiers ) {
            sb.append(identifiers.toString());
        }
        sb.append("\t}\n");

        return sb.toString();
    }
}
