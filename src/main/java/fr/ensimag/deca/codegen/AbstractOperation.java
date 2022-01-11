package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;

/**
 * Once the tree done, this Class allows to choose
 * which class will make calculations
 *
 */
public abstract class AbstractOperation {

    private CodeGenBackend codegenbackend;
    private AbstractExpr expression;

    /**
     * Constructor of class AbstractOperation
     * @param codegenbackend
     * @param expression
     */
    public AbstractOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        this.codegenbackend = codegenbackend;
        this.expression = expression;
    }

    public abstract void doOperation();

    /**
     * getter of expression
     * @return expression
     */
    public AbstractExpr getExpression(){
        return this.expression;
    }

    /**
     * Getter for codegenbackend
     * @return codegenbackend
     */
    public CodeGenBackend getCodeGenBackEnd(){
        return this.codegenbackend;
    }

}
