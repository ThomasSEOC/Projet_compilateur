package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class Selection extends AbstractLValue{
    private AbstractExpr expr;
    private AbstractIdentifier fieldIdent;

    public Selection(AbstractExpr expr, AbstractIdentifier fieldIdent){
        Validate.notNull(expr);
        Validate.notNull(fieldIdent);
        this.expr = expr;
        this.fieldIdent = fieldIdent;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Visibility visibility = fieldIdent.getFieldDefinition().getVisibility();
	Type selectType = verifyExpr(compiler, localEnv, currentClass);
	ClassDefinition classDef =(ClassDefinition) compiler.getTypes().get(selectType.getName());
	ClassType selectClass = classDef.getType();
	Type typeF;
    	if (visibility == Visibility.PUBLIC) {
	    if (selectClass != compiler.getTypes().get(selectClass.getName()).getType()) {
		throw new ContextualError("Class not defined", getLocation());
	    }
	    typeF = fieldIdent.verifyExpr(compiler, ((ClassDefinition)compiler.getTypes().get(selectClass.getName())).getMembers(), currentClass);
	}
	else {
	    if (!(selectClass).isSubClassOf(currentClass.getType()) && !selectClass.isClassOrNull()) {
		throw new ContextualError("Subtype error", getLocation());
	    }
	    typeF = fieldIdent.verifyExpr(compiler, ((ClassDefinition)compiler.getTypes().get(selectClass.getName())).getMembers(), currentClass);
	    if (!selectClass.isClassOrNull() && !selectClass.isSubClassOf((ClassType)typeF)) {
		throw new ContextualError("Subtype error", getLocation());
	    }
	}
	return typeF;
    }

    @Override
    public void decompile(IndentPrintStream s) {

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
}
