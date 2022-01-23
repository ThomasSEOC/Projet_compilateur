package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl54
 * @date 01/01/2022
 */
public class ListExpr extends TreeList<AbstractExpr> {


    @Override
    public void decompile(IndentPrintStream s) {
        int length_list = getList().size()- 1 ;

        for (AbstractExpr expr : getList()) {
            expr.decompile(s);

            // On affiche (", ") que si le paramètre courant n'est pas le dernier de la liste
            int range = getList().indexOf(expr);
            if (! (range == length_list)) {
                s.print(", ");
            }
        }
    }
}
