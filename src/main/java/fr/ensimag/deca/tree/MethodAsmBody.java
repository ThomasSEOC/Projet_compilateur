package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class MethodAsmBody extends AbstractMethodBody{

    private StringLiteral stringLiteral;

    public MethodAsmBody(StringLiteral stringLiteral){
        Validate.notNull(stringLiteral);
        this.stringLiteral = stringLiteral;
    }

    @Override
    public void decompile(IndentPrintStream s){
        stringLiteral.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        stringLiteral.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f){
        stringLiteral.iter(f);
    }
}
