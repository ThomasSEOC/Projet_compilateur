package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.WNL;

import java.util.Iterator;
import java.util.List;

/**
 * @author gl54
 * @date 01/01/2022
 */
public class Println extends AbstractPrint {

    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printlnx)
     */
    public Println(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        compiler.getCodeGenBackend().addInstruction(new WNL());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (getPrintHex()) {
            s.print("printlnx(");
        }
        else {
            s.print("println(");
        }
        for (int i = 0; i < getArguments().getList().size(); i++) {
            getArguments().getList().get(i).decompile(s);
            if ((i+1) < getArguments().getList().size()) {
                s.print(", ");
            }
        }
        s.println(");");
    }

    @Override
    String getSuffix() {
        return "ln";
    }
}
