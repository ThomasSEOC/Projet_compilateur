package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Class making binary arithmetical operations
 *
 * @author gl54
 * @date 10/01/2022
 */
public class BinaryArithmOperation extends AbstractBinaryOperation {
    //instanceof


    /**
     * Constructor of BinaryArithmOperation
     *
     * @param codegenbackend, expression
     */
    public BinaryArithmOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super(codegenbackend, expression);
    }


    /**
     * Main class doing the operation
     *
     * @param
     */
    @Override
    public void doOperation (){

//        VirtualRegister r1 = this.getCodeGenBackEnd().getContextManager().requestNewRegister();
//        VirtualRegister r2 = this.getCodeGenBackEnd().getContextManager().requestNewRegister();

        // récursion
        AbstractBinaryExpr expr = (AbstractBinaryExpr) this.getExpression();
        AbstractExpr[] ops = {expr.getLeftOperand(), expr.getRightOperand()};
        this.ListCodeGen(ops);
        VirtualRegister r2 = getCodeGenBackEnd().getContextManager().operationStackPop();
        VirtualRegister r1 = getCodeGenBackEnd().getContextManager().operationStackPop();

        if (this.getExpression() instanceof Plus){
            getCodeGenBackEnd().getCompiler().addInstruction(new ADD(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Plus"));
        }

        if (this.getExpression() instanceof Minus){
            // invert register
            VirtualRegister temp = r1;
            r1 = r2;
            r2 = temp;
            getCodeGenBackEnd().getCompiler().addInstruction(new SUB(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Minus"));
        }

        if (this.getExpression() instanceof Multiply){
            getCodeGenBackEnd().getCompiler().addInstruction(new MUL(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Multiply"));
        }

        if (this.getExpression() instanceof Divide){
            VirtualRegister temp = r1;
            r1 = r2;
            r2 = temp;
            getCodeGenBackEnd().getCompiler().addInstruction(new QUO(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Quotient"));
        }

        if (this.getExpression() instanceof Modulo){
            VirtualRegister temp = r1;
            r1 = r2;
            r2 = temp;
            getCodeGenBackEnd().getCompiler().addInstruction(new REM(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Remainder"));
        }

        r1.destroy();
        this.getCodeGenBackEnd().getContextManager().operationStackPush(r2);
    }

    @Override
    public void print() {
        //doOperation();

        VirtualRegister r = getCodeGenBackEnd().getContextManager().operationStackPop();

        getCodeGenBackEnd().getCompiler().addInstruction(new LOAD(r.getDVal(), GPRegister.getR(1)));

        if (r.getIsFloat()) {
            if (getCodeGenBackEnd().getPrintHex()) {
                getCodeGenBackEnd().getCompiler().addInstruction(new WFLOAT());
            }
            else {
                getCodeGenBackEnd().getCompiler().addInstruction(new WFLOATX());
            }
        }
        else {
            getCodeGenBackEnd().getCompiler().addInstruction(new WINT());
        }

        r.destroy();
    }

}
