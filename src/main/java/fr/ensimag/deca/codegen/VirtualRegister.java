package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class VirtualRegister {
    private static final int PHYSICAL = 0;
    private static final int INSTACK = 1;
    private static final int IMMEDIAT_INT = 2;
    private static final int IMMEDIAT_FLOAT = 3;
    private static final int IMMEDIAT_BOOLEAN = 4;
    private static final int IMMEDIAT_STRING = 5;

    private final ContextManager contextManager;

    private int type;

    // only relevant if physical register
    private GPRegister physicalRegister;

    // only relevant if in stack register
    private int localIndex;

    // only relevant if immediate integer or boolean
    private ImmediateInteger immediateInteger;

    // only relevant if immediate float
    private ImmediateFloat immediateFloat;

    // only relevant if immediate string
    private ImmediateString immediateString;

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

    public VirtualRegister(ContextManager contextManager, ImmediateFloat immediate) {
        type = IMMEDIAT_FLOAT;
        this.contextManager = contextManager;
        this.immediateFloat = immediate;
    }

    public VirtualRegister(ContextManager contextManager, Boolean immediate) {
        type = IMMEDIAT_BOOLEAN;
        this.contextManager = contextManager;
        if (immediate) {
            this.immediateInteger = new ImmediateInteger(1);
        }
        else {
            this.immediateInteger = new ImmediateInteger(0);
        }
    }

    public VirtualRegister(ContextManager contextManager, ImmediateString immediate) {
        type = IMMEDIAT_STRING;
        this.contextManager = contextManager;
        this.immediateString = immediate;
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
        else if (type == IMMEDIAT_INT) {
            return immediateInteger;
        }
        else if (type == IMMEDIAT_FLOAT) {
            return immediateFloat;
        }
        else { // IMMEDIAT_BOOLEAN
            return immediateInteger;
        }
    }

    public ImmediateString getImmediateString() {
        return immediateString;
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
