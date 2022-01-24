package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * class representing every read and written identifiers from an instruction
 */
public class InstructionIdentifiers {
    private final AbstractInst instruction;
    private Identifier writeIdentifier;
    private final List<Identifier> readIdentifiers;

    /**
     * constructor for InstructionIdentifiers
     * @param instruction input for search
     */
    public InstructionIdentifiers(AbstractInst instruction) {
        this.instruction = instruction;
        this.writeIdentifier = null;
        this.readIdentifiers = new ArrayList<>();
        searchIdentifiers();
    }

    /**
     * getter for written identifier
     * @return written identifier, can be null
     */
    public Identifier getWriteIdentifier() { return writeIdentifier; }

    /**
     * getter for the list of read identifiers
     * @return list of read identifiers
     */
    public List<Identifier> getReadIdentifiers() { return readIdentifiers; }

    /**
     * search identifiers from the input instrction
     */
    private void searchIdentifiers() {
        instruction.searchIdentifiers(this);
    }

    /**
     * getter for input instruction
     * @return input instruction
     */
    public AbstractInst getInstruction() {
        return instruction;
    }

    /**
     * add a read identifier, must be called only when searching
     * @param identifier to add
     */
    public void addReadIdentifer(Identifier identifier) {
        readIdentifiers.add(identifier);
    }

    /**
     * setter for written identifier, must be called only when searching
     * @param identifier to set
     */
    public void setWriteIdentifier(Identifier identifier) {
        writeIdentifier = identifier;
    }

    /**
     * represent written and read identifiers in a human-readable form
     * @return represent identifiers
     */
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
