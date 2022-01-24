package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Once the tree done, this Class allows choosing
 * which class will make calculations
 *
 * @author gl54
 * @date 11/01/2022
 */
public abstract class AbstractOperation {

    private final CodeGenBackend codegenbackend;
    private AbstractExpr expression;

    /**
     * Constructor of class AbstractOperation
     * @param codegenbackend global backend to every codegen steps
     * @param expression expression related to the current operation
     */
    public AbstractOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        this.codegenbackend = codegenbackend;
        this.expression = expression;
    }

    /**
     * called by codeGenInst method in AbstractExpr derived class
     */
    public abstract void doOperation();

    /**
     * called by codeGenPrint method in AbstractExpr derived class
     */
    public abstract void print();

    /**
     * Recursion on the branch expressions
     * There are usually 1 or 2 expressions in AbstractExpr[]
     * @param expressions list of AbstractExpr
     */
    public void ListCodeGen(AbstractExpr[] expressions) {
        ListInst list = new ListInst();
        for (AbstractExpr abstractExpr : expressions) {
            list.add(abstractExpr);
        }
        list.codeGenListInst(codegenbackend.getCompiler());
    }

    /**
     * generate code for condition operands
     * @param operand expression to process
     * @return true if a result added in operation stack
     */
    public boolean operandCodeGen(AbstractExpr operand) {
        return operandCodeGen(operand, false);
    }

    /**
     * generate code for condition operands
     * @param operand expression to process
     * @param doNotBranch set true to do not generate a branch
     * @return true if a result added in operation stack
     */
    public boolean operandCodeGen(AbstractExpr operand, boolean doNotBranch) {
        if (operand instanceof Assign) {
            AssignOperation operator = new AssignOperation(getCodeGenBackEnd(), operand);
            operator.doOperation(true);
            return true;
        }
        else if (operand instanceof InstanceOf) {
            InstanceofOperation operator = new InstanceofOperation(getCodeGenBackEnd(), operand);
            operator.doOperation();

            // result is in R0
            VirtualRegister result;
            getCodeGenBackEnd().addInstruction(new CMP(new ImmediateInteger(0), GPRegister.getR(0)));
            if (doNotBranch) {
                result = getCodeGenBackEnd().getContextManager().requestNewRegister();
                getCodeGenBackEnd().addInstruction(new SNE(result.requestPhysicalRegister()));
            }
            else {
                if (getCodeGenBackEnd().getBranchCondition()) {
                    getCodeGenBackEnd().addInstruction(new BNE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                    result = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(0));
                }
                else {
                    getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                    result = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(0));
                }
            }

            getCodeGenBackEnd().getContextManager().operationStackPush(result);
            return true;
        }
        else if (operand instanceof BooleanLiteral) {
            LiteralOperation operator = new LiteralOperation(getCodeGenBackEnd(), operand);
            operator.doOperation();
            if (!doNotBranch) {
                VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();
                getCodeGenBackEnd().addInstruction(new CMP(new ImmediateInteger(0), result.requestPhysicalRegister()));
                if (getCodeGenBackEnd().getBranchCondition()) {
                    getCodeGenBackEnd().addInstruction(new BNE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
                }
                else {
                    getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
                }
                getCodeGenBackEnd().getContextManager().operationStackPush(result);
            }

            return true;
        }
        else if (operand instanceof Identifier) {
            IdentifierRead operator = new IdentifierRead(getCodeGenBackEnd(), operand);
            operator.doOperation(doNotBranch);
            if (doNotBranch) {
                return true;
            }
            return false;
        }
        else {
            AbstractExpr[] inst = {operand};
            ListCodeGen(inst);
            return false;
        }
    }

    /**
     * getter of expression
     * @return expression related to current Operation
     */
    public AbstractExpr getExpression(){
        return this.expression;
    }

    /**
     * Setter of Expression in AbstractOperation Class
     * @param expression expression related to current operation
     */
    public void setExpression(AbstractExpr expression) {
        this.expression = expression;
    }

    /**
     * Getter for codegenbackend
     * @return codegenbackend
     */
    public CodeGenBackend getCodeGenBackEnd(){
        return this.codegenbackend;
    }

    /**
     * compute constant associated to current operation
     * @param compiler global compiler
     * @return non-null Constant object if constant folding was a success
     */
    public Constant getConstant(DecacCompiler compiler) {
        return null;
    }
}
