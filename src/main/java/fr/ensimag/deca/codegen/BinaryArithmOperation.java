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

		// generate code for left and right operands
		AbstractExpr[] ops = {expr.getLeftOperand(), expr.getRightOperand()};
		this.ListCodeGen(ops);

		// pop result out of operation stack
		VirtualRegister rOp = getCodeGenBackEnd().getContextManager().operationStackPop();
		VirtualRegister lOp = getCodeGenBackEnd().getContextManager().operationStackPop();

		boolean opti = getCodeGenBackEnd().getCompiler().getCompilerOptions().getOptimize();

		// separate code generation according to arithmetic operation
		if (this.getExpression() instanceof Plus) {

			if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
				ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
				ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() + op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else if ((lOp.getDVal() instanceof ImmediateFloat) && (rOp.getDVal() instanceof ImmediateFloat) && opti) {
				ImmediateFloat op1 = (ImmediateFloat) lOp.getDVal();
				ImmediateFloat op2 = (ImmediateFloat) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateFloat resultImm = new ImmediateFloat(op1.getValue() + op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else {
				// get correct operand
				lOp.requestPhysicalRegister();
				getCodeGenBackEnd().addInstruction(new ADD(rOp.getDVal(), (GPRegister) lOp.getDVal()), "Operation Plus");
				rOp.destroy();
				this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
			}
		} else if (this.getExpression() instanceof Minus) {

			if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
				ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
				ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() - op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else if ((lOp.getDVal() instanceof ImmediateFloat) && (rOp.getDVal() instanceof ImmediateFloat) && opti) {
				ImmediateFloat op1 = (ImmediateFloat) lOp.getDVal();
				ImmediateFloat op2 = (ImmediateFloat) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateFloat resultImm = new ImmediateFloat(op1.getValue() - op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else {
				// get correct operand
				lOp.requestPhysicalRegister();
				getCodeGenBackEnd().addInstruction(new SUB(rOp.getDVal(), (GPRegister) lOp.getDVal()), "Operation Minus");
				rOp.destroy();
				this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
			}
		} else if (this.getExpression() instanceof Multiply) {

			if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
				ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
				ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() * op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else if ((lOp.getDVal() instanceof ImmediateFloat) && (rOp.getDVal() instanceof ImmediateFloat) && opti) {
				ImmediateFloat op1 = (ImmediateFloat) lOp.getDVal();
				ImmediateFloat op2 = (ImmediateFloat) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateFloat resultImm = new ImmediateFloat(op1.getValue() * op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else {
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
					getCodeGenBackEnd().addInstruction(new MUL(rOp.getDVal(), (GPRegister) lOp.getDVal()), "Operation Multiply");
					rOp.destroy();
					getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
				}
			}
		} else if (this.getExpression() instanceof Divide) {

			if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
				ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
				ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() / op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else if ((lOp.getDVal() instanceof ImmediateFloat) && (rOp.getDVal() instanceof ImmediateFloat) && opti) {
				ImmediateFloat op1 = (ImmediateFloat) lOp.getDVal();
				ImmediateFloat op2 = (ImmediateFloat) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateFloat resultImm = new ImmediateFloat(op1.getValue() / op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else {
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
						getCodeGenBackEnd().addInstruction(new DIV(rOp.getDVal(), lOp.requestPhysicalRegister()), "Operation Division");
					} else {
						getCodeGenBackEnd().addInstruction(new QUO(rOp.getDVal(), lOp.requestPhysicalRegister()), "Operation Quotient");
					}
					rOp.destroy();
					this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
				}
			}
		} else if (this.getExpression() instanceof Modulo) {

			if ((lOp.getDVal() instanceof ImmediateInteger) && (rOp.getDVal() instanceof ImmediateInteger) && opti) {
				ImmediateInteger op1 = (ImmediateInteger) lOp.getDVal();
				ImmediateInteger op2 = (ImmediateInteger) rOp.getDVal();
				rOp.destroy();
				lOp.destroy();
				ImmediateInteger resultImm = new ImmediateInteger(op1.getValue() % op2.getValue());
				VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister(resultImm);
				getCodeGenBackEnd().getContextManager().operationStackPush(result);
			} else {
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
					getCodeGenBackEnd().addInstruction(new LOAD(register.requestPhysicalRegister(), GPRegister.getR(0)));
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
					getCodeGenBackEnd().addInstruction(new REM(rOp.getDVal(), lOp.requestPhysicalRegister()), "Operation Remainder");
					rOp.destroy();
					this.getCodeGenBackEnd().getContextManager().operationStackPush(lOp);
				}
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

		Constant cLOp = expr.getLeftOperand().getConstant(compiler);
		Constant cROp = expr.getRightOperand().getConstant(compiler);
		if (cLOp == null || cROp == null) {
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
			}
			else if (this.getExpression() instanceof Divide) {
				int resultInt = op1 / op2;
				return new Constant(resultInt);
			}
			else if (this.getExpression() instanceof Modulo) {
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
