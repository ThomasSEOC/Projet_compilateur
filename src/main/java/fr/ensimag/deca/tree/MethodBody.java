package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class MethodBody extends AbstractMethodBody{

    private ListDeclVar listDeclVar;
    private ListInst listInst;

    public MethodBody(ListDeclVar listDeclVar, ListInst listInst){
        Validate.notNull(listDeclVar);
        Validate.notNull(listInst);
        this.listDeclVar = listDeclVar;
        this.listInst = listInst;
    }

    @Override
    public void decompile(IndentPrintStream s){
        listDeclVar.decompile(s);
        listInst.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        listDeclVar.prettyPrint(s,prefix,false);
        listInst.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f){
        listInst.iter(f);
        listDeclVar.iter(f);
    }
}
