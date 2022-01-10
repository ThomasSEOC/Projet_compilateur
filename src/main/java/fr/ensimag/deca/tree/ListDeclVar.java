package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl54
 * @date 01/01/2022
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the "env_exp_r" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */    
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    }

    public void codeGenListDeclVar(DecacCompiler compiler) {
        for (AbstractDeclVar i : getList()) {
            DeclVar var = (DeclVar) i;

            // set address operand
            var.getType().getVariableDefinition().setOperand(new RegisterOffset(compiler.getCodeGenBackend().getMaxGlobalVAriablesSize(), Register.GB));

            // inc global variables size
            compiler.getCodeGenBackend().incMaxGlobalVAriablesSize();

            // init variable
            if (var.getInitialization() instanceof Initialization) {
                Initialization init = (Initialization) var.getInitialization();
                init.getExpression().codeGenInst(compiler);
            }
        }
    }
}
