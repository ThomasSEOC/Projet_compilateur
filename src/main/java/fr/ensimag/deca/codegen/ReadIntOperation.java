package fr.ensimag.deca.codegen;


import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RINT;
import fr.ensimag.ima.pseudocode.instructions.WINT;

/**
 * Class responsible for int read
 *
 * @author gl54
 * @date 11/01/22
 */
public class ReadIntOperation extends AbstractReadOperation{

    /**
     * Constructor of Class IntOperation
     *
     * @param codegenbackend global code generation backend
     * @param expression expression related to operation
     */
    public ReadIntOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super(codegenbackend, expression);
    }

    /**
     * method called to generate code for int read
     */
    @Override
    public void doOperation(){
        // read int to R1
        getCodeGenBackEnd().addInstruction(new RINT());

        // request new virtual register
        VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister();

        // copy R1 to virtual register
        getCodeGenBackEnd().addInstruction(new LOAD(GPRegister.getR(1), r.requestPhysicalRegister()));

        // push virtual register to operation stack
        getCodeGenBackEnd().getContextManager().operationStackPush(r);
    }

    /**
     * method called to generate code for int read print
     */
    @Override
    public void print() {
        // read int to R1
        getCodeGenBackEnd().addInstruction(new RINT());

        // print R1
        getCodeGenBackEnd().addInstruction(new WINT());
    }


}
