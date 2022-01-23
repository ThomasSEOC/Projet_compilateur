package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
/**
 * Variable declaration
 *
 * @author gl54
 * @date 11/01/2022
 */

public class DeclParam extends AbstractDeclParam {
    private final AbstractIdentifier type;
    private final AbstractIdentifier name;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier name) {
        this.name = name;
        this.type = type;

    }

    @Override
    public Type verifyDeclParam(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Signature signature) throws ContextualError {

        // check type
        Type currentType = type.verifyType(compiler);
        if (type.getType().isVoid()) {
            throw new ContextualError("parameter must not be void", getLocation());
        }

        // check if the name is a Predefined type or a class
        EnvironmentType envTypes = compiler.getTypes();
        SymbolTable.Symbol realSymbol = name.getName();
        TypeDefinition typeDef = envTypes.get(realSymbol);
        if (typeDef != null) {
            if (typeDef.isClass()) {
                throw new ContextualError(realSymbol + " is a class name defined at " +
                        envTypes.getDico().get(realSymbol).getLocation() + ", can't be a parameter name", getLocation());
            } else {
                throw new ContextualError(realSymbol + " is a predefined type, can't be a parameter name", getLocation());
            }
        }

        // put the parameter name in the local environment
        try {
            localEnv.declare(name.getName(), new ParamDefinition(currentType,this.getLocation()));
            name.setType(currentType);
            if ((new ParamDefinition(currentType,this.getLocation())).isExpression()) {
                throw new ContextualError("Parameter name must not be an expression", getLocation());
            }
        } catch (DoubleDefException e) {
            if(localEnv.get(realSymbol).isParam()) {
                throw new ContextualError(realSymbol + " is a parameter already defined at " +
                        localEnv.get(realSymbol).getLocation(), getLocation());
            } else {
                localEnv.declareforce(name.getName(), new ParamDefinition(currentType,this.getLocation()));
            }
        }
        return(currentType);

}

    @Override
    public void decompile(IndentPrintStream s){
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, true);
    }
}
