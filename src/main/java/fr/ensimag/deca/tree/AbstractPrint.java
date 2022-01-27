package fr.ensimag.deca.tree;

//import com.sun.imageio.plugins.common.SubImageInputStream;
import fr.ensimag.deca.codegen.IdentifierRead;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.opti.InstructionIdentifiers;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.mockito.internal.creation.SuspendMethod;

import java.util.Iterator;

/**
 * Print statement (print, println, ...).
 *
 * @author gl54
 * @date 01/01/2022
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }
    protected boolean getPrintHex() {
        return printHex;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType)
            throws ContextualError {

        // Verify all the expression in the HashMap
        Iterator<AbstractExpr> it = arguments.iterator();
        while (it.hasNext()) {
            Type typeExpr = it.next().verifyExpr(compiler, localEnv, currentClass);

            // Verify if the type id compatible with a print
            if (!typeExpr.isInt() && !typeExpr.isFloat() && !typeExpr.isString()) {
                throw new ContextualError("What is printed needs to be either an int, a float or a string", getLocation());
            }
        }
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.getCodeGenBackend().setPrintHex(getPrintHex());

        for (AbstractExpr a : getArguments().getList()) {
            a.codeGenPrint(compiler);
        }
    }

    @Override
    public void searchIdentifiers(InstructionIdentifiers instructionIdentifiers) {
        for (AbstractExpr expr : getArguments().getList()) {
            expr.searchIdentifiers(instructionIdentifiers);
        }
    }
}
