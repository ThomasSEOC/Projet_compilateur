package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.AssignOperation;
import fr.ensimag.deca.codegen.VirtualRegister;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
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

        // Check if it is a void type
        if (type.getType().isVoid()) {
            throw new ContextualError("Var must not be void", getLocation());
        }


        // check if the name is a Predefined type or a class
        EnvironmentType envTypes = compiler.getTypes();
        SymbolTable.Symbol realSymbol = varName.getName();
        TypeDefinition typeDef =  envTypes.get(realSymbol);
        if (typeDef != null){
            if (typeDef.isClass()){
                throw new ContextualError(realSymbol + " is a class name defined at "+
                        envTypes.getDico().get(realSymbol).getLocation()+ ", can't be a var name", getLocation());
            }
            else {
                throw new ContextualError(realSymbol + " is a predefined type, can't be a var name", getLocation());
            }
        }

        // check initialization
        initialization.verifyInitialization(compiler, type.getType(), localEnv, currentClass);

        // put the variable name in the local environment
        try {
            varName.setDefinition(new VariableDefinition(type.getType(), getLocation()));
            localEnv.declare(varName.getName(), varName.getVariableDefinition());
            varName.setType(type.getType());
            if (varName.getVariableDefinition().isExpression()) {
            //    throw new ContextualError("Variable name must not be an expression", getLocation());
            }
        } catch (DoubleDefException e) {
                throw new ContextualError(realSymbol + " is already defined at " +
                        localEnv.get(realSymbol).getLocation(), getLocation());
            }
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
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
        RegisterOffset registerOffset = compiler.getCodeGenBackend().getVariableRegisterOffset(varName.getName().getName());

        // set address operand
        varName.getVariableDefinition().setOperand(registerOffset);

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
