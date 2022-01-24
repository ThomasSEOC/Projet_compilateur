package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
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
        int length_list = getList().size()- 1 ;
        for( AbstractDeclParam param : getList()){
            param.decompile(s);

            // On affiche (", ") que si le paramètre courant n'est pas le dernier de la liste
            int range = getList().indexOf(param);
            if (! (range == length_list)) {
                s.print(", ");
            }
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
