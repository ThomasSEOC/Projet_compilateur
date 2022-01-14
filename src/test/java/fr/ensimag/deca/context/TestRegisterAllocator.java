package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ContextManager;
import fr.ensimag.deca.codegen.VirtualRegister;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class TestRegisterAllocator {
    @Test
    public void testOnePhysicalRegister() {
        ContextManager contextManager = new ContextManager(null, 16);
        VirtualRegister register = contextManager.requestNewRegister();
        DVal dval = register.getDVal();
        System.out.println("DVAL: " + dval.toString());
        GPRegister physicalRegister = register.requestPhysicalRegister();
        System.out.println("Physical register: " + physicalRegister.getNumber());
    }

    @Test
    public void testFourPhysicalRegister() {
        ContextManager contextManager = new ContextManager(null, 4);
        VirtualRegister register1 = contextManager.requestNewRegister();
        VirtualRegister register2 = contextManager.requestNewRegister();
        VirtualRegister register3 = contextManager.requestNewRegister();
        VirtualRegister register4 = contextManager.requestNewRegister();

        DVal dval = register1.getDVal();
        System.out.println("register1 DVAL: " + dval.toString());
        GPRegister physicalRegister = register1.requestPhysicalRegister();
        System.out.println("register1 Physical register: " + physicalRegister.getNumber());

        dval = register2.getDVal();
        System.out.println("register2 DVAL: " + dval.toString());
        physicalRegister = register2.requestPhysicalRegister();
        System.out.println("register2 Physical register: " + physicalRegister.getNumber());

        dval = register3.getDVal();
        System.out.println("register3 DVAL: " + dval.toString());

        dval = register4.getDVal();
        System.out.println("register4 DVAL: " + dval.toString());
    }
}
