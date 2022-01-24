package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodCallOperation;
import fr.ensimag.deca.codegen.VirtualRegister;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
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

    public AbstractExpr getExpr() {
        return expr;
    }

    public AbstractIdentifier getIdent() {
        return ident;
    }

    public ListExpr getListExpr() {
        return listExpr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        Type classType = expr.verifyExpr(compiler, localEnv, currentClass);
        // verify if the method is called with a valid class Name
        if (!(classType.isClass())) {
            throw new ContextualError(expr + "is not called with a valid class name", getLocation());
        }
        SymbolTable.Symbol exprSymbol = ((AbstractIdentifier) (expr)).getName();
        if (compiler.getTypes().get(exprSymbol)!= null){
            if (expr.getType().isClass()) {
                throw new ContextualError(exprSymbol + " is a class name or a predefined type", getLocation());
            }
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
        ident.setDefinition(methodDef);
        Signature signature = ident.getMethodDefinition().getSignature();
        if (listExpr.size() != signature.size()) {
            throw new ContextualError(expr + "has a wrong list of param, please check at the method defined at " + methodDef.getLocation(), getLocation());
        }
        for (int i = 0; i < listExpr.size(); i++) {
            if (!(listExpr.getList().get(i).verifyExpr(compiler, localEnv, currentClass).sameType(signature.paramNumber(i)))) {
                throw new ContextualError(expr + "has a wrong list of param, please check at the method defined at " + methodDef.getLocation(), getLocation());
            }
        }

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

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        MethodCallOperation operator = new MethodCallOperation(compiler.getCodeGenBackend(), this);
        operator.print();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        MethodCallOperation operator = new MethodCallOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();

        VirtualRegister result = compiler.getCodeGenBackend().getContextManager().requestNewRegister();
        compiler.getCodeGenBackend().addInstruction(new LOAD(GPRegister.getR(0), result.requestPhysicalRegister()));
        compiler.getCodeGenBackend().getContextManager().operationStackPush(result);
    }
}
