package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class MOV extends LOAD {
    public MOV(GPRegister in, GPRegister out) {
        super((DVal) in, out);
    }

    public MOV(DVal in, GPRegister out) { super(in, out); }
}
