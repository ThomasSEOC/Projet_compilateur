package fr.ensimag.deca.tree;

import fr.ensimag.deca.opti.InstructionIdentifiers;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Unary expression.
 *
 * @author gl54
 * @date 01/01/2022
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

    private AbstractExpr operand;
    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }

    public AbstractExpr getOperand() {
        return operand;
    }

    protected abstract String getOperatorName();
  
    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("not yey implemented");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }

    @Override
    public void searchIdentifiers(InstructionIdentifiers instructionIdentifiers) {
        getOperand().searchIdentifiers(instructionIdentifiers);
    }
}
