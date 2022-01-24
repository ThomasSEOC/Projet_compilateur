package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;


public class Cast extends AbstractExpr {

    private AbstractIdentifier castType;
    private AbstractExpr castedExpr;

    public Cast(AbstractIdentifier castType, AbstractExpr castedExpr) {
	this.castType = castType;
	this.castedExpr = castedExpr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
			   ClassDefinition currentClass) throws ContextualError {
	Type type = castType.verifyType(compiler);
	Type type2 = castedExpr.verifyExpr(compiler, localEnv, currentClass);

	if (!castCompatible(type, type2)) {
	    throw new ContextualError(type + " cannot be casted into " + type2, getLocation());
	}

	setType(type);
	return type;
    }


    protected  boolean castCompatible(Type type, Type type2) {
	if (type.isVoid()) {
	    return false;
	}
	
	if ((type.isInt() || type.isFloat()) && (type2.isInt() || type2.isFloat())) {
	    return true;
	}

	ClassType classType = (ClassType) type;
	ClassType classType2 = (ClassType) type2;
	return classType.isSubClassOf(classType2) || classType2.isSubClassOf(classType);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
	castType.prettyPrint(s, prefix, false);
	castedExpr.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
	castType.iter(f);
	castedExpr.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        castType.decompile(s);
        s.print(") ");
        castedExpr.decompile(s);
    }

}



