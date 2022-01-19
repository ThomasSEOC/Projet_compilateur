package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BinaryBoolOperation;
import fr.ensimag.deca.codegen.ReadIntOperation;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 *
 * @author gl54
 * @date 01/01/2022
 */
public class ReadInt extends AbstractReadExpr {

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        setType(new IntType(compiler.getSymbolTable().create("readInt")));
        Type type = getType();
        if (type.isInt()) {
            return type;
        }
        throw new ContextualError("Must be an int", getLocation());
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readInt()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        ReadIntOperation operator = new ReadIntOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        ReadIntOperation operator = new ReadIntOperation(compiler.getCodeGenBackend(), this);
        operator.print();
    }
}
