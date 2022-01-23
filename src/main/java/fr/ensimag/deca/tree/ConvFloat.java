package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.ConversionOperation;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.opti.InstructionIdentifiers;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl54
 * @date 01/01/2022
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        Type floatType = new FloatType(compiler.getSymbolTable().create("float"));
        setType(floatType);
        return floatType;
    }

    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        ConversionOperation operator = new ConversionOperation(compiler.getCodeGenBackend(), this);
        operator.print();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        ConversionOperation operator = new ConversionOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getOperand().decompile(s);
    }
}
