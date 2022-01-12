package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class DeclField extends AbstractDeclField{

    private AbstractIdentifier type;
    private AbstractIdentifier field;
    private AbstractInitialization init;

    public DeclField(AbstractIdentifier type, AbstractIdentifier field, AbstractInitialization init){
        Validate.notNull(type);
        Validate.notNull(field);
        Validate.notNull(init);
        this.type = type;
        this.field = field;
        this.init = init;
    }


    @Override
    protected void verifyDeclField(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

    }

    @Override
    public void decompile(IndentPrintStream s) {
        // A mettre en forme
        type.decompile(s);
        field.decompile(s);
        init.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        field.prettyPrint(s,prefix,false);
        init.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        field.iter(f);
        init.iter(f);
    }
}
