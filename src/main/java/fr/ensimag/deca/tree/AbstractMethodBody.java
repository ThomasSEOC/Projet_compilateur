package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;

public abstract class AbstractMethodBody extends Tree{

    abstract public void codeGen(DecacCompiler compiler);


}
