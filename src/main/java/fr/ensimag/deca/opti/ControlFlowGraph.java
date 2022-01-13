package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.IfThenElse;
import fr.ensimag.deca.tree.ListInst;
import fr.ensimag.deca.tree.While;

import java.util.ArrayList;
import java.util.List;

public class ControlFlowGraph extends Graph {
    private ListInst instructions;

    public ControlFlowGraph(ListInst instructions) {
        super();
        this.instructions = instructions;
        createCFG();
    }

    private void createCFG() {
        List<AbstractInst> instructionsList = instructions.getList();
//        List<AbstractInst> currentInstructionsList = new ArrayList<>();
        LinearCodeBloc currentBloc = new LinearCodeBloc(requestId());
        for (int i = 0; i < instructions.size(); i++) {
            AbstractInst instruction = instructionsList.get(i);
            if ((instruction instanceof IfThenElse) || (instruction instanceof While)) {

                throw new UnsupportedOperationException("not yet implemented");

//                if (instruction instanceof While) {
//                    throw new UnsupportedOperationException("not yet implemented");
//                }

//                IfThenElse inst = (IfThenElse) instruction;
//
//
//                ListInst previousBlocInst = new ListInst();
//                for (AbstractInst inst : currentInstructionsList) {
//                    previousBlocInst.add(inst);
//                }
//                CodeBloc previousBloc = new CodeBloc(previousBlocInst);
//                addCodeBloc(previousBloc);
            }
            else {
                currentBloc.addInstruction(instruction);
            }
        }

        addCodeBloc(currentBloc);

        addArc(new Arc(getStart(), currentBloc));
        addArc(new Arc(currentBloc, getStop()));
    }

}
