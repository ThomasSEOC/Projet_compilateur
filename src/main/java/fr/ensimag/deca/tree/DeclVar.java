package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.AssignOperation;
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

        // check if the name is a Predefined type
        EnvironmentType envTypesPredef = compiler.getTypesPredef();
        SymbolTable.Symbol realSymbol = varName.getName();
        if (envTypesPredef.get(realSymbol) != null){
            throw new ContextualError(realSymbol + " is a predefined type, can't be a variable name", getLocation());
        }

        // check initialization
        initialization.verifyInitialization(compiler, type.getType(), localEnv, currentClass);

        // put the variable name in the local environment
        try {
            varName.setDefinition(new VariableDefinition(type.getType(), getLocation()));
            localEnv.declare(varName.getName(), varName.getVariableDefinition());
	} catch (DoubleDefException e) {
            throw new ContextualError("Var is already defined", getLocation());
        }
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(type.getName().getName());
        s.print(" ");
        s.print(varName.getName().getName());
        initialization.decompile(s);
        s.println(";");
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

            // create an Assign
            Assign expr = new Assign(varName, init.getExpression());
            AssignOperation operator = new AssignOperation(compiler.getCodeGenBackend(), expr);
            operator.doOperation();
//            init.getExpression().codeGenInst(compiler);
//            VirtualRegister result = compiler.getCodeGenBackend().getContextManager().operationStackPop();
//            compiler.addInstruction(new STORE(result.requestPhysicalRegister(), varName.getVariableDefinition().getOperand()));
//            result.destroy();
        }
    }
}
