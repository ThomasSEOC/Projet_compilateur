package fr.ensimag.deca.codegen;


import fr.ensimag.deca.tree.AbstractBinaryExpr;
import fr.ensimag.deca.tree.AbstractExpr;

/**
 * Abstract class for making Binary operations
 *
 * @author gl54
 * @date 11/01/2022
 */
public abstract class AbstractBinaryOperation extends AbstractOperation {

    /**
     * Constructor of Class AbstractBinaryOperation
     * @param codegenbackend expression
     */
    public AbstractBinaryOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super (codegenbackend, expression);
    }




}
