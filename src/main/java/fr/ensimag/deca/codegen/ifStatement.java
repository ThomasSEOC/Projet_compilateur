package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;

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
            VirtualRegister result = backend.getContextManager().operationStackPop();
            backend.addInstruction(new CMP(new ImmediateInteger(0), result.requestPhysicalRegister()));
            backend.addInstruction(new BEQ(elseLabel));
            result.destroy();
        }
        else if (expression.getCondition() instanceof BooleanLiteral) {
            LiteralOperation operator = new LiteralOperation(backend, expression.getCondition());
            operator.doOperation();
            VirtualRegister result = backend.getContextManager().operationStackPop();
            backend.addInstruction(new CMP(new ImmediateInteger(0), result.requestPhysicalRegister()));
            backend.addInstruction(new BEQ(elseLabel));
            result.destroy();
        }
        else if (expression.getCondition() instanceof InstanceOf) {
            InstanceofOperation operator = new InstanceofOperation(backend, expression.getCondition());
            operator.doOperation();

            // result is in R0
            backend.addInstruction(new CMP(new ImmediateInteger(0), GPRegister.getR(0)));
            backend.addInstruction(new BEQ(elseLabel));
        }
        else if (expression.getCondition() instanceof Assign) {
            AssignOperation operator = new AssignOperation(backend, expression.getCondition());
            operator.doOperation(true);

            VirtualRegister result = backend.getContextManager().operationStackPop();
            backend.addInstruction(new CMP(new ImmediateInteger(0), result.requestPhysicalRegister()));
            backend.addInstruction(new BEQ(elseLabel));
            result.destroy();
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
