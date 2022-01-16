package fr.ensimag.deca.codegen;


import fr.ensimag.deca.tree.AbstractExpr;

/**
 * Public abstract class ReadOperation
 *
 * @author gl54
 * @date 11/01/2022
 */

public abstract class AbstractReadOperation extends AbstractOperation {

    /**
     * Constructor of class AbstractReadExpression
     *
     * @param codegenbackend
     * @param expression
     */
    public AbstractReadOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super (codegenbackend, expression);
    }



}
