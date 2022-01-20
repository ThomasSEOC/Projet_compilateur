package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Class making binary arithmetical operations
 *
 * @author gl54
 * @date 10/01/2022
 */
public class BinaryArithmOperation extends AbstractBinaryOperation {
    /**
     * Constructor of BinaryArithmOperation
     *
     * @param codegenbackend global codegen backend
     * @param expression expression related to current operation
     */
    public BinaryArithmOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super(codegenbackend, expression);
    }

    /**
     * Method called to generate code for binary arithmetic operation
     */
    @Override
    public void doOperation (){
        // cast expression to AbstractBinaryExpr
        AbstractBinaryExpr expr = (AbstractBinaryExpr) this.getExpression();

        // generate code for left and right operands
        AbstractExpr[] ops = {expr.getLeftOperand(), expr.getRightOperand()};
        this.ListCodeGen(ops);

        // pop result out of operation stack
        VirtualRegister rOp = getCodeGenBackEnd().getContextManager().operationStackPop();
        VirtualRegister lOp = getCodeGenBackEnd().getContextManager().operationStackPop();

	bool opti = getCodeGenBackEnd().getCompiler().getCompilerOptions().getOptimize();
	
        // separate code generation according to arithmetic operation
        if (this.getExpression() instanceof Plus){

	    if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
		ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
		ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() + op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }
		
	    else if ((lOp.getDVal() instanceof ImmediateFloat) && (rOp.getDVal() instanceof ImmediateFloat) && opti) {
		ImmediateFloat op1 = (ImmediateFloat) lOp.getDVal();
		ImmediateFloat op2 = (ImmediateFloat) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateFloat resultImm = new ImmediateFloat(op1.getValue() + op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }
	    
	    else {
		// get correct operand
		lOp.requestPhysicalRegister();
		getCodeGenBackEnd().addInstruction(new ADD(rOp.getDVal(), lOp.getDVal()), "Operation Plus");
		rOp.destroy();
		this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
	    }
	}
	
        else if (this.getExpression() instanceof Minus){

	    if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
		ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
		ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() - op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }
	    
	    else if ((lOp.getDVal() instanceof ImmediateFloat) && (rOp.getDVal() instanceof ImmediateFloat) && opti) {
		ImmediateFloat op1 = (ImmediateFloat) lOp.getDVal();
		ImmediateFloat op2 = (ImmediateFloat) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateFloat resultImm = new ImmediateFloat(op1.getValue() - op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }
		
	    else {
		// get correct operand
		lOp.requestPhysicalRegister();
		getCodeGenBackEnd().addInstruction(new ADD(rOp.getDVal(), lOp.getDVal()), "Operation Minus");
		rOp.destroy();
		this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
	    }
	}
	
        else if (this.getExpression() instanceof Multiply){
	    
	    if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
		ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
		ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() * op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }
	    
	    else if ((lOp.getDVal() instanceof ImmediateFloat) && (rOp.getDVal() instanceof ImmediateFloat) && opti) {
		ImmediateFloat op1 = (ImmediateFloat) lOp.getDVal();
		ImmediateFloat op2 = (ImmediateFloat) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateFloat resultImm = new ImmediateFloat(op1.getValue() * op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }
		
	    else {
		// get correct operand
		lOp.requestPhysicalRegister();
		getCodeGenBackEnd().addInstruction(new ADD(rOp.getDVal(), lOp.getDVal()), "Operation Multiply");
		rOp.destroy();
		this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
	    }
	}
	    
	else if (this.getExpression() instanceof Divide){
	    
	    if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
		ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
		ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() / op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }
		
	    else if ((lOp.getDVal() instanceof ImmediateFloat) && (rOp.getDVal() instanceof ImmediateFloat) && opti) {
		ImmediateFloat op1 = (ImmediateFloat) lOp.getDVal();
		ImmediateFloat op2 = (ImmediateFloat) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateFloat resultImm = new ImmediateFloat(op1.getValue() / op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }
	    
	    else {
		// get correct operand
		if (lOp.getIsFloat() || rOp.getIsFloat()) {
		    getCodeGenBackEnd().addInstruction(new DIV(rOp.getDVal(), lOp.requestPhysicalRegister()), "Operation Division");
		}
		else {
                getCodeGenBackEnd().addInstruction(new QUO(rOp.getDVal(), lOp.requestPhysicalRegister()), "Operation Quotient");
		}
		rOp.destroy();
		this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
	    }
	}
	else if (this.getExpression() instanceof Modulo){

	    if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
		ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
		ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
		rOp.destroy();
		lOp.destroy();
		ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() % op2.getValue());
		VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
		getCodeGenBackEnd().getContextManager().operationStackPush(result);
	    }

	    else{
		getCodeGenBackEnd().addInstruction(new REM(rOp.getDVal(), lOp.requestPhysicalRegister()), "Operation Remainder");
		rOp.destroy();
		this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
	    }
	    
        else {
            throw new UnsupportedOperationException("unknown arithmetic operation");
        }
    }

    /**
     * method called to generate code to print result of binary arithmetic operation
     */
    @Override
    public void print() {
        doOperation();

        // get result
        VirtualRegister r = getCodeGenBackEnd().getContextManager().operationStackPop();

        // move result to R1
        getCodeGenBackEnd().addInstruction(new LOAD(r.getDVal(), GPRegister.getR(1)));

        // use appropriate write instruction according to type and Hex
        if (r.getIsFloat()) {
            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().addInstruction(new WFLOATX());
            }
            else {
                getCodeGenBackEnd().addInstruction(new WFLOAT());
            }
        }
        else {
            getCodeGenBackEnd().addInstruction(new WINT());
        }

        // free used register
        r.destroy();
    }

}
