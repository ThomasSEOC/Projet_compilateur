package fr.ensimag.deca.codegen;


import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;

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
        getCodeGenBackEnd().getCompiler().addInstruction(new RFLOAT());

    }


}
