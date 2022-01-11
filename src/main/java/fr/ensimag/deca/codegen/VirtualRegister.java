package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class VirtualRegister {
    private final ContextManager contextManager;

    private boolean isPhysicalRegister;

    // only relevant if physical register
    private GPRegister physicalRegister;

    // only relevant if in stack register
    private int localIndex;

    public VirtualRegister(ContextManager contextManager, GPRegister register) {
        isPhysicalRegister = true;
        this.contextManager = contextManager;
        this.physicalRegister = register;
    }

    public VirtualRegister(ContextManager contextManager, int localIndex) {
        isPhysicalRegister = false;
        this.contextManager = contextManager;
        this.localIndex = localIndex;
    }

    public void destroy() {
        contextManager.freeRegister(this);
    }

    public boolean getIsPhysicalRegister() { return isPhysicalRegister; }

    public GPRegister requestPhysicalRegister() {
        if (!isPhysicalRegister) {
            // need to request a free physical register
            contextManager.AllocatePhysicalRegister(this);
        }

        return physicalRegister;
    }

    public int getLocalIndex() {
        return localIndex;
    }

    public DVal getDVal() {
        if (isPhysicalRegister) {
            return physicalRegister;
        }
        else {
            return new RegisterOffset(localIndex - contextManager.getStackOffset(), Register.SP);
        }
    }

    public void setPhysical(GPRegister register) {
        isPhysicalRegister = true;
        physicalRegister = register;
    }

    public void setInStack(int localIndex) {
        isPhysicalRegister = false;
        this.localIndex = localIndex;
    }
}
