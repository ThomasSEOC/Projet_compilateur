package fr.ensimag.deca.codegen;

import com.sun.org.apache.bcel.internal.Const;
import fr.ensimag.deca.opti.Constant;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ListInst;

/**
 * Once the tree done, this Class allows choosing
 * which class will make calculations
 *
 * @author gl54
 * @date 11/01/2022
 */
public abstract class AbstractOperation {

    private final CodeGenBackend codegenbackend;
    private AbstractExpr expression;

    /**
     * Constructor of class AbstractOperation
     * @param codegenbackend global backend to every codegen steps
     * @param expression expression related to the current operation
     */
    public AbstractOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        this.codegenbackend = codegenbackend;
        this.expression = expression;
    }

    /**
     * called by codeGenInst method in AbstractExpr derived class
     */
    public abstract void doOperation();

    /**
     * called by codeGenPrint method in AbstractExpr derived class
     */
    public abstract void print();

    /**
     * Recursion on the branch expressions
     * There are usually 1 or 2 expressions in AbstractExpr[]
     * @param expressions list of AbstractExpr
     */
    public void ListCodeGen(AbstractExpr[] expressions) {
        ListInst list = new ListInst();
        for (AbstractExpr abstractExpr : expressions) {
            list.add(abstractExpr);
        }
        list.codeGenListInst(codegenbackend.getCompiler());
    }

    /**
     * getter of expression
     * @return expression related to current Operation
     */
    public AbstractExpr getExpression(){
        return this.expression;
    }

    /**
     * Setter of Expression in AbstractOperation Class
     * @param expression expression related to current operation
     */
    public void setExpression(AbstractExpr expression) {
        this.expression = expression;
    }

    /**
     * Getter for codegenbackend
     * @return codegenbackend
     */
    public CodeGenBackend getCodeGenBackEnd(){
        return this.codegenbackend;
    }

    public Constant getConstant() {
        return null;
    }
}
