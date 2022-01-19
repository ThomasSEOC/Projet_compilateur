package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl54
 * @date 11/01/2022
 */
public class DeclClass extends AbstractDeclClass {

    private AbstractIdentifier nameClass;
    private AbstractIdentifier superClass;
    private ListDeclMethod methods;
    private ListDeclField field;
    private ClassType classType;
    private ClassDefinition classDefinition;

    public DeclClass(AbstractIdentifier nameClass, AbstractIdentifier superClass, ListDeclMethod methods, ListDeclField field){
        Validate.notNull(nameClass);
        Validate.notNull(superClass);
        Validate.notNull(methods);
        this.nameClass = nameClass;
        this.superClass = superClass;
        this.methods = methods;
        this.field = field ;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        nameClass.decompile(s);
        s.print(" extends");
        superClass.decompile(s);
        s.println("{");
        s.indent();
        methods.decompile(s);
        field.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        // verifies the existence of superClass
        superClass.verifyType(compiler);

        // creates and put the ClassTypeclassType = new ClassType(nameClass.getName(), getLocation(), (ClassDefinition) compiler.getExpPredef().get(superClass.getName()));
        classDefinition = classType.getDefinition();

        // Definition and type of the class
        nameClass.setDefinition(classDefinition);
        nameClass.setType(classType);

        // put in the dictionary
        try {
            compiler.getTypesPredef().declare(nameClass.getName(), classDefinition);
        } catch (EnvironmentExp.DoubleDefException e) {
            System.out.println("Object : " + e);
            System.exit(1);
        }


    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        methods.verifyListDeclMethod(compiler, classDefinition.getMembers(), classDefinition);


    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        nameClass.prettyPrint(s,prefix,false);
        superClass.prettyPrint(s,prefix,false);
        methods.prettyPrint(s,prefix,false);
        field.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        throw new UnsupportedOperationException("Not yet supported");
    }

}
