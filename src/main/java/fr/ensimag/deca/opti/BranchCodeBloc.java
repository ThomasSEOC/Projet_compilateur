package fr.ensimag.deca.opti;

import fr.ensimag.deca.tree.AbstractExpr;

public class BranchCodeBloc extends AbstractCodeBloc {
    private AbstractExpr condition;
    private AbstractCodeBloc thenBloc;
    private AbstractCodeBloc elseBloc;

    public BranchCodeBloc(int id) {
        super(id);
        thenBloc = null;
        elseBloc = null;
    }

    public void setCondition(AbstractExpr condition) {
        this.condition = condition;
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
