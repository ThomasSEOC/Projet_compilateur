package fr.ensimag.deca.codeGen;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ContextManager;
import fr.ensimag.deca.codegen.VirtualRegister;
import fr.ensimag.deca.context.ContextualError;
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
    public void testRegistersCount() {
        ContextManager contextManager = compiler.getCodeGenBackend().getContextManager();

        assertEquals(4, contextManager.getUsableRegistersCount());
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

    @Test
    public void testInStackToPhysical() {
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

        GPRegister physicalRegister3 = register3.requestPhysicalRegister();
        assertEquals(physicalRegister3.getNumber(), 3);

        GPRegister physicalRegister4 = register4.requestPhysicalRegister();
        assertEquals(physicalRegister4.getNumber(), 3);

        assertEquals(register4.getDVal(), GPRegister.getR(3));

        assertTrue(register3.getIsInStack());

        register3.destroy();
    }

    @Test
    public void testInStackToPhysical2() {
        ContextManager contextManager = compiler.getCodeGenBackend().getContextManager();
        VirtualRegister register1 = contextManager.requestNewRegister();
        VirtualRegister register2 = contextManager.requestNewRegister();
        VirtualRegister register3 = contextManager.requestNewRegister();

        DVal dval = register3.getDVal();
        assertEquals("0(SP)", dval.toString());

        register2.destroy();

        GPRegister physicalRegister3 = register3.requestPhysicalRegister();
        assertEquals(physicalRegister3.getNumber(), 3);

        assertFalse(register3.getIsInStack());

        assertTrue(register3.getDVal() instanceof GPRegister);

        assertEquals(register3.getDVal(), GPRegister.getR(3));
    }

    @Test
    public void testInStackToPhysical3() {
        ContextManager contextManager = compiler.getCodeGenBackend().getContextManager();
        VirtualRegister register1 = contextManager.requestNewRegister();
        VirtualRegister register2 = contextManager.requestNewRegister();
        VirtualRegister register3 = contextManager.requestNewRegister();
        VirtualRegister register4 = contextManager.requestNewRegister();

        register2.destroy();

        GPRegister physicalRegister3 = register3.requestPhysicalRegister();
        assertEquals(physicalRegister3.getNumber(), 3);

        assertFalse(register3.getIsInStack());

        assertTrue(register3.getDVal() instanceof GPRegister);

        assertEquals(register3.getDVal(), GPRegister.getR(3));
    }

    @Test
    public void testInStackToPhysical4() {
        ContextManager contextManager = compiler.getCodeGenBackend().getContextManager();
        VirtualRegister register1 = contextManager.requestNewRegister();
        VirtualRegister register2 = contextManager.requestNewRegister();
        VirtualRegister register3 = contextManager.requestNewRegister();
        VirtualRegister register4 = contextManager.requestNewRegister();

        register2.destroy();

        register3.destroy();

        GPRegister physicalRegister4 = register4.requestPhysicalRegister();
        assertEquals(physicalRegister4.getNumber(), 3);

        assertFalse(register4.getIsInStack());

        assertTrue(register4.getDVal() instanceof GPRegister);

        assertEquals(register4.getDVal(), GPRegister.getR(3));
    }
}
