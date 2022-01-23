package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.Assign;
import fr.ensimag.deca.tree.Identifier;

import java.util.ArrayList;
import java.util.List;

public class InstructionIdentifiers {
    private final AbstractInst instruction;
    private Identifier writeIdentifier;
    private final List<Identifier> readIdentifiers;

    public InstructionIdentifiers(AbstractInst instruction) {
        this.instruction = instruction;
        this.writeIdentifier = null;
        this.readIdentifiers = new ArrayList<>();
        searchIdentifiers();
    }

    public Identifier getWriteIdentifier() { return writeIdentifier; }

    public List<Identifier> getReadIdentifiers() { return readIdentifiers; }

    private void searchIdentifiers() {
//        if (instruction instanceof Assign) {
//            Assign assign = (Assign) instruction;
//            if (assign.getLeftOperand() instanceof Identifier) {
//                writeIdentifier = (Identifier) assign.getLeftOperand();
//            }
//        }

        instruction.searchIdentifiers(this);
    }

    public AbstractInst getInstruction() {
        return instruction;
    }

    public void addReadIdentifer(Identifier identifier) {
        readIdentifiers.add(identifier);
    }

    public void setWriteIdentifier(Identifier identifier) {
        writeIdentifier = identifier;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (writeIdentifier != null) {
            sb.append("\t\tWrite: ");
            sb.append(writeIdentifier.getSsaVariable().getName());
            sb.append("#");
            sb.append(writeIdentifier.getSsaVariable().getId()).append("; ");
        }
        else {
            sb.append("\t\tWrite: null; ");
        }

        sb.append("Read: [");
        for (Identifier ident : readIdentifiers) {
            sb.append(ident.getSsaVariable().getName());
            sb.append("#");
            sb.append(ident.getSsaVariable().getId());
            sb.append(", ");
        }
        sb.append("]\n");

        return sb.toString();
    }
}
