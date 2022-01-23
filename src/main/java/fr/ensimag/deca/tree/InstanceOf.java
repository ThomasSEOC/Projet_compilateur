package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.InstanceofOperation;
import fr.ensimag.deca.codegen.MethodCallOperation;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class InstanceOf extends AbstractExpr{

    private AbstractExpr e;
    private AbstractIdentifier type;

    public InstanceOf(AbstractExpr e, AbstractIdentifier type){
        Validate.notNull(e);
        Validate.notNull(type);
        this.e = e ;
        this.type = type;
    }

    public AbstractExpr getObjectE() {
        return e;
    }

    public AbstractIdentifier getTargetType() {
        return type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type ClassType =  type.verifyType(compiler);
        e.verifyExpr(compiler,localEnv,currentClass);

        if (!ClassType.isClass()){
            throw new ContextualError(e + "Can't cast to a non class Type", getLocation());
        }

        EnvironmentType envTypes = compiler.getTypes();
        SymbolTable.Symbol booleanSymbol = compiler.getSymbolTable().getSymbol("boolean");
        setType(envTypes.get(booleanSymbol).getType());
        return (envTypes.get(booleanSymbol).getType());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        e.decompile(s);
        s.print(" instanceof ");
        type.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        e.prettyPrint(s,prefix,false);
        type.prettyPrint(s,prefix,true);

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        e.iter(f);
        type.iter(f);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        InstanceofOperation operator = new InstanceofOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
        operator.print();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        InstanceofOperation operator = new InstanceofOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }
}
