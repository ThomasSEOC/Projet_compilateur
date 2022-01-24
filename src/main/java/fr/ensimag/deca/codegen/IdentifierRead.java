package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * class dedicated to identifier/global variable read
 */
public class IdentifierRead extends AbstractOperation {

    /**
     * constructor for IdentifierRead
     * @param backend global codegen backend
     * @param expression expression related to operation
     */
    public IdentifierRead(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * method called to generate code for declared variable read
     */
    public void doOperation(boolean doNotBranch) {
        // try to evaluate as constant
        boolean opti = (getCodeGenBackEnd().getCompiler().getCompilerOptions().getOptimize() > 0);
        Constant constant = null;
        if (opti) {
            constant = getConstant(getCodeGenBackEnd().getCompiler());
        }

        if (constant != null) {
            VirtualRegister result;
            if (constant.getIsFloat()) {
                result = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateFloat(constant.getValueFloat()));
                getCodeGenBackEnd().getContextManager().operationStackPush(result);

            }
            else if (constant.getIsBoolean()) {
                if (doNotBranch) {
                    VirtualRegister register;
                    if (constant.getValueBoolean()) {
                        register = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(1));
                    }
                    else {
                        register = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(0));
                    }
                    getCodeGenBackEnd().getContextManager().operationStackPush(register);
                }
                else {
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
                }
            }
            else {
                result = getCodeGenBackEnd().getContextManager().requestNewRegister(new ImmediateInteger(constant.getValueInt()));
                getCodeGenBackEnd().getContextManager().operationStackPush(result);
            }
            return;
        }

        // cast to Identifier
        Identifier expr = (Identifier) getExpression();

        // request a new virtual register
//        VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister();

        // get register offset for variable identified by expr
        RegisterOffset registerOffset = getCodeGenBackEnd().getVariableRegisterOffset(expr.getName().getName());

        // load into physical register
//        getCodeGenBackEnd().addInstruction(new LOAD(registerOffset, r.requestPhysicalRegister()));

        VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister(registerOffset);

        if (doNotBranch) {
            // push virtual register to operation stack
            getCodeGenBackEnd().getContextManager().operationStackPush(r);
        }
        else {
            getCodeGenBackEnd().addInstruction(new CMP(new ImmediateInteger(0), r.requestPhysicalRegister()));
            if (getCodeGenBackEnd().getBranchCondition()) {
                getCodeGenBackEnd().addInstruction(new BNE(getCodeGenBackEnd().getCurrentTrueBooleanLabel()));
            }
            else {
                getCodeGenBackEnd().addInstruction(new BEQ(getCodeGenBackEnd().getCurrentFalseBooleanLabel()));
            }
        }
    }

    /**
     * method called to generate code for declared variable read
     */
    @Override
    public void doOperation() {
       doOperation(true);
    }

    /**
     * method called to generate code for printing a declared variable
     */
    @Override
    public void print() {
        boolean opti = (getCodeGenBackEnd().getCompiler().getCompilerOptions().getOptimize() > 0);

        Constant constant = null;
        if (opti) {
            constant = getConstant(getCodeGenBackEnd().getCompiler());
        }

        if (constant != null) {
            VirtualRegister result;
            if (constant.getIsFloat()) {
                getCodeGenBackEnd().addInstruction(new LOAD(new ImmediateFloat(constant.getValueFloat()), GPRegister.getR(1)));
                if (getCodeGenBackEnd().getPrintHex()) {
                    getCodeGenBackEnd().addInstruction(new WFLOATX());
                }
                else {
                    getCodeGenBackEnd().addInstruction(new WFLOAT());
                }
            }
            else {
                getCodeGenBackEnd().addInstruction(new LOAD(new ImmediateInteger(constant.getValueInt()), GPRegister.getR(1)));
                getCodeGenBackEnd().addInstruction(new WINT());
            }
            return;
        }

        // cast to Identifier
        Identifier expr = (Identifier) getExpression();

        // get offset from GB for variable identified by expr
        RegisterOffset registerOffset = getCodeGenBackEnd().getVariableRegisterOffset(expr.getName().getName());

        // load into physical register
        getCodeGenBackEnd().addInstruction(new LOAD(registerOffset, GPRegister.getR(1)));

        // separate according to type
        if (expr.getType() instanceof IntType) {
            getCodeGenBackEnd().addInstruction(new WINT());
        }
        else if (expr.getType() instanceof FloatType) {
            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().addInstruction(new WFLOATX());
            }
            else {
                getCodeGenBackEnd().addInstruction(new WFLOAT());
            }
        }
        else
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    /**
     * try to evaluate operation as constant
     * @param compiler global compiler
     * @return created constant, can be null
     */
    @Override
    public Constant getConstant(DecacCompiler compiler) {
        // cast to UnaryMinus
        Identifier expr = (Identifier) getExpression();

        Constant cOp = expr.getConstant(compiler);

        if (cOp == null) {
            return null;
        }

        if (cOp.getIsFloat()) {
            return new Constant(cOp.getValueFloat());
        }
        else if (cOp.getIsBoolean()) {
            return new Constant(cOp.getValueBoolean());
        }
        else {
            return new Constant(cOp.getValueInt());
        }
    }
}
