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
    EnvironmentExp methodMembers;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam params, AbstractMethodBody body){
        Validate.notNull(type);
        Validate.notNull(name);
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;

    }
    public EnvironmentExp getMethodMemberMembers() {
        return methodMembers;
    }

    protected void verifyDeclMethod(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {

        methodMembers = new EnvironmentExp(localEnv);
        //methodMembers.initDico(currentClass.getMembers().getDico());
        SymbolTable.Symbol realSymbol = name.getName();
        TypeDefinition typeDef =  compiler.getTypes().get(realSymbol);
        ExpDefinition currentDef = currentClass.getMembers().get(realSymbol);
        ClassDefinition superClass = currentClass.getSuperClass();
        MethodDefinition currentMethodDef = name.getMethodDefinition();


        // Verify if the type exists and set it
        Type currentType = type.verifyType(compiler);

        // Verify the signature
        signature = params.verifyListDeclParam(compiler, methodMembers, currentClass);

        // Check if the name is a Predefined type or a class
        if (typeDef != null){
            if (typeDef.isClass()){
                throw new ContextualError(realSymbol + " is a class name defined at "+
                        typeDef.getLocation()+ ", can't be a method name", getLocation());
            }
            else {
                throw new ContextualError(realSymbol + " is a predefined type, can't be a method name", getLocation());
            }
        }

        // Check if we need to override the method that already exists in super class
        if (superClass != null){
            ExpDefinition superDef = (ExpDefinition) superClass.getMembers().get(realSymbol);
            // If the name is already in the envExp of the super class
            if (superDef != null){
                // Check if the name is a field name
                if (superDef.isField()) {
                    throw new ContextualError(realSymbol + " is a field name in super class defined at " +
                            superDef.getLocation() + ", can't be a method name", getLocation());
                }
                // It is a method name
                else if (superDef.isMethod()) {
                    MethodDefinition superMethod = (MethodDefinition) superDef;
                    MethodDefinition currentMethod = (MethodDefinition) currentDef;
                    Type superType = superDef.getType();
                    if (!superMethod.getSignature().equals(currentMethod.getSignature())) {
                        throw new ContextualError(realSymbol + "  is already defined at " +
                                superDef.getLocation() + ", the signature is different", getLocation());
                    } else if (!(currentType.getName().getName().equals(superType.getName().getName()))){
                        throw new ContextualError("override of " + realSymbol +
                                " is not possible ; the return type of the original method defined at " +
                                superDef.getLocation() + "is different", getLocation());
                    }
                    currentDef = new MethodDefinition(superType, getLocation(), currentMethod.getSignature(), superMethod.getIndex());
                }
            }
            else {
                currentDef = new MethodDefinition(currentType, getLocation(), signature, currentClass.incNumberOfMethods());
            }
        }
        // Put the method in the localEnv
        try {
            localEnv.declare(realSymbol, currentDef);
            name.setType(currentType);
            name.setDefinition(currentDef);
        } catch (DoubleDefException e) {
            if (localEnv.get(realSymbol).isField()){
                throw new ContextualError(realSymbol + " is a field already defined at " +
                        currentDef.getLocation() + "can't be a method name", getLocation());
            }
            throw new ContextualError(realSymbol + " is a method already defined at " +
                    currentDef.getLocation(), getLocation());
        }


    }

    protected void verifyDeclMethodBody(DecacCompiler compiler,
                                        EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Type currentType = type.verifyType(compiler);
        body.verifyMethodBody(compiler, methodMembers, currentClass, currentType);
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


    public AbstractIdentifier getType() { return type; }

    public AbstractIdentifier getName() { return name; }

    public ListDeclParam getParams() { return params; }

    public AbstractMethodBody getBody() { return body; }
}
