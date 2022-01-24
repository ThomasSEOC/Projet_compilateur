package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.*;

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

        // copy R1 to virtual register and check type
        getCodeGenBackEnd().addInstruction(new INT(GPRegister.getR(1), r.requestPhysicalRegister()));
        getCodeGenBackEnd().addInstruction(new BOV(getCodeGenBackEnd().getErrorsManager().getWrongInputTypeLabel()));

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

        // check type
        getCodeGenBackEnd().addInstruction(new INT(GPRegister.getR(1), GPRegister.getR(1)));
        getCodeGenBackEnd().addInstruction(new BOV(getCodeGenBackEnd().getErrorsManager().getWrongInputTypeLabel()));

        // print R1
        getCodeGenBackEnd().addInstruction(new WINT());
    }
}
