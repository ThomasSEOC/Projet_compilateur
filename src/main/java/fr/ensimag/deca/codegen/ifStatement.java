package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.IfThenElse;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

public class ifStatement {
    CodeGenBackend backend;
    private IfThenElse expression;

    public ifStatement(CodeGenBackend backend, AbstractInst expression) {
        this.backend = backend;
        this.expression = (IfThenElse) expression;
    }

    public void createStatement() {
        backend.incIfStatementCount();
        Label thenLabel = new Label("if_" + backend.getIfStatementsCount() + "_then");
        Label elseLabel = new Label("if_" + backend.getIfStatementsCount() + "_else");
        Label endLabel = new Label("if_" + backend.getIfStatementsCount() + "_end");

        backend.trueBooleanLabelPush(thenLabel);
        backend.falseBooleanLabelPush(elseLabel);

        // appels récursifs
        BinaryBoolOperation operator = new BinaryBoolOperation(backend, expression.getCondition());
        operator.doOperation();

        backend.getCompiler().addLabel(thenLabel);
        expression.getThenBranch().codeGenListInst(backend.getCompiler());

        backend.getCompiler().addInstruction(new BRA(endLabel), "jump to end of if statement");

        backend.getCompiler().addLabel(elseLabel);
        expression.getElseBranch().codeGenListInst(backend.getCompiler());

        backend.popCurrentTrueBooleanLabel();
        backend.popCurrentFalseBooleanLabel();
    }
}
