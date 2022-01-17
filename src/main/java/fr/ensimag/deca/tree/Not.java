package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BinaryBoolOperation;
import fr.ensimag.deca.codegen.NotOperation;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 *
 * @author gl54
 * @date 01/01/2022
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        AbstractExpr op = getOperand();
        op.verifyExpr(compiler, localEnv, currentClass);
        Type typeOperand = op.getType();
        if (typeOperand.isBoolean()) {
            setType(typeOperand);
            return typeOperand;
        }
        throw new ContextualError("Not operand needs to be a boolean",getLocation());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        NotOperation operator = new NotOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    protected String getOperatorName() {
        return "!";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("!(");
        getOperand().decompile(s);
        s.print(")");
    }
}
