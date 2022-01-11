package fr.ensimag.deca.codegen;

import com.sun.org.apache.xpath.internal.operations.Mult;
import com.sun.tools.javac.jvm.Code;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.SUB;

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

        VirtualRegister r1 = this.getCodeGenBackEnd().getContextManager().requestNewRegister();
        VirtualRegister r2 = this.getCodeGenBackEnd().getContextManager().requestNewRegister();

        if (this.getExpression() instanceof Plus){
            getCodeGenBackEnd().getCompiler().addInstruction(new ADD(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Plus"));
        }

        if (this.getExpression() instanceof Minus){
            getCodeGenBackEnd().getCompiler().addInstruction(new SUB(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Minus"));
        }

        if (this.getExpression() instanceof Multiply){
            getCodeGenBackEnd().getCompiler().addInstruction(new MUL(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Multiply"));
        }

        if (this.getExpression() instanceof Divide){
            getCodeGenBackEnd().getCompiler().addInstruction(new DIV(r1.getDVal(), r2.requestPhysicalRegister()), String.format("Operation Divide"));
        }
        // Quel registre conserver? Lequel libérer?

    }



}
