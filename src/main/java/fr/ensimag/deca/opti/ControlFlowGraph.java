package fr.ensimag.deca.opti;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CodeGenBackend;
import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.IfThenElse;
import fr.ensimag.deca.tree.ListInst;
import fr.ensimag.deca.tree.While;

import java.util.ArrayList;
import java.util.List;

public class ControlFlowGraph extends Graph {
    private final DecacCompiler compiler;
    private final ListInst instructions;
    private List<AbstractCodeBloc> codeGenDoneBlocs;

    public ControlFlowGraph(DecacCompiler compiler, ListInst instructions) {
        super();
        this.instructions = instructions;
        this.compiler = compiler;
        createCFG();
    }

    private void CFGRecursion(List<AbstractInst> instructionsList, AbstractCodeBloc inCodeBloc, AbstractCodeBloc outCodeBloc) {
        AbstractCodeBloc currentBloc = new LinearCodeBloc(requestId());
        addArc(new Arc(inCodeBloc, currentBloc));

        for (AbstractInst instruction : instructionsList) {
            if ((instruction instanceof IfThenElse) || (instruction instanceof While)) {
                BranchCodeBloc branchCodeBloc = new BranchCodeBloc(requestId());
//                for (AbstractInst inst : currentBloc.getInstructions().getList()) {
//                    branchCodeBloc.addInstruction(inst);
//                }
                addArc(new Arc(currentBloc, branchCodeBloc));

                if (instruction instanceof While) {
                    While whileInstruction = (While) instruction;
                    branchCodeBloc.setCondition(whileInstruction.getCondition());
//                    LinearCodeBloc bodyCodeBloc = new LinearCodeBloc(requestId());
//                    for (AbstractInst inst : whileInstruction.getBody().getList()) {
//                        bodyCodeBloc.addInstruction(inst);
//                    }
//                    branchCodeBloc.setThenBloc(bodyCodeBloc);
//                    addArc(new Arc(branchCodeBloc, bodyCodeBloc));
//                    addArc(new Arc(bodyCodeBloc, branchCodeBloc));

                    CFGRecursion(whileInstruction.getBody().getList(), branchCodeBloc, branchCodeBloc);
                    branchCodeBloc.setThenBloc(branchCodeBloc.getOutArcs().get(0).getStop());

                    LinearCodeBloc nextCurrentBloc = new LinearCodeBloc(requestId());
                    branchCodeBloc.setElseBloc(nextCurrentBloc);
                    addArc(new Arc(branchCodeBloc, nextCurrentBloc));

                    currentBloc = nextCurrentBloc;
                } else {
                    IfThenElse ifThenElseInstruction = (IfThenElse) instruction;

                    branchCodeBloc.setCondition(ifThenElseInstruction.getCondition());

                    LinearCodeBloc nextCurrentBloc = new LinearCodeBloc(requestId());

//                    LinearCodeBloc thenCodeBloc = new LinearCodeBloc(requestId());
//                    for (AbstractInst inst : ifThenElseInstruction.getThenBranch().getList()) {
//                        thenCodeBloc.addInstruction(inst);
//                    }
//                    branchCodeBloc.setThenBloc(thenCodeBloc);
//                    addArc(new Arc(branchCodeBloc, thenCodeBloc));
                    CFGRecursion(ifThenElseInstruction.getThenBranch().getList(), branchCodeBloc, nextCurrentBloc);
                    branchCodeBloc.setThenBloc(branchCodeBloc.getOutArcs().get(0).getStop());

//                    LinearCodeBloc elseCodeBloc = new LinearCodeBloc(requestId());
//                    for (AbstractInst inst : ifThenElseInstruction.getElseBranch().getList()) {
//                        elseCodeBloc.addInstruction(inst);
//                    }
//                    branchCodeBloc.setElseBloc(elseCodeBloc);
//                    addArc(new Arc(branchCodeBloc, elseCodeBloc));

                    CFGRecursion(ifThenElseInstruction.getElseBranch().getList(), branchCodeBloc, nextCurrentBloc);
                    branchCodeBloc.setElseBloc(branchCodeBloc.getOutArcs().get(1).getStop());

                    currentBloc = nextCurrentBloc;
                }
            } else {
                currentBloc.addInstruction(instruction);
            }
        }

        addCodeBloc(currentBloc);

        addArc(new Arc(currentBloc, outCodeBloc));
    }

    private void createCFG() {
        List<AbstractInst> instructionsList = instructions.getList();

        CFGRecursion(instructionsList, getStartBloc(), getStopBloc());
    }

    public void addDoneBloc(AbstractCodeBloc bloc) {
        codeGenDoneBlocs.add(bloc);
    }

    public List<AbstractCodeBloc> getDoneBlocs() {
        return codeGenDoneBlocs;
    }

    public void codeGen() {
        AbstractCodeBloc startBloc = getStartBloc();
        codeGenDoneBlocs = new ArrayList<>();
        codeGenDoneBlocs.add(getStartBloc());
        codeGenDoneBlocs.add(getStopBloc());

        startBloc.codeGen(this);

        getStopBloc().codeGen(this);

        getBackend().writeInstructions();
    }

    public DecacCompiler getCompiler() {
        return compiler;
    }

    public CodeGenBackend getBackend() {
        return compiler.getCodeGenBackend();
    }
}
