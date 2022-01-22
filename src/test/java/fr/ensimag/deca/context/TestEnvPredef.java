package fr.ensimag.deca.context;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.DecacCompiler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class TestEnvPredef {
    @Test
    public void testEnvPredef() throws ContextualError {
        DecacCompiler compiler = new DecacCompiler(new CompilerOptions(), null);
        EnvironmentExp envPredef = compiler.getExpPredef();
        System.out.println(envPredef);
    }
}
