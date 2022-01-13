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
        for( AbstractDeclParam param : getList()){
            param.decompile(s);
        }
    }

    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
                                ClassDefinition currentClass) throws ContextualError {
    }


}
