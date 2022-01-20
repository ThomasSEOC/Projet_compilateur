package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.ListInst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class AbstractCodeBloc {
    private int id;
    protected ListInst instructions;
    protected List<InstructionIdentifiers> instructionIdentifiers;
    protected List<Arc> inArcs;
    protected List<Arc> outArcs;

    public AbstractCodeBloc(int id) {
        this.instructions = new ListInst();
        this.instructionIdentifiers = new ArrayList<>();
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

    public List<InstructionIdentifiers> getInstructionIdentifiersList() {
        return instructionIdentifiers;
    }

    public Set<String> getUsedVariables() {
        Set<String> usedVariables = new HashSet<>();
        for (InstructionIdentifiers identifiers : instructionIdentifiers) {
            if ((identifiers.getWriteIdentifier() != null)) {
                usedVariables.add(identifiers.getWriteIdentifier().getName().getName());
            }

            for (Identifier readIdentifier : identifiers.getReadIdentifiers()) {
                usedVariables.add(readIdentifier.getName().getName());
            }
        }
        return usedVariables;
    }

    public void addInstruction(AbstractInst instruction) {
        instructions.add(instruction);
        instructionIdentifiers.add(new InstructionIdentifiers(instruction));
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
        // generate code for instructions of this bloc
        ListInst instructions = getInstructions();
        instructions.codeGenListInst(graph.getCompiler());
    }

    public String instructionstoString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \n\t");
        for (InstructionIdentifiers identifiers : instructionIdentifiers ) {
            sb.append("\t").append(identifiers.toString());
        }
        sb.append("\t}\n");

        return sb.toString();
    }
}
