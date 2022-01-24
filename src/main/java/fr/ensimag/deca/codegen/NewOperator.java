package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.New;

/**
 * Class responsible for new operation
 *
 * @author gl54
 * @date 21/01/2022
 */
public class NewOperator extends AbstractOperation {

    /**
     * constructor for class NewOperator
     * @param backend global code generation backend
     * @param expression expression related to operation
     */
    public NewOperator(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    /**
     * generate code for New
     */
    @Override
    public void doOperation() {
        // get object to instantiate
        New expr = (New) getExpression();
        AbstractClassObject object = getCodeGenBackEnd().getClassManager().getClassObject(expr.getClassType());

        // instantiate object
        object.createObjectCodeGen();
    }

    /**
     * generate code for New print
     */
    @Override
    public void print() {
        throw new UnsupportedOperationException("operation not permitted");
    }
}
