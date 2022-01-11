package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.*;

/**
 * Class making binary arithmetical operations
 *
 * @author gl54
 * @date 10/01/2022
 */
public class BinaryArithmOperation {
    //instanceof

    private CodeGenBackend codegenbackend;

    /**
     * Constructor of BinaryArithmOperation
     *
     * @param codegenbackend
     */
    public BinaryArithmOperation (CodeGenBackend codegenbackend){
        this.codegenbackend = codegenbackend;
    }


    /**
     * Main class doing the operation
     *
     * @param expression
     */
    public void doOperation (AbstractExpr expression){
        // Il y aura ici forcément 2 expressions (binaires)
        // Il faut demander deux registres:
//        Register r1 = new Register();
//        Register r2 = new Register();
//
//        if (expression instanceof Plus){
//            // codegenbackend.getCompiler().addInstruction(new ADD(r1,r2))
//        }
//
//        if (expression instanceof Minus){
//            // codegenbackend.getCompiler().addInstruction();
//        }
//
//        if (expression instanceof Multiply){
//
//        }
//
//        if (expression instanceof Divide){
//
//        }

    }



}
