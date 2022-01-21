package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Variable declaration
 *
 * @author gl54
 * @date 11/01/2022
 */

public abstract class AbstractDeclParam extends Tree{

    protected abstract Type verifyDeclParam(DecacCompiler compiler, EnvironmentExp localEnv,
                                            ClassDefinition classDefinition, Signature signature)
        throws ContextualError;

}
