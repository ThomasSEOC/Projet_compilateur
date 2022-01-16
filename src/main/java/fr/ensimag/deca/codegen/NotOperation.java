package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;

/**
 * class responsible for not operation
 */
public class NotOperation extends AbstractOperation {

    /**
     * constructor for NotOperation
     * @param backend global code generation backend
     * @param expression expression related to operation
     */
    public NotOperation(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * method called to generate code for not operation
     */
    @Override
    public void doOperation() {
        // separate according to operand type
        if (getExpression() instanceof And) {
            // use de Morgan theorem
            Not leftOperand = new Not(((And) getExpression()).getLeftOperand());
            Not righOperand = new Not(((And) getExpression()).getRightOperand());
            Or newExpression = new Or(leftOperand, righOperand);
            this.setExpression(newExpression);
        }
        else if (getExpression() instanceof Or) {
            // use de Morgan theorem
            Not leftOperand = new Not(((And) getExpression()).getLeftOperand());
            Not righOperand = new Not(((And) getExpression()).getRightOperand());
            And newExpression = new And(leftOperand, righOperand);
            this.setExpression(newExpression);
        }
        // reverse classical boolean operations
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

        // generate code with change done
        AbstractExpr[] expr = {this.getExpression()};
        ListCodeGen(expr);
    }

    /**
     * method called to generate code for not operation printing
     */
    @Override
    public void print() {
        doOperation();

        throw new UnsupportedOperationException("not yet implemented");
    }

}
