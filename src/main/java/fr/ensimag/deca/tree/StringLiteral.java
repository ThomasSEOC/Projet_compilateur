package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.LiteralOperation;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * String literal
 *
 * @author gl54
 * @date 01/01/2022
 */
public class StringLiteral extends AbstractStringLiteral {

    @Override
    public String getValue() {
        return value;
    }

    private String value;

    public StringLiteral(String value) {
        Validate.notNull(value);
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type stringType = new StringType(compiler.getSymbolTable().create("string"));
        this.setType(stringType);
        return stringType;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
//        compiler.addInstruction(new WSTR(new ImmediateString(value)));
        LiteralOperation operator = new LiteralOperation(compiler.getCodeGenBackend(), this);
        operator.print();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        LiteralOperation operator = new LiteralOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getValue());
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
    String prettyPrintNode() {
        return "StringLiteral (" + value + ")";
    }

}
