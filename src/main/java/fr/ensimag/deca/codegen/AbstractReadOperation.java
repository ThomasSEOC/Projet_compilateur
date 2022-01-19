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
     * @param codegenbackend global codegen backend
     * @param expression expression related to the current operation
     */
    public AbstractReadOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super (codegenbackend, expression);
    }
}
