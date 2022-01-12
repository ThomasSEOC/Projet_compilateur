package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class DeclMethod extends AbstractDeclMethod{

    private AbstractIdentifier type;
    private AbstractIdentifier name;
    private ListDeclParam listDeclParam;
    private MethodBody methodBody;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam listDeclParam, MethodBody methodBody){
        Validate.notNull(type);
        Validate.notNull(name);
        this.type = type;
        this.name = name;
        this.listDeclParam = listDeclParam;
        this.methodBody = methodBody;
    }

    @Override
    public void decompile(IndentPrintStream s){
        //A ordonne
        type.decompile(s);
        name.decompile(s);
        listDeclParam.decompile(s);
        methodBody.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,false);
        listDeclParam.prettyPrint(s,prefix,false);
        methodBody.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f){
        type.iter(f);
        name.iter(f);
        listDeclParam.iter(f);
        methodBody.iter(f);
    }

    @Override
    protected void verifyDeclMethod(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{}



}
