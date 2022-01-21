package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.NewOperator;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class New extends AbstractExpr{

    private AbstractIdentifier type;

    public New(AbstractIdentifier type){
        Validate.notNull(type);
        this.type = type ;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Type newType = type.verifyType(compiler);
        //verifyType checks if type is a type

        if (newType.isClass()) {
            setType(newType);
            return newType;
        }
        throw new ContextualError(type.getName() + " is not a class", getLocation());

    }

    public AbstractIdentifier getClassType() {
        return type;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        type.decompile(s);
        s.print("()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // l.170 dans Tree.java explique les paramètres de la fonction
        type.prettyPrint(s,prefix,true);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        NewOperator operator = new NewOperator(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }
}
