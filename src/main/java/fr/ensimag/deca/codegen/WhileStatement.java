package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 * class responsible for while statement code generation
 */
public class WhileStatement {
    CodeGenBackend backend;
    private final While expression;

    /**
     * constructor for WhileStatement
     * @param backend global code generation backend
     * @param expression expression related to statement
     */
    public WhileStatement(CodeGenBackend backend, AbstractInst expression) {
        this.backend = backend;
        this.expression = (While) expression;
    }

    /**
     * method called to generate code for while statement
     */
    public void createStatement() {
        // increment while statement count
        backend.incWhileStatementCount();

        // create labels for start, body, end
        Label startLabel = new Label("while_" + backend.getWhileStatementsCount() + "_start");
        Label bodyLabel = new Label("while_" + backend.getWhileStatementsCount() + "_body");
        Label endLabel = new Label("while_" + backend.getWhileStatementsCount() + "_end");

        // push labels for conditions branch
        backend.trueBooleanLabelPush(bodyLabel);
        backend.falseBooleanLabelPush(endLabel);

        // add start label
        backend.addLabel(startLabel);

        // generate code for condition
        if (expression.getCondition() instanceof Not) {
            NotOperation operator = new NotOperation(backend, expression.getCondition());
            operator.doOperation();
        }
        else if (expression.getCondition() instanceof Identifier) {
            IdentifierRead operator = new IdentifierRead(backend, expression.getCondition());
            operator.doOperation();
        }
        else if (expression.getCondition() instanceof BooleanLiteral) {
            LiteralOperation operator = new LiteralOperation(backend, expression.getCondition());
            operator.doOperation();
        }
        else {
            BinaryBoolOperation operator = new BinaryBoolOperation(backend, expression.getCondition());
            operator.doOperation();
        }

        // add body label
        backend.addLabel(bodyLabel);
        expression.getBody().codeGenListInst(backend.getCompiler());

        // add unconditional jump to start label
        backend.addInstruction(new BRA(startLabel));

        // add end label
        backend.addLabel(endLabel);

        // pop labels
        backend.popCurrentTrueBooleanLabel();
        backend.popCurrentFalseBooleanLabel();
    }
}
