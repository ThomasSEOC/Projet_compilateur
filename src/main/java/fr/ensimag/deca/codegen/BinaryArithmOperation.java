package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public void doOperation () {
		// cast expression to AbstractBinaryExpr
		AbstractBinaryExpr expr = (AbstractBinaryExpr) this.getExpression();

		boolean opti = (getCodeGenBackEnd().getCompiler().getCompilerOptions().getOptimize() > 0);

		Constant constant = null;
		if (opti) {
			constant = getConstant(getCodeGenBackEnd().getCompiler());
		}

		if (constant != null) {
			VirtualRegister result;
			if (constant.getIsFloat()) {
				result = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateFloat(constant.getValueFloat()));
			}
			else {
				result = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(constant.getValueInt()));
			}
			getCodeGenBackEnd().getContextManager().operationStackPush(result);
			return;
		}

		// generate code for left and right operands
		AbstractExpr[] ops = {expr.getLeftOperand(), expr.getRightOperand()};
		this.ListCodeGen(ops);

		// pop result out of operation stack
		VirtualRegister rOp = getCodeGenBackEnd().getContextManager().operationStackPop();
		VirtualRegister lOp = getCodeGenBackEnd().getContextManager().operationStackPop();

		// separate code generation according to arithmetic operation
		if (this.getExpression() instanceof Plus) {
			// get correct operand
			lOp.requestPhysicalRegister();
			getCodeGenBackEnd().addInstruction(new ADD(rOp.getDVal(), (GPRegister) lOp.getDVal()), "Plus");
			if (lOp.getIsFloat()) {
				if (!getCodeGenBackEnd().getCompiler().getCompilerOptions().getNoCheckStatus()) {
					getCodeGenBackEnd().addInstruction(new BOV(getCodeGenBackEnd().getErrorsManager().getDivisionByZeroLabel()));
				}
			}
			rOp.destroy();
			this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
		} else if (this.getExpression() instanceof Minus) {
			// get correct operand
			lOp.requestPhysicalRegister();
			getCodeGenBackEnd().addInstruction(new SUB(rOp.getDVal(), (GPRegister) lOp.getDVal()), "Minus");
			if (lOp.getIsFloat()) {
				if (!getCodeGenBackEnd().getCompiler().getCompilerOptions().getNoCheckStatus()) {
					getCodeGenBackEnd().addInstruction(new BOV(getCodeGenBackEnd().getErrorsManager().getDivisionByZeroLabel()));
				}
			}
			rOp.destroy();
			this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
		} else if (this.getExpression() instanceof Multiply) {
			VirtualRegister register = null;
			int operand = 0;
			if (rOp.getDVal() instanceof ImmediateInteger) {
				register = lOp;
				operand = ((ImmediateInteger)rOp.getDVal()).getValue();
			}
			else if (lOp.getDVal() instanceof ImmediateInteger) {
				register = rOp;
				operand = ((ImmediateInteger)lOp.getDVal()).getValue();
			}

			int shiftCount = 0;
			if (operand > 0) {
				List<Integer> powerOfTwo = new ArrayList<>();
				for (int i = 1; i < 9; i++) {
					powerOfTwo.add(1 << i);
				}

				if (powerOfTwo.contains(operand)) {
					shiftCount = powerOfTwo.indexOf(operand) + 1;
				}
			}

			if (shiftCount > 0) {
				for (int i = 0; i < shiftCount; i++) {
					getCodeGenBackEnd().addInstruction(new SHL(register.requestPhysicalRegister()));
				}
				getCodeGenBackEnd().getContextManager().operationStackPush(register);
			}
			else {
				// get correct operand
				lOp.requestPhysicalRegister();
				getCodeGenBackEnd().addInstruction(new MUL(rOp.getDVal(), (GPRegister) lOp.getDVal()), "Multiply");
				if (lOp.getIsFloat()) {
					if (!getCodeGenBackEnd().getCompiler().getCompilerOptions().getNoCheckStatus()) {
						getCodeGenBackEnd().addInstruction(new BOV(getCodeGenBackEnd().getErrorsManager().getDivisionByZeroLabel()));
					}
				}
				rOp.destroy();
				getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
			}
		} else if (this.getExpression() instanceof Divide) {
			VirtualRegister register = null;
			int shiftCount = 0;
			if ((rOp.getDVal() instanceof ImmediateInteger) && !lOp.getIsFloat()) {
				register = lOp;
				int operand = ((ImmediateInteger)rOp.getDVal()).getValue();
				if (operand > 0) {
					List<Integer> powerOfTwo = new ArrayList<>();
					for (int i = 1; i < 9; i++) {
						powerOfTwo.add(1 << i);
					}
					if (powerOfTwo.contains(operand)) {
						shiftCount = powerOfTwo.indexOf(operand) + 1;
					}
				}
			}

			if (shiftCount > 0) {
				for (int i = 0; i < shiftCount; i++) {
					getCodeGenBackEnd().addInstruction(new SHR(register.requestPhysicalRegister()));
				}
				getCodeGenBackEnd().getContextManager().operationStackPush(register);
			}
			else {
				// get correct operand
				if (lOp.getIsFloat() || rOp.getIsFloat()) {
					getCodeGenBackEnd().addInstruction(new DIV(rOp.getDVal(), lOp.requestPhysicalRegister()), "float divide");
				} else {
					getCodeGenBackEnd().addInstruction(new QUO(rOp.getDVal(), lOp.requestPhysicalRegister()), "int divide");
				}
				if (!getCodeGenBackEnd().getCompiler().getCompilerOptions().getNoCheckStatus()) {
					getCodeGenBackEnd().addInstruction(new BOV(getCodeGenBackEnd().getErrorsManager().getDivisionByZeroLabel()));
				}
				rOp.destroy();
				this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
			}
		} else if (this.getExpression() instanceof Modulo) {
			VirtualRegister register = null;
			int shiftCount = 0;
			if (rOp.getDVal() instanceof ImmediateInteger) {
				register = lOp;
				int operand = ((ImmediateInteger)rOp.getDVal()).getValue();
				if (operand > 0) {
					List<Integer> powerOfTwo = new ArrayList<>();
					for (int i = 1; i < 5; i++) {
						powerOfTwo.add(1 << i);
					}
					if (powerOfTwo.contains(operand)) {
						shiftCount = powerOfTwo.indexOf(operand) + 1;
					}
				}
			}
			if (shiftCount > 0) {
				getCodeGenBackEnd().addInstruction(new LOAD(register.requestPhysicalRegister(), GPRegister.getR(0)), "simplify modulo using shifts");
				for (int i = 0; i < shiftCount; i++) {
					getCodeGenBackEnd().addInstruction(new SHR(GPRegister.getR(0)));
				}
				for (int i = 0; i < shiftCount; i++) {
					getCodeGenBackEnd().addInstruction(new SHL(GPRegister.getR(0)));
				}
				getCodeGenBackEnd().addInstruction(new SUB(GPRegister.getR(0), register.requestPhysicalRegister()));
				getCodeGenBackEnd().getContextManager().operationStackPush(register);
			}
			else {
				getCodeGenBackEnd().addInstruction(new REM(rOp.getDVal(), lOp.requestPhysicalRegister()), "int remainder");
				if (!getCodeGenBackEnd().getCompiler().getCompilerOptions().getNoCheckStatus()) {
					getCodeGenBackEnd().addInstruction(new BOV(getCodeGenBackEnd().getErrorsManager().getDivisionByZeroLabel()));
				}
				rOp.destroy();
				this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
			}
		}
		else {
			throw new UnsupportedOperationException("unknown arithmetic operation");
		}
	}

	@Override
	public Constant getConstant(DecacCompiler compiler) {
		// cast expression to AbstractBinaryExpr
		AbstractBinaryExpr expr = (AbstractBinaryExpr) this.getExpression();

		// get operand recursively
		Constant cLOp = expr.getLeftOperand().getConstant(compiler);
		Constant cROp = expr.getRightOperand().getConstant(compiler);
		if ((cLOp == null) || (cROp == null)) {
			return null;
		}

		if (cLOp.getIsFloat()) {
			float op1 = cLOp.getValueFloat();
			float op2 = cROp.getValueFloat();

			if (this.getExpression() instanceof Plus) {
				float resultFloat = op1 + op2;
				return new Constant(resultFloat);
			}
			else if (this.getExpression() instanceof Minus) {
				float resultFloat = op1 - op2;
				return new Constant(resultFloat);
			}
			else if (this.getExpression() instanceof Multiply) {
				float resultFloat = op1 * op2;
				return new Constant(resultFloat);
			}
			else if (this.getExpression() instanceof Divide) {
				if (op2 == 0) {
					return null;
				}
				float resultFloat = op1 / op2;
				return new Constant(resultFloat);
			}
		}
		else {
			int op1 = cLOp.getValueInt();
			int op2 = cROp.getValueInt();

			if (this.getExpression() instanceof Plus) {
				int resultInt = op1 + op2;
				return new Constant(resultInt);
			}
			else if (this.getExpression() instanceof Minus) {
				int resultInt = op1 - op2;
				return new Constant(resultInt);
			}
			else if (this.getExpression() instanceof Multiply) {
				int resultInt = op1 * op2;
				return new Constant(resultInt);
			} else if (this.getExpression() instanceof Divide) {
				if (op2 == 0) {
					return null;
				}
				int resultInt = op1 / op2;
				return new Constant(resultInt);
			} else if (this.getExpression() instanceof Modulo) {
				if (op2 == 0) {
					return null;
				}
				int resultInt = op1 % op2;
				return new Constant(resultInt);
			}
		}

		return null;
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
