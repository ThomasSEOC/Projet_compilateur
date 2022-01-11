package fr.ensimag.deca.codegen;

import com.sun.tools.javac.jvm.Code;
import fr.ensimag.deca.tree.AbstractExpr;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

/**
 * Abstract class implementing final vocabulary (int or float)
 *
 * @author gl64
 * @date 11/10/2022
 */
public abstract class AbstractReadOperation extends AbstractOperation {

    /**Constructor of abstract class AbstractReadOperation
     *
     * @param codegenbackend
     * @param expression
     */
    public AbstractReadOperation(CodeGenBackend codegenbackend, AbstractExpr expression){
        super (codegenbackend, expression);
    }



}
