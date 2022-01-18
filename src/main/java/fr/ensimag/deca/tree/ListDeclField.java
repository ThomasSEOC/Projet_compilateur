package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.AbstractClassObject;
import fr.ensimag.deca.codegen.ClassManager;
import fr.ensimag.deca.codegen.ClassObject;
import fr.ensimag.deca.codegen.Fields;
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

    public void codeGenDecl(ClassManager classManager, AbstractClassObject object, int offset) {
        if (object instanceof ClassObject) {
            Fields fields = new Fields((ClassObject) object);
            fields.codeGenDecl(offset);
        }
    }
}
