package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * class dedicated to addressing modes abstraction to an object called a virtual register
 * constructors must be called by context manager only
 */
public class VirtualRegister {
    private static final int PHYSICAL = 0;
    private static final int INSTACK = 1;
    private static final int IMMEDIAT_INT = 2;
    private static final int IMMEDIAT_FLOAT = 3;
    private static final int IMMEDIAT_BOOLEAN = 4;
    private static final int IMMEDIAT_STRING = 5;

    private final ContextManager contextManager;

    private int type;

    private boolean isValueFloat = false;

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

    /**
     * constructor for physical register
     * @param contextManager current context manager
     * @param register physical register
     */
    public VirtualRegister(ContextManager contextManager, GPRegister register) {
        type = PHYSICAL;
        this.contextManager = contextManager;
        this.physicalRegister = register;
    }

    /**
     * constructor for in stack register
     * @param contextManager current context manager
     * @param localIndex local index of in stack register
     */
    public VirtualRegister(ContextManager contextManager, int localIndex) {
        type = INSTACK;
        this.contextManager = contextManager;
        this.localIndex = localIndex;
    }

    /**
     * constructor for int immediate register
     * @param contextManager current context manager
     * @param immediate int immediate
     */
    public VirtualRegister(ContextManager contextManager, ImmediateInteger immediate) {
        type = IMMEDIAT_INT;
        this.contextManager = contextManager;
        this.immediateInteger = immediate;
        setInt();
    }

    /**
     * constructor for float immediate register
     * @param contextManager current context manager
     * @param immediate float immediate
     */
    public VirtualRegister(ContextManager contextManager, ImmediateFloat immediate) {
        type = IMMEDIAT_FLOAT;
        this.contextManager = contextManager;
        this.immediateFloat = immediate;
        setFloat();
    }

    /**
     * constructor for boolean immediate register (cast to int)
     * @param contextManager current context manager
     * @param immediate boolean immediate value
     */
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

    /**
     * destroy virtual register and free used resources
     */
    public void destroy() {
       destroy(false);
    }

    /**
     * destroy virtual register and free used resources
     */
    public void destroy(boolean forceDestroy) {
        if (forceDestroy || (contextManager.getLastStoreRegister() != this)) {
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
    }

    /**
     * copy virtual register to physical register if not already and return used physical register
     * @return physical register used by this virtual register
     */
    public GPRegister requestPhysicalRegister() {
        if (type != PHYSICAL) {
            // need to request a free physical register
            contextManager.AllocatePhysicalRegister(this);
            if (type != INSTACK) {
                contextManager.getBackend().addInstruction(new LOAD(this.getDVal(), physicalRegister));
            }
        }

        type = PHYSICAL;

        return physicalRegister;
    }

    /**
     * getter for localIndex, only relevant if register is in stack
     * @return local index of register in stack
     */
    public int getLocalIndex() {
        return localIndex;
    }

    /**
     * get DVal for virtual register
     * @return DVal which may represent various addressing modes
     */
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
        else if (type == IMMEDIAT_BOOLEAN) {
            return immediateInteger;
        }
        else { // error
            throw new UnsupportedOperationException("cannot get DVal for the current type and/or addressing mode");
        }
    }

    /**
     * getter for immediate string, only relevant if it's a string immediate
     * @return immediate string
     */
    public ImmediateString getImmediateString() {
        return immediateString;
    }

    /**
     * set register as physical one
     * @param register physical register
     */
    public void setPhysical(GPRegister register) {
        physicalRegister = register;
    }

    /**
     * set local index to in stack register
     * @param localIndex local index in stack
     */
    public void setInStack(int localIndex) {
        type = INSTACK;
        this.localIndex = localIndex;
    }

    /**
     * set data type as float
     */
    public void setFloat() {
        isValueFloat = true;
    }

    /**
     * set data type as int
     */
    public void setInt() {
        isValueFloat = false;
    }

    /**
     * getter for data float condition
     * @return true if data type is float, false otherwise
     */
    public boolean getIsFloat() {
        return isValueFloat;
    }

    /**
     * getter for data in stack condition
     * @return true if data is in stack, false otherwise
     */
    public boolean getIsInStack() {
        return type == INSTACK;
    }
}
