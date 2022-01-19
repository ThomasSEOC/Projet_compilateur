package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 * class responsible for is statement code generation
 */
public class ifStatement {
    CodeGenBackend backend;
    private final IfThenElse expression;

    /**
     * constructor for if statement
     * @param backend global code generation backend
     * @param expression expression related to statement
     */
    public ifStatement(CodeGenBackend backend, AbstractInst expression) {
        this.backend = backend;
        this.expression = (IfThenElse) expression;
    }

    /**
     * method called to generate code for if statement
     */
    public void createStatement() {
        // increment if statements count
        backend.incIfStatementCount();

        // create label for then, else, end if
        Label thenLabel = new Label("if_" + backend.getIfStatementsCount() + "_then");
        Label elseLabel = new Label("if_" + backend.getIfStatementsCount() + "_else");
        Label endLabel = new Label("if_" + backend.getIfStatementsCount() + "_end");

        // push labels true and false conditions branch stack
        backend.trueBooleanLabelPush(thenLabel);
        backend.falseBooleanLabelPush(elseLabel);

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

        // add then label
        backend.addLabel(thenLabel);

        // generate code for then branch
        expression.getThenBranch().codeGenListInst(backend.getCompiler());

        // add unconditioned branch to end if
        backend.addInstruction(new BRA(endLabel), "jump to end of if statement");

        // add else label
        backend.addLabel(elseLabel);

        // generate code for else branch
        expression.getElseBranch().codeGenListInst(backend.getCompiler());

        // add end if label
        backend.addLabel(endLabel);

        // pop labels
        backend.popCurrentTrueBooleanLabel();
        backend.popCurrentFalseBooleanLabel();
    }
}
