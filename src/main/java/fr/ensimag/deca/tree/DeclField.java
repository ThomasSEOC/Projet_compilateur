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

    public AbstractIdentifier getType() { return type; }

    public AbstractIdentifier getField() { return field; }

    public AbstractInitialization getInit() { return init; }

    @Override
    protected void verifyDeclField(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        SymbolTable.Symbol realSymbol = compiler.getSymbolTable().getSymbol(field.getName().getName());
        TypeDefinition typeDef =  compiler.getTypes().get(realSymbol);

        // Verify the existence of the type and set it
        type.verifyType(compiler);

        // Verify that the type is not a void
        if (type.getType().isVoid()) {
            throw new ContextualError("This field must not be void type", getLocation());
        }

        // Check if the name is a predefined type or a class
        if (typeDef != null && typeDef.isExpression()){
            if (typeDef.isClass()){
                throw new ContextualError(realSymbol + " is a class name defined at "+
                        typeDef.getLocation()+ ", can't be a field name", getLocation());
            }
            else {
                throw new ContextualError(realSymbol + " is a predefined type, can't be a field name", getLocation());
            }
        }

        // Check if the field is already defined in the current and the superclass
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
            throw new ContextualError("This field is already defined at " + dico.get(field.getName()).getLocation(), getLocation());
        }

        // Verify the expression of the field
        field.verifyExpr(compiler, localEnv, currentClass);

        // Check initialization
        init.verifyInitialization(compiler, type.getType(), localEnv, currentClass);
    }


    @Override
    public void decompile(IndentPrintStream s) {
        if(visibility == Visibility.PROTECTED){
            s.print("protected ");
        }
        type.decompile(s);
        s.print(" ");
        field.decompile(s);
        s.print(" ");
        init.decompile(s);
        s.print(";");
    }


    // Overrides the method to add the visibility decoration
    @Override
    String printNodeLine(PrintStream s, String prefix, boolean last,
                         boolean inlist, String nodeName){
        s.print(prefix);
        if (inlist) {
            s.print("[]>");
        } else if (last) {
            s.print("`>");
        } else {
            s.print("+>");
        }
        if (getLocation() != null) {
            s.print(" " + getLocation().toString());
        }
        s.print(" ");
        s.print("[visibility=" + visibility + "] ");
        s.print(nodeName);
        s.println();
        String newPrefix;
        if (last) {
            if (inlist) {
                newPrefix = prefix + "    ";
            } else {
                newPrefix = prefix + "   ";
            }
        } else {
            if (inlist) {
                newPrefix = prefix + "||  ";
            } else {
                newPrefix = prefix + "|  ";
            }
        }
        prettyPrintType(s, newPrefix);
        return newPrefix;
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
