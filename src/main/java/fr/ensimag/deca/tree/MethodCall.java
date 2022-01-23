package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class MethodCall extends AbstractExpr{

    final private AbstractExpr expr;
    final private AbstractIdentifier ident;
    final private ListExpr listExpr;

    public MethodCall(AbstractExpr expr, AbstractIdentifier ident, ListExpr listExpr){
        Validate.notNull(expr);
        Validate.notNull(ident);
        Validate.notNull(listExpr);
        this.expr = expr;
        this.ident = ident;
        this.listExpr = listExpr;
    }


    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        Type classType = expr.verifyExpr(compiler, localEnv, currentClass);
        // verify if the method is called with a valid class Name
        if (!(classType.isClass())) {
            throw new ContextualError(expr + "is not called with a valid class name", getLocation());
        }


        // verifies if the method exists in the clas
        EnvironmentType envTypes = compiler.getTypes();
        ClassDefinition classDef = (ClassDefinition) envTypes.get(classType.getName());
        SymbolTable.Symbol realSymbol = ident.getName();
        if (!(classDef.getMembers().get(realSymbol).isMethod())) {
            throw new ContextualError(expr + "does not belong to" + classType.getName(), getLocation());
        }

        // verify if the list of parameters correct
        MethodDefinition methodDef = (MethodDefinition) classDef.getMembers().get(realSymbol);
        Signature signature = methodDef.getSignature();
        if (listExpr.size() != signature.size()) {
            throw new ContextualError(expr + "has a wrong list of param, please check at the method defined at " + methodDef.getLocation(), getLocation());
        }
        for (int i = 0; i < listExpr.size(); i++) {
            if (!(listExpr.getList().get(i).verifyExpr(compiler, localEnv, currentClass).sameType(signature.paramNumber(i)))) {
                throw new ContextualError(expr + "has a wrong list of param, please check at the method defined at " + methodDef.getLocation(), getLocation());
            }
        }

        // Set the type
        setType(methodDef.getType());
        return (methodDef.getType());

    }


    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(".");
        ident.decompile(s);
        s.print("(");
        listExpr.decompile(s);
        s.print(")");

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s,prefix,false);
        ident.prettyPrint(s,prefix,false);
        listExpr.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        ident.iter(f);
        listExpr.iter(f);
    }
}
