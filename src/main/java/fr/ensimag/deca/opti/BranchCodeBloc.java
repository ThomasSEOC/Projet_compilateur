package fr.ensimag.deca.opti;

import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;

/**
 * class representing a branch code bloc
 * there are 2 out arcs
 * out arc 0 is then branch
 * out arc 1 is else branch
 */
public class BranchCodeBloc extends AbstractCodeBloc {
    private AbstractExpr condition;
    private AbstractCodeBloc thenBloc;
    private AbstractCodeBloc elseBloc;
    private InstructionIdentifiers conditionIdentifiers;

    /**
     * constructor for BranchCodeBloc
     * @param id unique ID for the bloc
     */
    public BranchCodeBloc(int id) {
        super(id);
        thenBloc = null;
        elseBloc = null;
    }

    /**
     * generate code for instructions and branches of this bloc
     * @param graph graph related to the bloc
     */
    @Override
    public void codeGen(ControlFlowGraph graph) {
        // add bloc label
        graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

        // generate code for bloc instructions
        super.codeGen(graph);

        // push labels true and false conditions branch stack
        graph.getBackend().trueBooleanLabelPush(new Label("Code.Bloc." + getThenBloc().getId()));
        graph.getBackend().falseBooleanLabelPush(new Label("Code.Bloc." + getElseBloc().getId()));

        // check condition
        // generate code for condition
        if (getCondition() instanceof Not) {
            NotOperation operator = new NotOperation(graph.getBackend(), getCondition());
            operator.doOperation();
        }
        else if (getCondition() instanceof Identifier) {
            IdentifierRead operator = new IdentifierRead(graph.getBackend(), getCondition());
            operator.doOperation();
            VirtualRegister result = graph.getBackend().getContextManager().operationStackPop();
            graph.getBackend().addInstruction(new CMP(new ImmediateInteger(0), result.requestPhysicalRegister()));
            graph.getBackend().addInstruction(new BEQ(new Label("Code.Bloc." + getElseBloc().getId())));
            result.destroy();
        }
        else if (getCondition() instanceof BooleanLiteral) {
            LiteralOperation operator = new LiteralOperation(graph.getBackend(), getCondition());
            operator.doOperation();
            VirtualRegister result = graph.getBackend().getContextManager().operationStackPop();
            graph.getBackend().addInstruction(new CMP(new ImmediateInteger(0), result.requestPhysicalRegister()));
            graph.getBackend().addInstruction(new BEQ(new Label("Code.Bloc." + getElseBloc().getId())));
            result.destroy();
        }
        else if (getCondition() instanceof Assign) {
            AssignOperation operator = new AssignOperation(graph.getBackend(), getCondition());
            operator.doOperation(true);

            VirtualRegister result = graph.getBackend().getContextManager().operationStackPop();
            graph.getBackend().addInstruction(new CMP(new ImmediateInteger(0), result.requestPhysicalRegister()));
            graph.getBackend().addInstruction(new BEQ(new Label("Code.Bloc." + getElseBloc().getId())));
            result.destroy();
        }
        else if (getCondition() instanceof InstanceOf) {
            InstanceofOperation operator = new InstanceofOperation(graph.getBackend(), getCondition());
            operator.doOperation();
            // result is in R0
            graph.getBackend().addInstruction(new CMP(0, GPRegister.getR(0)));
            graph.getBackend().addInstruction(new BEQ(new Label("Code.Bloc." + getElseBloc().getId())));
        }
        else {
            BinaryBoolOperation operator = new BinaryBoolOperation(graph.getBackend(), getCondition());
            operator.doOperation();
        }

        // pop labels
        graph.getBackend().popCurrentTrueBooleanLabel();
        graph.getBackend().popCurrentFalseBooleanLabel();

        // generate code for then branch
        if (!(graph.getDoneBlocs().contains(getThenBloc()))) {
            graph.addDoneBloc(getThenBloc());
            getThenBloc().codeGen(graph);
        }

        // generate code for else branch
        if (!(graph.getDoneBlocs().contains(getElseBloc()))) {
            graph.addDoneBloc(getElseBloc());
            getElseBloc().codeGen(graph);
        }
    }

    /**
     * setter for condition
     * @param condition condition on which branch
     */
    public void setCondition(AbstractExpr condition) {
        this.condition = condition;
        conditionIdentifiers = new InstructionIdentifiers(condition);
    }

    /**
     * setter for then bloc
     * @param bloc bloc to branch in case of true condition
     */
    public void setThenBloc(AbstractCodeBloc bloc) {
        thenBloc = bloc;
    }

    /**
     * setter for else bloc
     * @param bloc bloc to branch in case of false condition
     */
    public void setElseBloc(AbstractCodeBloc bloc) {
        elseBloc = bloc;
    }

    /**
     * getter for condition
     * @return condition to branch on
     */
    public AbstractExpr getCondition() {
        return condition;
    }

    /**
     * getter for then bloc
     * @return then bloc
     */
    public AbstractCodeBloc getThenBloc() {
        return thenBloc;
    }

    /**
     * getter for else bloc
     * @return else bloc
     */
    public AbstractCodeBloc getElseBloc() {
        return elseBloc;
    }

    /**
     * getter for conditionIdentifiers
     * @return instructionIdentifiers of the condition
     */
    public InstructionIdentifiers getConditionIdentifiers() { return conditionIdentifiers; }

    /**
     * represent branch bloc in a human-readable form
     * @return representation of branch bloc
     */
    @Override
    public String toString() {
        String sthen = "no then bloc";
        String selse = "no else bloc";
        if (thenBloc != null) {
            sthen = "then OK";
        }
        if (elseBloc != null) {
            selse = "else OK";
        }
        return "Branch bloc #" + getId() + " : " + instructions.size() + " instruction, " + inArcs.size() + " in arcs, " + sthen + ", " + selse;
    }
}
