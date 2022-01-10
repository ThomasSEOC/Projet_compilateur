package fr.ensimag.deca.codegen;


import com.sun.tools.javac.jvm.Code;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import jdk.vm.ci.code.Register;

/**
 * Class making Binary Boolean Operations
 *
 * @author gl54
 * @date 10/01/2022
 */

public class BinaryBoolOperation {

    private CodeGenBackend codegenbackend;

    /**
     * Constructor pf class BinaryBoolOperation
     *
     * @param codegenbackend
     */

    public BinaryBoolOperation (CodeGenBackend codegenbackend){
        this.codegenbackend = codegenbackend;
    }

    /**
     * Main class making binary boolean operations
     * @param expression
     */
    public void doOperation (AbstractExpr expression){

        Register r1 = new Register();
        Register r2 = new Register();

        if (expression instanceof And){

        }
        if (expression instanceof Greater){

        }
        if (expression instanceof GreaterOrEqual){

        }
        if (expression instanceof Lower){

        }
        if (expression instanceof LowerOrEqual){

        }
        if (expression instanceof Equals){

        }
        if (expression instanceof NotEquals){

        }


    }


}
