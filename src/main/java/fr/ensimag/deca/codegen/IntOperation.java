package fr.ensimag.deca.codegen;


import fr.ensimag.deca.tree.AbstractExpr;

/**
 * Class using int types
 *
 * @author gl54
 * @date 11/01/22
 */
public class IntOperation extends AbstractReadOperation{

    /**
     * Constructor of Class IntOperation
     *
     * @param codegenbackend
     * @param expression
     */
    public IntOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super(codegenbackend, expression);
    }

    /**
     * Override of doOperation Method for ReadInt
     *
     * @return int
     */
    @Override
    public void doOperation(){
        //il faut ici charger un int
    }


}
