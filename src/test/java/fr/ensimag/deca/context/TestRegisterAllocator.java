package fr.ensimag.deca.context;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ContextManager;
import fr.ensimag.deca.codegen.VirtualRegister;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class TestRegisterAllocator {
    DecacCompiler compiler;

    @Mock
    CompilerOptions compilerOptions;

    @BeforeEach
    public void setup() throws ContextualError {
        MockitoAnnotations.initMocks(this);
        compiler = new DecacCompiler(compilerOptions, null);
        when(compilerOptions.getRegistersCount()).thenReturn(4);
    }

    @Test
    public void testOnePhysicalRegister() {
        ContextManager contextManager = compiler.getCodeGenBackend().getContextManager();
        VirtualRegister register = contextManager.requestNewRegister();
        DVal dval = register.getDVal();
        assertEquals("R2", dval.toString());
        GPRegister physicalRegister = register.requestPhysicalRegister();
        assertEquals(physicalRegister.getNumber(), 2);
    }

    @Test
    public void testFourPhysicalRegister() {
        ContextManager contextManager = compiler.getCodeGenBackend().getContextManager();
        VirtualRegister register1 = contextManager.requestNewRegister();
        VirtualRegister register2 = contextManager.requestNewRegister();
        VirtualRegister register3 = contextManager.requestNewRegister();
        VirtualRegister register4 = contextManager.requestNewRegister();

        DVal dval = register1.getDVal();
        assertEquals("R2", dval.toString());
        GPRegister physicalRegister = register1.requestPhysicalRegister();
        assertEquals(physicalRegister.getNumber(), 2);

        dval = register2.getDVal();
        assertEquals("R3", dval.toString());
        physicalRegister = register2.requestPhysicalRegister();
        assertEquals(physicalRegister.getNumber(), 3);

        dval = register3.getDVal();
        assertEquals("-1(SP)", dval.toString());


        dval = register4.getDVal();
        assertEquals("0(SP)", dval.toString());
    }
}
