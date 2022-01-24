package fr.ensimag.deca.opti;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CodeGenBackend;
import fr.ensimag.deca.tree.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * class representing a control flow graph
 */
public class ControlFlowGraph extends Graph {
    private final ListInst instructions;
    private ListDeclVar variables;
    private List<AbstractCodeBloc> codeGenDoneBlocs;
    private final SSAProcessor ssaProcessor;
    private final ConstantPropagator constantPropagator;
    private final DeadCodeRemover deadCodeRemover;
    private boolean isMethod = false;

    /**
     * constructor for ControlFlowGraph
     * @param compiler global compiler
     * @param variables list of variables
     * @param instructions list of instructions
     */
    public ControlFlowGraph(DecacCompiler compiler, ListDeclVar variables, ListInst instructions) {
        super(compiler);
        this.variables = variables;
        this.instructions = instructions;

        // create the graph
        createCFG();
        // transform graph into SSA form
        this.ssaProcessor = new SSAProcessor(this);
        ssaProcessor.process();

        // propagate constants
        this.constantPropagator = new ConstantPropagator(this);
        constantPropagator.process();

        // remove dead code
        this.deadCodeRemover = new DeadCodeRemover(this);
        deadCodeRemover.process();
    }

    /**
     * constructor for ControlFlowGraph
     * @param compiler global compiler
     * @param variables list of variables
     * @param instructions list of instructions
     * @param isMethod true if graph is used on a method
     */
    public ControlFlowGraph(DecacCompiler compiler, ListDeclVar variables, ListInst instructions, boolean isMethod) {
        super(compiler);
        this.variables = variables;
        this.instructions = instructions;
        this.isMethod = isMethod;

        // create the graph
        createCFG();

        // transform graph into SSA form
        this.ssaProcessor = new SSAProcessor(this);
        ssaProcessor.process();

        // propagate constants
        this.constantPropagator = new ConstantPropagator(this);
        constantPropagator.process();

        // remove dead code
        this.deadCodeRemover = new DeadCodeRemover(this);
        deadCodeRemover.process();
    }

    /**
     * get if graph represents a method
     * @return true if graph represents a method
     */
    public boolean getIsMethod() {
        return isMethod;
    }

    /**
     * setter for variables
     * @param variables list of variables declarations
     */
    public void setDeclVariables(ListDeclVar variables) {
        this.variables = variables;
    }

    /**
     * getter for variables
     * @return list of variables declarations
     */
    public ListDeclVar getDeclVariables() {
        return variables;
    }

    /**
     * getter for SSA processor
     * @return SSAProcessor related to the graph
     */
    @Override
    public SSAProcessor getSsaProcessor() {
        return ssaProcessor;
    }

    /**
     * create recursively control flow graph from instructions
     * @param instructionsList instructions as input
     * @param inCodeBloc local start bloc
     * @param outCodeBloc local stop bloc
     */
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

    /**
     * create control flow graph from list of instructions
     */
    private void createCFG() {
        List<AbstractInst> instructionsList = instructions.getList();
        CFGRecursion(instructionsList, getStartBloc(), getStopBloc());
    }

    /**
     * add done bloc when iterating on the graph
     * @param bloc done bloc
     */
    public void addDoneBloc(AbstractCodeBloc bloc) {
        codeGenDoneBlocs.add(bloc);
    }

    /**
     * getter for done bloc hen iterating on the graph
     * @return done blocs
     */
    public List<AbstractCodeBloc> getDoneBlocs() {
        return codeGenDoneBlocs;
    }

    /**
     * clear done blocs when iterating of the graph
     */
    public void clearDoneBlocs() {
        codeGenDoneBlocs = new ArrayList<>();
    }

    /**
     * generate code corresponding to the current state of the graph
     */
    public void codeGen() {
        clearDoneBlocs();

        AbstractCodeBloc startBloc = getStartBloc();
        codeGenDoneBlocs = new ArrayList<>();
        codeGenDoneBlocs.add(getStartBloc());
        codeGenDoneBlocs.add(getStopBloc());

        variables.codeGenListDeclVar(compiler);
        if (!getIsMethod()) {
            compiler.getCodeGenBackend().addComment("Beginning of main instructions:");
        }

        startBloc.codeGen(this);

        getStopBloc().codeGen(this);
    }

    /**
     * getter for compiler
     * @return global compiler
     */
    public DecacCompiler getCompiler() {
        return compiler;
    }

    /**
     * getter for backend
     * @return global code generation backend
     */
    public CodeGenBackend getBackend() {
        return compiler.getCodeGenBackend();
    }

    /**
     * create a dot file to represent the graph
     * it can be processed using graphviz
     * @throws IOException if file cannot be created
     */
    public void createDotGraph() throws IOException {
        String destFile = compiler.getSource().toString().replaceFirst("[.][^.]+$", "") + ".dot";
        BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));
        writer.write("digraph {\n");

        for (Arc arc : getArcs()) {
            writer.write("\t");
            writer.write("" + arc.getStart().getId());
            writer.write(" -> ");
            writer.write("" + arc.getStop().getId());
            writer.write(";\n");
        }

        writer.write("}");
        writer.close();
    }
}
