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
     * @param codegenbackend global codegen backend
     * @param expression expression related to current operation
     */
    public BinaryBoolOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super(codegenbackend, expression);
    }

    /**
     * method called to generate code for binary boolean operation
     */
    @Override
    public void doOperation () {
        // cast expression to AbstractBinaryExpr
        AbstractBinaryExpr expr = (AbstractBinaryExpr) getExpression();

        // separate according to boolean operation, And and Or are managed in a different way
        if (this.getExpression() instanceof And){
            // branch if condition is false
            getCodeGenBackEnd().setBranchCondition(false);

            // compute left part first and right part after
            AbstractExpr[] ops = {expr.getLeftOperand(), expr.getRightOperand()};
            ListCodeGen(ops);
        }
        else if (this.getExpression() instanceof Or) {
            // request a nex Or label
            getCodeGenBackEnd().incOrLabelsCount();

            // create label for true and false conditions
            Label trueLabel = new Label("Or_" + this.getCodeGenBackEnd().getOrLabelsCount() + "_true");
            Label falseLabel = new Label("Or_" + this.getCodeGenBackEnd().getOrLabelsCount() + "_false");

            // push labels
            getCodeGenBackEnd().trueBooleanLabelPush(trueLabel);
            getCodeGenBackEnd().falseBooleanLabelPush(falseLabel); // in case of and at left

            // branch on true condition
            getCodeGenBackEnd().setBranchCondition(true);

            // generate code for left operand
            AbstractExpr[] op1 = {expr.getLeftOperand()};
            ListCodeGen(op1);

            // add label and pop it
            getCodeGenBackEnd().addLabel(falseLabel);
            getCodeGenBackEnd().popCurrentFalseBooleanLabel();

            // branch on false condition
            getCodeGenBackEnd().setBranchCondition(false);

            // generate code for right operand
            AbstractExpr[] op2 = {expr.getRightOperand()};
            ListCodeGen(op2);

            // add label and pop it
            getCodeGenBackEnd().addLabel(trueLabel);
            getCodeGenBackEnd().popCurrentTrueBooleanLabel();
        }
        else {
            // classical boolean operation

            // generate code for left and right operand
            AbstractExpr[] ops = {expr.getLeftOperand(), expr.getRightOperand()};
            ListCodeGen(ops);

            // get result out of operation stack
            VirtualRegister rOp = getCodeGenBackEnd().getContextManager().operationStackPop();
            VirtualRegister lOp = getCodeGenBackEnd().getContextManager().operationStackPop();

            // compare registers
            getCodeGenBackEnd().addInstruction(new CMP(rOp.getDVal(), lOp.requestPhysicalRegister()));

            // destroy registers
            rOp.destroy();
            lOp.destroy();

            // generate branch instruction
            if (getCodeGenBackEnd().getBranchCondition()) {
                // branch on true condition
                if (getExpression() instanceof Greater) {
                    getCodeGenBackEnd().addInstruction(new BGT(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (getExpression() instanceof  GreaterOrEqual) {
                    getCodeGenBackEnd().addInstruction(new BGE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (this.getExpression() instanceof Lower){
                    getCodeGenBackEnd().addInstruction(new BLT(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (this.getExpression() instanceof LowerOrEqual){
                    getCodeGenBackEnd().addInstruction(new BLE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (this.getExpression() instanceof Equals){
                    getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else if (this.getExpression() instanceof NotEquals){
                    getCodeGenBackEnd().addInstruction(new BNE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
            }
            else {
                // branch on false condition
                // need to inverse branch operations
                if (getExpression() instanceof Greater) {
                    getCodeGenBackEnd().addInstruction(new BLE(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (getExpression() instanceof  GreaterOrEqual) {
                    getCodeGenBackEnd().addInstruction(new BLT(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (this.getExpression() instanceof Lower){
                    getCodeGenBackEnd().addInstruction(new BGE(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (this.getExpression() instanceof LowerOrEqual){
                    getCodeGenBackEnd().addInstruction(new BGT(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (this.getExpression() instanceof Equals){
                    getCodeGenBackEnd().addInstruction(new BNE(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                else if (this.getExpression() instanceof NotEquals){
                    getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
            }
        }
    }

    /**
     * method called to generate code for printing result of binary boolean operation
     * pretty useless method
     */
    @Override
    public void print() {
        doOperation();

        throw new UnsupportedOperationException("not yet implemented");
    }


}
