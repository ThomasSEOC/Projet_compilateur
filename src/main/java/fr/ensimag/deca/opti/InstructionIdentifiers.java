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

    public void addReadIdentifer(Identifier identifier) {
        readIdentifiers.add(identifier);
    }

    public void setWriteIdentifier(Identifier identifier) {
        writeIdentifier = identifier;
    }
}
