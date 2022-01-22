package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.util.Iterator;

/**
 *
 * @author gl54
 * @date 11/01/2022
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {

    @Override
    public void decompile(IndentPrintStream s) {
        for( AbstractDeclParam param : getList()){
            param.decompile(s);
        }
    }

    Signature verifyListDeclParam (DecacCompiler compiler, EnvironmentExp localEnv,
                                ClassDefinition currentClass) throws ContextualError {
        Iterator<AbstractDeclParam> it = this.iterator();
        Signature signature = new Signature();
        Type type;
        while (it.hasNext()) {
            AbstractDeclParam param = it.next();
            type = param.verifyDeclParam(compiler, localEnv, currentClass, signature);
            signature.add(type);
        }
        return signature;

    }

}
