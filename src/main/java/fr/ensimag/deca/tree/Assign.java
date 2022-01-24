package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.opti.InstructionIdentifiers;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl54
 * @date 01/01/2022
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {

        // Verify if the two operands have the same Type
        if (currentClass != null){
            AbstractLValue lOp = this.getLeftOperand();
            Type typeLOp = lOp.verifyExpr(compiler, currentClass.getMembers(), currentClass);
            getRightOperand().verifyRValue(compiler, localEnv, currentClass, typeLOp);
            setType(typeLOp);
            return typeLOp;
        }
        else {
            AbstractLValue lOp = this.getLeftOperand();
            Type typeLOp = lOp.verifyExpr(compiler, localEnv, currentClass);
            getRightOperand().verifyRValue(compiler, localEnv, currentClass, typeLOp);
            setType(typeLOp);
            return typeLOp;
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        AssignOperation operator = new AssignOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    public void searchIdentifiers(InstructionIdentifiers instructionIdentifiers) {
        if (getLeftOperand() instanceof Identifier) {
            instructionIdentifiers.setWriteIdentifier((Identifier) getLeftOperand());
        }

        getRightOperand().searchIdentifiers(instructionIdentifiers);
    }
}
