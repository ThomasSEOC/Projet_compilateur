package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.While;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

public class whileStatement {
    CodeGenBackend backend;
    private While expression;

    public whileStatement(CodeGenBackend backend, AbstractInst expression) {
        this.backend = backend;
        this.expression = (While) expression;
    }

    public void createStatement() {
        backend.incWhileStatementCount();

        Label startLabel = new Label("while_" + backend.getWhileStatementsCount() + "_start");
        Label bodyLabel = new Label("while_" + backend.getWhileStatementsCount() + "_body");
        Label endLabel = new Label("while_" + backend.getWhileStatementsCount() + "_end");

        backend.trueBooleanLabelPush(bodyLabel);
        backend.falseBooleanLabelPush(endLabel);

        backend.getCompiler().addLabel(startLabel);

        // appels récursifs
        BinaryBoolOperation operator = new BinaryBoolOperation(backend, expression.getCondition());
        operator.doOperation();

        backend.getCompiler().addLabel(bodyLabel);
        expression.getBody().codeGenListInst(backend.getCompiler());

        backend.getCompiler().addInstruction(new BRA(startLabel));

        backend.getCompiler().addLabel(endLabel);

        backend.popCurrentTrueBooleanLabel();
        backend.popCurrentFalseBooleanLabel();
    }
}
