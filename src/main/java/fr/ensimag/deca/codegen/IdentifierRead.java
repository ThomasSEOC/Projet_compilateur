package fr.ensimag.deca.codegen;

import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.StringType;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;

public class IdentifierRead extends AbstractOperation {

    public IdentifierRead(CodeGenBackend backend, AbstractExpr expression) {
        super(backend, expression);
    }

    @Override
    public void doOperation() {
        Identifier expr = (Identifier) getExpression();
        VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister();
        int offset = getCodeGenBackEnd().getVariableOffset(expr.getName().getName());
        getCodeGenBackEnd().getCompiler().addInstruction(new LOAD(new RegisterOffset(offset, Register.GB), r.requestPhysicalRegister()));
        getCodeGenBackEnd().getContextManager().operationStackPush(r);
    }

    @Override
    public void print() {
        Identifier expr = (Identifier) getExpression();
        if (expr.getType() instanceof IntType) {
            int offset = getCodeGenBackEnd().getVariableOffset(expr.getName().getName());
            getCodeGenBackEnd().getCompiler().addInstruction(new LOAD(new RegisterOffset(offset, Register.GB), GPRegister.getR(1)));
            getCodeGenBackEnd().getCompiler().addInstruction(new WINT());
        }
        else
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }
}
