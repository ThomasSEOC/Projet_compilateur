package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class DeclMethod extends AbstractDeclMethod{

    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private ListDeclParam params;
    final private AbstractMethodBody body;
    private Signature signature;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam params, AbstractMethodBody body){
        Validate.notNull(type);
        Validate.notNull(name);
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    protected void verifyDeclMethod(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {

        // Verifies if the type exists
        Type currentType = type.verifyType(compiler);


        // check if the name is a Predefined type or a class
        EnvironmentType envTypes = compiler.getTypes();
        SymbolTable.Symbol realSymbol = name.getName();
        TypeDefinition typeDef =  envTypes.get(realSymbol);
        ExpDefinition currentDef = (ExpDefinition) currentClass.getMembers().get(realSymbol);
        if (typeDef != null){
            if (typeDef.isClass()){
                throw new ContextualError(realSymbol + " is a class name defined at "+
                        envTypes.getDico().get(realSymbol).getLocation()+ ", can't be a method name", getLocation());
            }
            else {
                throw new ContextualError(realSymbol + " is a predefined type, can't be a method name", getLocation());
            }
        }

        // Check if we need to override the method that already exists in super class
        ClassDefinition superClass = currentClass.getSuperClass();
        if (superClass != null){
            ExpDefinition superDef = (ExpDefinition) superClass.getMembers().get(realSymbol);
            // if the name is already in the envExp of the super class
            if (superDef != null){
                // check if the name is a field name
                if (superDef.isField()) {
                    throw new ContextualError(realSymbol + " is a field name defined at " +
                            superClass.getMembers().getDico().get(realSymbol).getLocation() + ", can't be a method name", getLocation());
                }
                // it is a method name
                else if (superDef.isMethod()) {
                    MethodDefinition superMethod = (MethodDefinition) superDef;
                    MethodDefinition currentMethod = (MethodDefinition) currentDef;
                    Type superType = superDef.getType();
                    signature = params.verifyListDeclParam(compiler, localEnv, currentClass);
                    if (!superMethod.getSignature().equals(currentMethod.getSignature())) {
                        throw new ContextualError(realSymbol + "  is already defined at " +
                                superClass.getMembers().getDico().get(realSymbol).getLocation() + ", the signature is different", getLocation());
                    } else if (!(currentType.getName().getName().equals(superType.getName().getName()))){
                        throw new ContextualError("override of " + realSymbol +
                                " is not possible ; the return type of the original method defined at " +
                                superDef.getLocation() + "is different", getLocation());
                    }
                    currentDef = new MethodDefinition(superType, getLocation(), currentMethod.getSignature(), superMethod.getIndex());
                }
            }
            else {
                currentDef = new MethodDefinition(currentType, getLocation(), ((MethodDefinition) currentDef).getSignature(), currentClass.incNumberOfMethods());
            }
        }
        // Put the method in the localEnv
        try {
            localEnv.declare(name.getName(), currentDef);
        } catch (DoubleDefException e) {
            throw new ContextualError(realSymbol + " is already defined at " +
                    currentDef.getLocation(), getLocation());
        }


    }

    protected void verifyDeclMethodBody(DecacCompiler compiler,
                                        EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        EnvironmentType envTypes = compiler.getTypes();
        SymbolTable.Symbol realSymbol = name.getName();
        TypeDefinition typeDef =  envTypes.get(realSymbol);
        Type currentType = type.verifyType(compiler);
        body.verifyMethodBody(compiler, localEnv, currentClass, currentType);
    }


    @Override
    public void decompile(IndentPrintStream s){
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(")");
        body.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,false);
        params.prettyPrint(s,prefix,false);
        body.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f){
        type.iter(f);
        name.iter(f);
        params.iter(f);
        body.iter(f);
    }


}
