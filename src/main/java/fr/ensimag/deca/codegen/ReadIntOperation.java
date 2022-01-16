package fr.ensimag.deca.codegen;


import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RINT;
import fr.ensimag.ima.pseudocode.instructions.WINT;

/**
 * Class using int types
 *
 * @author gl54
 * @date 11/01/22
 */
public class ReadIntOperation extends AbstractReadOperation{

    /**
     * Constructor of Class IntOperation
     *
     * @param codegenbackend
     * @param expression
     */
    public ReadIntOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super(codegenbackend, expression);
    }

    /**
     * Override of doOperation Method for ReadInt
     *
     * @return int
     */
    @Override
    public void doOperation(){
        //Rien en entrée:
        getCodeGenBackEnd().getCompiler().addInstruction(new RINT());

        VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister();

        getCodeGenBackEnd().getCompiler().addInstruction(new LOAD(GPRegister.getR(1), r.requestPhysicalRegister()));

        getCodeGenBackEnd().getContextManager().operationStackPush(r);

    }

    @Override
    public void print() {
        getCodeGenBackEnd().getCompiler().addInstruction(new RINT());
        getCodeGenBackEnd().getCompiler().addInstruction(new WINT());
    }


}
