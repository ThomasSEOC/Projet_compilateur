package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class MethodAsmBody extends AbstractMethodBody{

    final private StringLiteral code;

    public MethodAsmBody(StringLiteral code){
        Validate.notNull(code);
        this.code = code;
    }

    @Override
    public void decompile(IndentPrintStream s){
        s.print("asm(");
        code.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){

        code.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f){

        code.iter(f);
    }
}
