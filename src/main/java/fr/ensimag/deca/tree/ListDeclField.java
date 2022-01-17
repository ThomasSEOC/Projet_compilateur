package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ClassManager;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

/**
 *
 * @author gl54
 * @date 11/01/2022
 */
public class ListDeclField extends TreeList<AbstractDeclField> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    public void codeGen(ClassManager classManager, AbstractIdentifier nameClass, int offset) {
        String name = nameClass.getName().getName();

        throw new UnsupportedOperationException("not yet implemented");
    }
}
