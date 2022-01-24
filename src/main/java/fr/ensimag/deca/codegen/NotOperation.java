package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;

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
        // try to evaluate as a constant
        boolean opti = (getCodeGenBackEnd().getCompiler().getCompilerOptions().getOptimize() > 0);
        Constant constant = null;
        if (opti) {
            constant = getConstant(getCodeGenBackEnd().getCompiler());
        }
        if (constant != null) {
            if (constant.getValueBoolean()) {
                if (getCodeGenBackEnd().getBranchCondition()) {
                    getCodeGenBackEnd().addInstruction(new BRA(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
            }
            else {
                if (!getCodeGenBackEnd().getBranchCondition()) {
                    getCodeGenBackEnd().addInstruction(new BRA(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
            }
            return;
        }

        // cast to Not
        Not expr = (Not) getExpression();

        // separate according to operand type
        if (expr.getOperand() instanceof And) {
            // use de Morgan theorem
            Not leftOperand = new Not(((And) expr.getOperand()).getLeftOperand());
            Not righOperand = new Not(((And) expr.getOperand()).getRightOperand());
            Or newExpression = new Or(leftOperand, righOperand);
            this.setExpression(newExpression);
        }
        else if (expr.getOperand() instanceof Or) {
            // use de Morgan theorem
            Not leftOperand = new Not(((Or) expr.getOperand()).getLeftOperand());
            Not righOperand = new Not(((Or) expr.getOperand()).getRightOperand());
            And newExpression = new And(leftOperand, righOperand);
            this.setExpression(newExpression);
        }
        // reverse classical boolean operations
        else if (expr.getOperand() instanceof Greater) {
            setExpression(new LowerOrEqual(((Greater) expr.getOperand()).getLeftOperand(), ((Greater) expr.getOperand()).getRightOperand()));
        }
        else if (expr.getOperand() instanceof GreaterOrEqual) {
            setExpression(new Lower(((GreaterOrEqual) expr.getOperand()).getLeftOperand(), ((GreaterOrEqual) expr.getOperand()).getRightOperand()));
        }
        else if (expr.getOperand() instanceof Lower){
            setExpression(new GreaterOrEqual(((Lower) expr.getOperand()).getLeftOperand(), ((Lower) expr.getOperand()).getRightOperand()));
        }
        else if (expr.getOperand() instanceof LowerOrEqual){
            setExpression(new Greater(((LowerOrEqual) expr.getOperand()).getLeftOperand(), ((LowerOrEqual) expr.getOperand()).getRightOperand()));
        }
        else if (expr.getOperand() instanceof Equals){
            setExpression(new NotEquals(((Equals) expr.getOperand()).getLeftOperand(), ((Equals) expr.getOperand()).getRightOperand()));
        }
        else if (expr.getOperand() instanceof NotEquals){
            setExpression(new Equals(((NotEquals) expr.getOperand()).getLeftOperand(), ((NotEquals) expr.getOperand()).getRightOperand()));
        }
        else {
            operandCodeGen(expr.getOperand(), true);
            VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();
            getCodeGenBackEnd().addInstruction(new CMP(new ImmediateInteger(0), result.requestPhysicalRegister()));
            if (getCodeGenBackEnd().getBranchCondition()) {
                getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
            }
            else {
                getCodeGenBackEnd().addInstruction(new BNE(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
            }
            return;
        }


        // generate code with change done
        AbstractExpr[] exp = {getExpression()};
        ListCodeGen(exp);
    }

    /**
     * method called to generate code for not operation printing
     */
    @Override
    public void print() {
        doOperation();

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * try to evaluate as a constant
     * @param compiler global compiler
     * @return created constant, can be null
     */
    @Override
    public Constant getConstant(DecacCompiler compiler) {
        // cast to Not
        Not expr = (Not) getExpression();

        Constant constant = expr.getOperand().getConstant(compiler);
        if (constant != null) {
            return new Constant(!constant.getValueBoolean());
        }

        return null;
    }
}
