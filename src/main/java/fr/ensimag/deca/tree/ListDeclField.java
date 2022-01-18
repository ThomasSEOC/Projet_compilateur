package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;
import java.util.Iterator;

/**
 *
 * @author gl54
 * @date 11/01/2022
 */
public class ListDeclField extends TreeList<AbstractDeclField> {

    void verifyListField(DecacCompiler compiler) throws ContextualError {
        Iterator<AbstractDeclField> it = this.iterator();
        while (it.hasNext()) {
            it.next().verifyDeclField(compiler);
        }
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

}
