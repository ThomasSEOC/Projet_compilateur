package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.VirtualRegister;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * @author gl54
 * @date 01/01/2022
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    public AbstractIdentifier getType() {
        return type;
    }

    public AbstractIdentifier getVarName() { return varName; }

    public AbstractInitialization getInitialization() { return initialization; }


    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        // check type
        type.verifyType(compiler);
        if (type.getType().isVoid()) {
            throw new ContextualError("Var must not be void", getLocation());
        }

        // check initialization
        initialization.verifyInitialization(compiler, type.getType(), localEnv, currentClass);

        try {
            varName.setDefinition(new VariableDefinition(type.getType(), getLocation()));
//            compiler.getSymbolTable().getSymbol(varName.getName().getName())
//            System.out.println(varName.getName());
//            localEnv.declare(varName.getName(), type.getExpDefinition());
            localEnv.declare(varName.getName(), varName.getVariableDefinition());

            //System.out.println(compiler.getSymbolTable().getSymbol(varName.getName().getName()));
        } catch (DoubleDefException e) {
            throw new ContextualError("Var is already defined", getLocation());
//            System.out.println(varName.getName() + " : " + e);
//            System.exit(1);
        }
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    public void codeGenDeclVar(DecacCompiler compiler) {
        // declare variable to backend
        compiler.getCodeGenBackend().addVariable(varName.getName().getName());
        int offset = compiler.getCodeGenBackend().getVariableOffset(varName.getName().getName());

        // set address operand
        varName.getVariableDefinition().setOperand(new RegisterOffset(offset, Register.GB));

        // init variable if initialization
        if (initialization instanceof Initialization) {
            Initialization init = (Initialization) initialization;
            init.getExpression().codeGenInst(compiler);
            VirtualRegister result = compiler.getCodeGenBackend().getContextManager().operationStackPop();
            compiler.addInstruction(new STORE(result.requestPhysicalRegister(), varName.getVariableDefinition().getOperand()));
            result.destroy();
        }
    }
}
