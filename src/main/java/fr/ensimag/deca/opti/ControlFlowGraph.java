package fr.ensimag.deca.opti;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.IfThenElse;
import fr.ensimag.deca.tree.ListInst;
import fr.ensimag.deca.tree.While;

import java.util.List;

public class ControlFlowGraph extends Graph {
    private DecacCompiler compiler;
    private ListInst instructions;

    public ControlFlowGraph(DecacCompiler compiler, ListInst instructions) {
        super();
        this.instructions = instructions;
        this.compiler = compiler;
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

        addArc(new Arc(getStartBloc(), currentBloc));
        addArc(new Arc(currentBloc, getStopBloc()));
    }

    public void codeGen() {
        AbstractCodeBloc bloc = getStartBloc();
        while (bloc != getStopBloc()) {
            System.out.println(bloc);
            bloc.getInstructions().codeGenListInst(compiler);
            bloc = bloc.outArcs.get(0).getStop();
        }
    }
}
