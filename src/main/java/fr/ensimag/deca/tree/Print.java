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
        s.print("print(");
        for (AbstractExpr a : getArguments().getList()) {
            a.decompile(s);
        }
        s.println(");");
    }

    @Override
    String getSuffix() {
        return "";
    }
}
