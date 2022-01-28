package fr.ensimag.deca.codegen;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * class dedicated to assign operation code generation
 *
 * @author gl54
 * @date 11/01/2022
 */
public class AssignOperation extends AbstractOperation {

    /**
     * constructor of class AssignOperation
     * @param codegenbackend global codegen backend
     * @param expression expression related to current operation
     */
    public AssignOperation(CodeGenBackend codegenbackend, AbstractExpr expression) {
        super(codegenbackend, expression);
    }

    /**
     * method called to generate code for assignation
     */
    @Override
    public void doOperation() {
        doOperation(false);
    }

    /**
     * method called to generate code for assignation
     * @param keepValueInOperationStack do not destroy result virtual register of true
     */
    public void doOperation(boolean keepValueInOperationStack) {
        // cast expression
        Assign expr = (Assign) this.getExpression();

        if ((expr.getRightOperand() instanceof AbstractOpCmp) || (expr.getRightOperand() instanceof AbstractOpBool)) {
            // need to create if statement

            // create then branch
            Assign okAssign = new Assign(expr.getLeftOperand(), new BooleanLiteral(true));
            ListInst okListInst = new ListInst();
            okListInst.add(okAssign);

            // create else branch
            Assign notOkAssign = new Assign(expr.getLeftOperand(), new BooleanLiteral(false));
            ListInst notOkListInst = new ListInst();
            notOkListInst.add(notOkAssign);

            // create statement
            IfThenElse ifThenElse = new IfThenElse(expr.getRightOperand(), okListInst, notOkListInst);
            ifStatement operator = new ifStatement(getCodeGenBackEnd(), ifThenElse);
            operator.createStatement();

            if (keepValueInOperationStack) {
                // keep result in a register and push it on operation stack
                VirtualRegister result = getCodeGenBackEnd().getContextManager().requestNewRegister();
                if (expr.getLeftOperand() instanceof Identifier) {
                    Identifier id = (Identifier) expr.getLeftOperand();
                    RegisterOffset ro = new RegisterOffset(getCodeGenBackEnd().getVariableOffset(id.getName().getName()), Register.LB);
                    getCodeGenBackEnd().addInstruction(new LOAD(ro, result.requestPhysicalRegister()));
                    getCodeGenBackEnd().getContextManager().operationStackPush(result);
                }
                else if (expr.getLeftOperand() instanceof Selection){
                    Selection sel = (Selection) expr.getLeftOperand();
                    FieldSelectOperation selOperator = new FieldSelectOperation(getCodeGenBackEnd(), sel);
                    selOperator.doOperation();
                }
            }
        }
        else {
            // generate code for right operand
            AbstractExpr[] listExprs = {expr.getRightOperand()};
            this.ListCodeGen(listExprs);

            // get result of right operand computation
            VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();

            // generate address where to store result
            if (expr.getLeftOperand() instanceof Identifier) {
                Identifier identifier = (Identifier) expr.getLeftOperand();
                DAddr addr;
                if (identifier.getDefinition() instanceof FieldDefinition) {
                    String name = identifier.getName().getName();
                    addr = getCodeGenBackEnd().getVariableRegisterOffset(name);
                }
                else {
                    addr = identifier.getVariableDefinition().getOperand();
                }

                // store result
                getCodeGenBackEnd().addInstruction(new STORE(result.requestPhysicalRegister(), addr));

                if (getCodeGenBackEnd().getCompiler().getCompilerOptions().getOptimize() > 0) {
                    if (addr instanceof RegisterOffset) {
                        RegisterOffset registerOffset = (RegisterOffset) addr;
                        getCodeGenBackEnd().getContextManager().setLastStoreRegister(result, registerOffset);
                    }
                }
            }
            else if (expr.getLeftOperand() instanceof Selection) {
                result.requestPhysicalRegister();

                getCodeGenBackEnd().getContextManager().operationStackPush(result);

                FieldSelectOperation operator = new FieldSelectOperation(getCodeGenBackEnd(), expr.getLeftOperand());
                operator.write();
            }

            if (keepValueInOperationStack) {
                // push result on operation stack
                getCodeGenBackEnd().getContextManager().operationStackPush(result);
            }
            else {
                // destroy register
                result.destroy();
            }
        }
    }

    /**
     * method called to generate code for assignation print
     * this method is pretty useless
     */
    @Override
    public void print() {
        // do assign
        doOperation(true);

        // get result
        VirtualRegister result = getCodeGenBackEnd().getContextManager().operationStackPop();
        getCodeGenBackEnd().addInstruction(new LOAD(result.requestPhysicalRegister(), GPRegister.getR(1)));

        // cast expression
        Assign expr = (Assign) this.getExpression();

        // print result according to type
        if (expr.getLeftOperand().getType().isFloat()) {
            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().addInstruction(new WFLOATX());
            }
            else {
                getCodeGenBackEnd().addInstruction(new WFLOAT());
            }
        }
        else if (expr.getLeftOperand().getType().isInt()) {
            getCodeGenBackEnd().addInstruction(new WINT());
        }

        // destroy result
        result.destroy();
    }
}
