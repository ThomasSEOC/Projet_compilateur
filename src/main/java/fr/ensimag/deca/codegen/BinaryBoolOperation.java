package fr.ensimag.deca.codegen;


import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Class making Binary Boolean Operations
 *
 * @author gl54
 * @date 10/01/2022
 */

public class BinaryBoolOperation  extends AbstractBinaryOperation{

    /**
     * Constructor pf class BinaryBoolOperation
     *
     * @param codegenbackend, expression
     */

    public BinaryBoolOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super(codegenbackend, expression);
    }

    /**
     * Main class making binary boolean operations
     * @param
     */
    @Override
    public void doOperation () {
        AbstractBinaryExpr expr = (AbstractBinaryExpr) this.getExpression();

        if (this.getExpression() instanceof And){
            // compute left part first and right part after
            getCodeGenBackEnd().setBranchCondition(false);
            AbstractExpr[] ops = {expr.getLeftOperand(), expr.getRightOperand()};
            ListCodeGen(ops);
        }
        else if (this.getExpression() instanceof Or) {
            getCodeGenBackEnd().incOrLabelsCount();

            Label trueLabel = new Label("Or_" + this.getCodeGenBackEnd().getOrLabelsCount() + "_true");
            Label falseLabel = new Label("Or_" + this.getCodeGenBackEnd().getOrLabelsCount() + "_false");

            getCodeGenBackEnd().trueBooleanLabelPush(trueLabel);
            getCodeGenBackEnd().falseBooleanLabelPush(falseLabel); // in case of and at left
            getCodeGenBackEnd().setBranchCondition(true);
            AbstractExpr[] op1 = {expr.getLeftOperand()};
            ListCodeGen(op1);

            getCodeGenBackEnd().getCompiler().addLabel(falseLabel);
            getCodeGenBackEnd().popCurrentFalseBooleanLabel();

            getCodeGenBackEnd().setBranchCondition(false);
            AbstractExpr[] op2 = {expr.getRightOperand()};
            ListCodeGen(op2);
            getCodeGenBackEnd().getCompiler().addLabel(trueLabel);
            getCodeGenBackEnd().popCurrentTrueBooleanLabel();
        }
        else {
            // classical boolean operation
            AbstractExpr[] ops = {expr.getLeftOperand(), expr.getRightOperand()};
            ListCodeGen(ops);
            VirtualRegister r2 = getCodeGenBackEnd().getContextManager().operationStackPop();
            VirtualRegister r1 = getCodeGenBackEnd().getContextManager().operationStackPop();
            getCodeGenBackEnd().getCompiler().addInstruction(new CMP(r1.getDVal(), r2.requestPhysicalRegister()));

            if (getCodeGenBackEnd().getBranchCondition()) {
                if (getExpression() instanceof Greater) {
                    getCodeGenBackEnd().getCompiler().addInstruction(new BGT(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (getExpression() instanceof  GreaterOrEqual) {
                    getCodeGenBackEnd().getCompiler().addInstruction(new BGE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (this.getExpression() instanceof Lower){
                    getCodeGenBackEnd().getCompiler().addInstruction(new BLT(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (this.getExpression() instanceof LowerOrEqual){
                    getCodeGenBackEnd().getCompiler().addInstruction(new BLE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (this.getExpression() instanceof Equals){
                    getCodeGenBackEnd().getCompiler().addInstruction(new BEQ(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (this.getExpression() instanceof NotEquals){
                    getCodeGenBackEnd().getCompiler().addInstruction(new BNE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
            }
            else {
                if (getExpression() instanceof Greater) {
                    getCodeGenBackEnd().getCompiler().addInstruction(new BLE(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (getExpression() instanceof  GreaterOrEqual) {
                    getCodeGenBackEnd().getCompiler().addInstruction(new BLT(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (this.getExpression() instanceof Lower){
                    getCodeGenBackEnd().getCompiler().addInstruction(new BGE(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (this.getExpression() instanceof LowerOrEqual){
                    getCodeGenBackEnd().getCompiler().addInstruction(new BGT(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (this.getExpression() instanceof Equals){
                    getCodeGenBackEnd().getCompiler().addInstruction(new BNE(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (this.getExpression() instanceof NotEquals){
                    getCodeGenBackEnd().getCompiler().addInstruction(new BEQ(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
            }
        }
    }


}
