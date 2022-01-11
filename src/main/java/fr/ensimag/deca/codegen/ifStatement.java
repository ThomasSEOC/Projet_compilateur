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
        expression = (IfThenElse) expression;
    }

    public void createStatement() {
        backend.incIfStatementCount();
        String thenLabel = "if_" + backend.getIfStatementsCount() + "_then";
        String elseLabel = "if_" + backend.getIfStatementsCount() + "_else";
        String endLabel = "if_" + backend.getIfStatementsCount() + "_end";

        // appels récursifs
        BinaryBoolOperation operator = new BinaryBoolOperation(backend, expression.getCondition());
        operator.doOperation();

        backend.getCompiler().addLabel(new Label(thenLabel));
        expression.getThenBranch().codeGenListInst(backend.getCompiler());

        backend.getCompiler().addInstruction(new BRA(new Label(endLabel)), "jump to end of if statement");

        backend.getCompiler().addLabel(new Label(elseLabel));
        expression.getElseBranch().codeGenListInst(backend.getCompiler());
    }
}
