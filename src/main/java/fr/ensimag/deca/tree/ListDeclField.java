package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.util.Iterator;

/**
 *
 * @author gl54
 * @date 11/01/2022
 */
public class ListDeclField extends TreeList<AbstractDeclField> {

    public void verifyListDeclField(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Iterator<AbstractDeclField> it = this.iterator();
        while (it.hasNext()) {
            it.next().verifyDeclField(compiler, localEnv, currentClass);
            currentClass.incNumberOfFields();
        }
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

//    public void codeGenDecl(ClassManager classManager, AbstractClassObject object, int offset) {
//        if (object instanceof ClassObject) {
//            Fields fields = new Fields((ClassObject) object);
//            fields.codeGenDecl(offset);
//        }
//    }
}
