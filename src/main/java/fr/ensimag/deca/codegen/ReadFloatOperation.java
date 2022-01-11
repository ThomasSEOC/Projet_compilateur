package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;

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

    }

}
