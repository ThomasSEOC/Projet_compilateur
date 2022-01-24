package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class This extends AbstractExpr{

    private boolean implicite;

    public This(boolean implicite){
        this.implicite=implicite;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        if (currentClass == null) {
	        throw new ContextualError("Instruction is not in the current class", getLocation());
	    }
        setType(currentClass.getType());
        return currentClass.getType();
    }

    @Override
    public boolean isThis(){
        return true;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // If "this" is not i
        if(!implicite){
            s.print("this");
        }
	
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }
}
