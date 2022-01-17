package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * @author gl54
 * @date 01/01/2022
 */
public class Print extends AbstractPrint {
    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printx)
     */
    public Print(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (getPrintHex()) {
            s.print("printx(");
        }
        else {
            s.print("print(");
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
        return "";
    }
}
