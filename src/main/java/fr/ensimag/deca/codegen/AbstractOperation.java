package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ListInst;

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

    public void ListCodeGen(AbstractExpr[] expressions) {
        ListInst list = new ListInst();
        for (AbstractExpr abstractExpr : expressions) {
            list.add(abstractExpr);
        }
        list.codeGenListInst(codegenbackend.getCompiler());
    }

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
