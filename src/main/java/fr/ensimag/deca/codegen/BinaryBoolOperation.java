package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.opti.Constant;
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
     * Constructor for class BinaryBoolOperation
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
        // try to simplify operation by a constant
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

        // cast expression to AbstractBinaryExpr
        AbstractBinaryExpr expr = (AbstractBinaryExpr) getExpression();

        // separate according to boolean operation, And and Or are managed in a different way
        if (this.getExpression() instanceof And){
            // branch if condition is false
            getCodeGenBackEnd().setBranchCondition(false);

            // compute left part first and right part after
            // generate code for left and right operand
            if (operandCodeGen(expr.getLeftOperand())) {
                getCodeGenBackEnd().getContextManager().operationStackPop();
            }

            if (operandCodeGen(expr.getRightOperand())) {
                getCodeGenBackEnd().getContextManager().operationStackPop();
            }
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
//            AbstractExpr[] op1 = {expr.getLeftOperand()};
//            ListCodeGen(op1);
            if (operandCodeGen(expr.getLeftOperand())) {
                getCodeGenBackEnd().getContextManager().operationStackPop();
            }

            // add label and pop it
            getCodeGenBackEnd().addLabel(falseLabel);
            getCodeGenBackEnd().popCurrentFalseBooleanLabel();

            // branch on false condition
            getCodeGenBackEnd().setBranchCondition(false);

            // generate code for right operand
//            AbstractExpr[] op2 = {expr.getRightOperand()};
//            ListCodeGen(op2);
            if (operandCodeGen(expr.getRightOperand())) {
                getCodeGenBackEnd().getContextManager().operationStackPop();
            }

            // add label and pop it
            getCodeGenBackEnd().addLabel(trueLabel);
            getCodeGenBackEnd().popCurrentTrueBooleanLabel();
        }
        else {
            // classical boolean operation

            // generate code for left and right operand
            operandCodeGen(expr.getLeftOperand(), true);
            VirtualRegister lOp = getCodeGenBackEnd().getContextManager().operationStackPop();

            operandCodeGen(expr.getRightOperand(), true);
            VirtualRegister rOp = getCodeGenBackEnd().getContextManager().operationStackPop();

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
     * try to evaluate operation as a constant
     * @param compiler global compiler
     * @return Constant result, can be null
     */
    @Override
    public Constant getConstant(DecacCompiler compiler) {
        // cast expression to AbstractBinaryExpr
        AbstractBinaryExpr expr = (AbstractBinaryExpr) this.getExpression();

        // get operand recursively
        Constant cLOp = expr.getLeftOperand().getConstant(compiler);
        Constant cROp = expr.getRightOperand().getConstant(compiler);
        if (cLOp == null || cROp == null) {
            return null;
        }

        // compute constant according to data type and operation
        if (cLOp.getIsFloat()) {
            float op1 = cLOp.getValueFloat();
            float op2 = cROp.getValueFloat();

            if (this.getExpression() instanceof Greater) {
                return new Constant(op1 > op2);
            }
            else if (this.getExpression() instanceof GreaterOrEqual) {
                return new Constant(op1 >= op2);
            }
            else if (this.getExpression() instanceof Lower) {
                return new Constant(op1 < op2);
            }
            else if (this.getExpression() instanceof LowerOrEqual) {
                return new Constant(op1 <= op2);
            }
            else if (this.getExpression() instanceof Equals) {
                return new Constant(op1 == op2);
            }
            else if (this.getExpression() instanceof NotEquals) {
                return new Constant(op1 != op2);
            }
        }
        else {
            if (cLOp.getIsBoolean()) {
                boolean op1 = cLOp.getValueBoolean();
                boolean op2 = cROp.getValueBoolean();

                if (this.getExpression() instanceof Equals) {
                    return new Constant(op1 == op2);
                }
                else if (this.getExpression() instanceof NotEquals) {
                    return new Constant(op1 != op2);
                }
            }
            else {
                int op1 = cLOp.getValueInt();
                int op2 = cROp.getValueInt();

                if (this.getExpression() instanceof Greater) {
                    return new Constant(op1 > op2);
                }
                else if (this.getExpression() instanceof GreaterOrEqual) {
                    return new Constant(op1 >= op2);
                }
                else if (this.getExpression() instanceof Lower) {
                    return new Constant(op1 < op2);
                }
                else if (this.getExpression() instanceof LowerOrEqual) {
                    return new Constant(op1 <= op2);
                }
                else if (this.getExpression() instanceof Equals) {
                    return new Constant(op1 == op2);
                }
                else if (this.getExpression() instanceof NotEquals) {
                    return new Constant(op1 != op2);
                }
            }
        }
        return null;

    }

    /**
     * method called to generate code for printing result of binary boolean operation
     * pretty useless method
     */
    @Override
    public void print() {
        // cannot print result of boolean operation
        throw new UnsupportedOperationException("operation not permitted");
    }


}
