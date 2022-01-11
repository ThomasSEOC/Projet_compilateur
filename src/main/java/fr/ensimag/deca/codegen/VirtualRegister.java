package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class VirtualRegister {
    private static final int PHYSICAL = 0;
    private static final int INSTACK = 1;
    private static final int IMMEDIAT_INT = 2;

    private final ContextManager contextManager;

    private int type;

    // only relevant if physical register
    private GPRegister physicalRegister;

    // only relevant if in stack register
    private int localIndex;

    // only relevant if immediate
    private ImmediateInteger immediateInteger;

    public VirtualRegister(ContextManager contextManager, GPRegister register) {
        type = PHYSICAL;
        this.contextManager = contextManager;
        this.physicalRegister = register;
    }

    public VirtualRegister(ContextManager contextManager, int localIndex) {
        type = INSTACK;
        this.contextManager = contextManager;
        this.localIndex = localIndex;
    }

    public VirtualRegister(ContextManager contextManager, ImmediateInteger immediate) {
        type = IMMEDIAT_INT;
        this.contextManager = contextManager;
        this.immediateInteger = immediate;
    }

    public void destroy() {
        switch (type) {
            case PHYSICAL:
                contextManager.freePhysicalRegister(this);
                break;
            case INSTACK:
                contextManager.freeInStackRegister(this);
                break;
            default:
                break;
        }
    }

    public GPRegister requestPhysicalRegister() {
        if (type != PHYSICAL) {
            // need to request a free physical register
            contextManager.AllocatePhysicalRegister(this);
        }
        return physicalRegister;
    }

    public int getLocalIndex() {
        return localIndex;
    }

    public DVal getDVal() {
        if (type == PHYSICAL) {
            return physicalRegister;
        }
        else if (type == INSTACK) {
            return new RegisterOffset(localIndex - contextManager.getStackOffset(), Register.SP);
        }
        else {
            // integer immediate
            return immediateInteger;
        }
    }

    public void setPhysical(GPRegister register) {
        type = PHYSICAL;
        physicalRegister = register;
    }

    public void setInStack(int localIndex) {
        type = INSTACK;
        this.localIndex = localIndex;
    }
}
