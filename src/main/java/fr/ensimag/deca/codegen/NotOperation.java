package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;

public class NotOperation extends AbstractOperation {

    public NotOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        if (getExpression() instanceof And) {
            Not leftOperand = new Not(((And) getExpression()).getLeftOperand());
            Not righOperand = new Not(((And) getExpression()).getRightOperand());
            Or newExpression = new Or(leftOperand, righOperand);
            this.setExpression(newExpression);
        }
        else if (getExpression() instanceof Or) {
            Not leftOperand = new Not(((And) getExpression()).getLeftOperand());
            Not righOperand = new Not(((And) getExpression()).getRightOperand());
            And newExpression = new And(leftOperand, righOperand);
            this.setExpression(newExpression);
        }
        else if (getExpression() instanceof Greater) {
            this.setExpression(new LowerOrEqual(((Greater) getExpression()).getLeftOperand(), ((Greater) getExpression()).getRightOperand()));
        }
        else if (getExpression() instanceof GreaterOrEqual) {
            this.setExpression(new Lower(((GreaterOrEqual) getExpression()).getLeftOperand(), ((GreaterOrEqual) getExpression()).getRightOperand()));
        }
        else if (this.getExpression() instanceof Lower){
            this.setExpression(new GreaterOrEqual(((Lower) getExpression()).getLeftOperand(), ((Lower) getExpression()).getRightOperand()));
        }
        else if (this.getExpression() instanceof LowerOrEqual){
            this.setExpression(new Greater(((LowerOrEqual) getExpression()).getLeftOperand(), ((LowerOrEqual) getExpression()).getRightOperand()));
        }
        else if (this.getExpression() instanceof Equals){
            this.setExpression(new Greater(((Equals) getExpression()).getLeftOperand(), ((Equals) getExpression()).getRightOperand()));
        }
        else if (this.getExpression() instanceof NotEquals){
            this.setExpression(new Greater(((NotEquals) getExpression()).getLeftOperand(), ((NotEquals) getExpression()).getRightOperand()));
        }

        AbstractExpr[] expr = {this.getExpression()};
        ListCodeGen(expr);
    }

    @Override
    public void print() {
        doOperation();

        throw new UnsupportedOperationException("not yet implemented");
    }

}
