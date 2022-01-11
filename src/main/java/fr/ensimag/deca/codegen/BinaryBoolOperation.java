package fr.ensimag.deca.codegen;


import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import jdk.vm.ci.code.Register;

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
     * @param codegenbackend, expression
     */

    public BinaryBoolOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super(codegenbackend, expression);
    }

    /**
     * Main class making binary boolean operations
     * @param
     */
    @Override
    public void doOperation (){
        VirtualRegister r1 = this.getCodeGenBackEnd().getContextManager().requestNewRegister();
        VirtualRegister r2 = this.getCodeGenBackEnd().getContextManager().requestNewRegister();

        //On va devoir faire les calculs avec r1 et r2
        //A faire pr plus tard
        if (this.getExpression() instanceof And){

        }
        if (this.getExpression() instanceof Greater){

        }
        if (this.getExpression() instanceof GreaterOrEqual){

        }
        if (this.getExpression() instanceof Lower){

        }
        if (this.getExpression() instanceof LowerOrEqual){

        }
        if (this.getExpression() instanceof Equals){

        }
        if (this.getExpression() instanceof NotEquals){

        }
        if (this.getExpression() instanceof Not){

        }
    }


}
