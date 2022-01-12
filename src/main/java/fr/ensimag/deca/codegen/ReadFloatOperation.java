package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ReadFloat;
import fr.ensimag.deca.tree.UnaryMinus;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;

/**
 * Class using float types
 *
 * @author gl54
 * @date 11/01/22
 */
public class ReadFloatOperation extends AbstractReadOperation {


    /**
     * Constructor of Class FloatOperation
     *
     * @param codegenbackend
     * @param expression
     */
    public ReadFloatOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super (codegenbackend, expression);
    }

    @Override
    public void doOperation(){

        //Rien en entrée:
        getCodeGenBackEnd().getCompiler().addInstruction(new RFLOAT());

    }


}
