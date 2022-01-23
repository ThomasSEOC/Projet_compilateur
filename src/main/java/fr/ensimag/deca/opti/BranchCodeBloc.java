package fr.ensimag.deca.opti;

import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;

public class BranchCodeBloc extends AbstractCodeBloc {
    private AbstractExpr condition;
    private AbstractCodeBloc thenBloc;
    private AbstractCodeBloc elseBloc;
    private InstructionIdentifiers conditionIdentifiers;

    public BranchCodeBloc(int id) {
        super(id);
        thenBloc = null;
        elseBloc = null;
    }

    @Override
    public void codeGen(ControlFlowGraph graph) {
        // add bloc label
        graph.getBackend().addLabel(new Label("Code.Bloc." + getId()));

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
        }
        else if (getCondition() instanceof BooleanLiteral) {
            LiteralOperation operator = new LiteralOperation(graph.getBackend(), getCondition());
            operator.doOperation();
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

        graph.getBackend().popCurrentTrueBooleanLabel();
        graph.getBackend().popCurrentFalseBooleanLabel();

        if (!(graph.getDoneBlocs().contains(getThenBloc()))) {
            graph.addDoneBloc(getThenBloc());
            getThenBloc().codeGen(graph);
        }

//        graph.getBackend().addInstruction(new BRA(new Label("Code.Bloc." + getThenBloc().getId())));
        if (!(graph.getDoneBlocs().contains(getElseBloc()))) {
            graph.addDoneBloc(getElseBloc());
            getElseBloc().codeGen(graph);
        }
    }

    public void setCondition(AbstractExpr condition) {
        this.condition = condition;
        conditionIdentifiers = new InstructionIdentifiers(condition);
    }

    public void setThenBloc(AbstractCodeBloc bloc) {
        thenBloc = bloc;
    }

    public void setElseBloc(AbstractCodeBloc bloc) {
        elseBloc = bloc;
    }

    public AbstractExpr getCondition() {
        return condition;
    }

    public AbstractCodeBloc getThenBloc() {
        return thenBloc;
    }

    public AbstractCodeBloc getElseBloc() {
        return elseBloc;
    }

    public InstructionIdentifiers getConditionIdentifiers() { return conditionIdentifiers; }

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
