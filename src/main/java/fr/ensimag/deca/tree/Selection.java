package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.FieldSelectOperation;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class Selection extends AbstractLValue{
    final private AbstractExpr expr;
    final private AbstractIdentifier fieldIdent;

    public Selection(AbstractExpr expr, AbstractIdentifier fieldIdent){
        Validate.notNull(expr);
        Validate.notNull(fieldIdent);
        this.expr = expr;
        this.fieldIdent = fieldIdent;
    }

    public AbstractExpr getExpr() {
        return expr;
    }

    public AbstractIdentifier getFieldIdent() {
        return fieldIdent;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        // Verify if the type exists and set it
        Type selectType = expr.verifyExpr(compiler, localEnv, currentClass);

        SymbolTable.Symbol exprTypeSymbol = selectType.getName();
        SymbolTable.Symbol fieldIdentSymbol = fieldIdent.getName();

        // Verify if exp hac a class type
        if (!selectType.isClass()){
            throw new ContextualError( " Must be a class type", getLocation());
        }

        // Verify in the main, if expr is not a class name
        if (!expr.isThis()) {
            SymbolTable.Symbol exprSymbol = ((AbstractIdentifier) (expr)).getName();
            if (compiler.getTypes().get(exprSymbol) != null) {
                throw new ContextualError(exprSymbol + " is a class name or a predefined type", getLocation());
            }
        }

        ClassDefinition classDef = (ClassDefinition) compiler.getTypes().get(exprTypeSymbol);
        ExpDefinition selectField = null;

        // Verify if the class have fieldIdent in it environment
        if (classDef.getMembers().get(fieldIdentSymbol)!=null){
            if (classDef.getMembers().get(fieldIdentSymbol).isField()){
                selectField = classDef.getMembers().get(fieldIdentSymbol);
                if (((FieldDefinition)selectField).getVisibility() == Visibility.PROTECTED) {
                    if (!(classDef.getType()).isSubClassOf(currentClass.getType())) {
                        throw new ContextualError("Subtype error", getLocation());
                    }
                    if (!classDef.getType().isSubClassOf(classDef.getType())) {
                        throw new ContextualError("Subtype error", getLocation());
                    }
                }
                selectField = classDef.getMembers().get(fieldIdentSymbol);
                setType((selectField).getType());
                if (currentClass != null) {
                    fieldIdent.verifyExpr(compiler, currentClass.getMembers(), currentClass);
                } else {
                    fieldIdent.verifyExpr(compiler, classDef.getMembers(), currentClass);
                }
                return (selectField).getType();
            }
             else {
                selectField = (MethodDefinition) classDef.getMembers().get(fieldIdentSymbol);
                setType(((MethodDefinition)selectField).getType());
                if (currentClass != null) {
                    fieldIdent.verifyExpr(compiler, currentClass.getMembers(), currentClass);
                } else {
                    fieldIdent.verifyExpr(compiler, classDef.getMembers(), currentClass);
                }
                return ((MethodDefinition)selectField).getType();
            }
        }
        else {
            throw new ContextualError("This is neither a field or a method of the class" + exprTypeSymbol, getLocation());
        }


    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(".");
        fieldIdent.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s,prefix,false);
        fieldIdent.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        expr.iter(f);

    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        FieldSelectOperation operator = new FieldSelectOperation(compiler.getCodeGenBackend(), this);
        operator.print();
    }


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        FieldSelectOperation operator = new FieldSelectOperation(compiler.getCodeGenBackend(), this);
        operator.doOperation();
    }
}
