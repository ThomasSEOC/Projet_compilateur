package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.util.Map;

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


    @Override
    protected void verifyDeclField(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        // verify that the type is not a void
        type.verifyType(compiler);
        if (type.getType().isVoid()) {
            throw new ContextualError("This field must not be void type", getLocation());
        }

        // check if the field is already defined in the current and the superclass
        Map<SymbolTable.Symbol, ExpDefinition> dico = localEnv.getDico();
        ClassDefinition iterClass = currentClass;
        while (iterClass != null) {
            if (currentClass.getMembers().get(field.getName()) != null) {
                throw new ContextualError("This field is already defined at " + dico.get(field.getName()), getLocation());
            }
            iterClass = iterClass.getSuperClass();
        }


        // Put the field in the localEnv
        try {
            field.setDefinition(new FieldDefinition(type.getType(), getLocation(), visibility, currentClass, currentClass.getNumberOfFields()));
            localEnv.declare(field.getName(), field.getFieldDefinition());
        } catch (DoubleDefException e) {
            throw new ContextualError("This field is already defined at " + dico.get(field.getName()), getLocation());
        }
        field.verifyExpr(compiler, localEnv, currentClass);


        // check initialization
        init.verifyInitialization(compiler, type.getType(), localEnv, currentClass);
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
