package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;

/**
 * Class responsible for float read
 *
 * @author gl54
 * @date 11/01/22
 */
public class ReadFloatOperation extends AbstractReadOperation {


    /**
     * Constructor of Class FloatOperation
     * @param codegenbackend global code generation backend
     * @param expression expression related to operation
     */
    public ReadFloatOperation (CodeGenBackend codegenbackend, AbstractExpr expression){
        super (codegenbackend, expression);
    }

    /**
     * method called to generate code for float read
     */
    @Override
    public void doOperation(){
        // add float read instruction
        getCodeGenBackEnd().addInstruction(new RFLOAT());

        // request new virtual register
        VirtualRegister r = getCodeGenBackEnd().getContextManager().requestNewRegister();

        // copy R1 to virtual register
        getCodeGenBackEnd().addInstruction(new LOAD(GPRegister.getR(1), r.requestPhysicalRegister()));

        // add virtual register to operation stack
        getCodeGenBackEnd().getContextManager().operationStackPush(r);
    }

    /**
     * method called to generate code for float read print
     */
    @Override
    public void print() {
        // add float read instruction
        getCodeGenBackEnd().addInstruction(new RFLOAT());

        // print according to float format
        if (getCodeGenBackEnd().getPrintHex()) {
            getCodeGenBackEnd().addInstruction(new WFLOATX());
        }
        else {
            getCodeGenBackEnd().addInstruction(new WFLOAT());
        }
    }


}
