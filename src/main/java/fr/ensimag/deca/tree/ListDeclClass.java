package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;
import java.util.Iterator;

/**
 *
 * @author gl54
 * @date 01/01/2022
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            //s.println("{");
            c.decompile(s);
            //s.println("}");
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        Iterator<AbstractDeclClass> it = this.iterator();
        while (it.hasNext()) {
            it.next().verifyClass(compiler);
        }
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        Iterator<AbstractDeclClass> it = this.iterator();
        while (it.hasNext()) {
            it.next().verifyClassMembers(compiler);
        }
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        Iterator<AbstractDeclClass> it = this.iterator();
        while (it.hasNext()) {
            it.next().verifyClassBody(compiler);
        }
    }

    public void codeGenDeclare(DecacCompiler compiler) {
        for (AbstractDeclClass declClass : getList()) {
            declClass.codeGenDeclare(compiler);
        }
    }
}
