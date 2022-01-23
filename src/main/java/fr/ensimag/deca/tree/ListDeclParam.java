package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

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

    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
                                ClassDefinition currentClass) throws ContextualError {
	throw new UnsupportedOperationException("not yet implemented");
    
    }


}
