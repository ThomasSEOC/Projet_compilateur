package fr.ensimag.deca.opti;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.IfThenElse;
import fr.ensimag.deca.tree.ListInst;
import fr.ensimag.deca.tree.While;

import java.util.List;

public class ControlFlowGraph extends Graph {
    private final DecacCompiler compiler;
    private final ListInst instructions;

    public ControlFlowGraph(DecacCompiler compiler, ListInst instructions) {
        super();
        this.instructions = instructions;
        this.compiler = compiler;
        createCFG();
    }

    private void createCFG() {
        List<AbstractInst> instructionsList = instructions.getList();
//        List<AbstractInst> currentInstructionsList = new ArrayList<>();
        AbstractCodeBloc currentBloc = new LinearCodeBloc(requestId());
        addArc(new Arc(getStartBloc(), currentBloc));
        for (int i = 0; i < instructions.size(); i++) {
            AbstractInst instruction = instructionsList.get(i);
            if ((instruction instanceof IfThenElse) || (instruction instanceof While)) {
                BranchCodeBloc branchCodeBloc = new BranchCodeBloc(requestId());
                for (AbstractInst inst : currentBloc.getInstructions().getList()) {
                    branchCodeBloc.addInstruction(inst);
                }
                addArc(new Arc(currentBloc, branchCodeBloc));

                if (instruction instanceof While) {
                    While whileInstruction = (While) instruction;
                    branchCodeBloc.setCondition(whileInstruction.getCondition());
                    LinearCodeBloc bodyCodeBloc = new LinearCodeBloc(requestId());
                    for (AbstractInst inst : whileInstruction.getBody().getList()) {
                        bodyCodeBloc.addInstruction(inst);
                    }
                    branchCodeBloc.setThenBloc(bodyCodeBloc);
                    addArc(new Arc(branchCodeBloc, bodyCodeBloc));
                    addArc(new Arc(bodyCodeBloc, branchCodeBloc));
                    LinearCodeBloc nextCurrentBloc = new LinearCodeBloc(requestId());
                    branchCodeBloc.setElseBloc(nextCurrentBloc);
                    addArc(new Arc(branchCodeBloc, nextCurrentBloc));

                    currentBloc = nextCurrentBloc;
                }
                else {
                    IfThenElse ifThenElseInstruction = (IfThenElse) instruction;

                    branchCodeBloc.setCondition(ifThenElseInstruction.getCondition());

                    LinearCodeBloc thenCodeBloc = new LinearCodeBloc(requestId());
                    for (AbstractInst inst : ifThenElseInstruction.getThenBranch().getList()) {
                        thenCodeBloc.addInstruction(inst);
                    }
                    branchCodeBloc.setThenBloc(thenCodeBloc);
                    addArc(new Arc(branchCodeBloc, thenCodeBloc));

                    LinearCodeBloc elseCodeBloc = new LinearCodeBloc(requestId());
                    for (AbstractInst inst : ifThenElseInstruction.getElseBranch().getList()) {
                        elseCodeBloc.addInstruction(inst);
                    }
                    branchCodeBloc.setElseBloc(elseCodeBloc);
                    addArc(new Arc(branchCodeBloc, elseCodeBloc));

                    LinearCodeBloc nextCurrentBloc = new LinearCodeBloc(requestId());
                    addArc(new Arc(thenCodeBloc, nextCurrentBloc));
                    addArc(new Arc(elseCodeBloc, nextCurrentBloc));

                    currentBloc = nextCurrentBloc;
                }
            }
            else {
                currentBloc.addInstruction(instruction);
            }
        }

        addCodeBloc(currentBloc);

        addArc(new Arc(currentBloc, getStopBloc()));
    }

    public void codeGen() {
//        AbstractCodeBloc bloc = getStartBloc();
//        while (bloc != getStopBloc()) {
//            System.out.println(bloc);
//            bloc.getInstructions().codeGenListInst(compiler);
//            bloc = bloc.outArcs.get(0).getStop();
//        }
    }
}
