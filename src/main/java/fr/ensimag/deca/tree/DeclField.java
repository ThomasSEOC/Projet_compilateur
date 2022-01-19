package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class DeclField extends AbstractDeclField{

    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier field;
    final private AbstractInitialization init;

    public DeclField(Visibility visibility, AbstractIdentifier type, AbstractIdentifier field, AbstractInitialization init){
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(field);
        Validate.notNull(init);
        this.visibility = visibility;
        this.type = type;
        this.field = field;
        this.init = init;
    }

    public AbstractIdentifier getType() { return type; }

    public AbstractIdentifier getField() { return field; }

    public AbstractInitialization getInit() { return init; }

    @Override
    protected void verifyDeclField(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

    }

    @Override
    public void decompile(IndentPrintStream s) {
        if(visibility == Visibility.PROTECTED){
            s.print("protected");
        }
        type.decompile(s);
        s.print(" ");
        field.decompile(s);
        s.print(" ");
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
