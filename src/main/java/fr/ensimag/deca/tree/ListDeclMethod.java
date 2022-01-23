package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 *
 * @author gl54
 * @date 11/01/2022
 */
public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    public void verifyListDeclMethod(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Iterator<AbstractDeclMethod> it = this.iterator();
        while (it.hasNext()) {
            it.next().verifyDeclMethod(compiler, localEnv, currentClass);
        }
    }

    public void verifyListDeclMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Iterator<AbstractDeclMethod> it = this.iterator();
        while (it.hasNext()) {
            it.next().verifyDeclMethodBody(compiler, localEnv, currentClass );
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod c : getList()) {
            c.decompile(s);
            s.println();
        }
    }
}
