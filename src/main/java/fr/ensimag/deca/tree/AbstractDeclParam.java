package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;

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
