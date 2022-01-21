package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.New;

public class NewOperator extends AbstractOperation {

    public NewOperator(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        New expr = (New) getExpression();
        AbstractClassObject object = getCodeGenBackEnd().getClassManager().getClassObject(expr.getClassType());

        object.createObjectCodeGen();

        // call constructor
    }

    @Override
    public void print() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
