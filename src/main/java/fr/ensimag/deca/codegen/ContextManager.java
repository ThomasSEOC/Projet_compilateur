package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.IntStream;

/**
 * class responsible for context and registers management
 */
public class ContextManager {
    private final CodeGenBackend backend;

    private int currentRegisterIndex = 2;
    private int stackOffset = 0;

    private final VirtualRegister[] physicalRegisters = new VirtualRegister[16];
    private final List<VirtualRegister> inStackRegisters;

    private final Stack<VirtualRegister> operationStack;

    private final boolean[] toSavePhysicalRegisters = new boolean[16];

    private VirtualRegister lastStoreRegister = null;
    private RegisterOffset lastStoreOffset = null;

    /**
     * constructor context manager, must be called only once by {@link CodeGenBackend}
     * @param backend {@link CodeGenBackend}
     */
    public ContextManager(CodeGenBackend backend) {
        this.backend = backend;

        IntStream.range(0, 16).forEach(i -> physicalRegisters[i] = null);
        IntStream.range(0, 16).forEach(i -> toSavePhysicalRegisters[i] = false);
        inStackRegisters = new ArrayList<>();

        operationStack = new Stack<>();
    }

    public void setLastStoreRegister(VirtualRegister register, RegisterOffset registerOffset) {
        this.lastStoreRegister = register;
        this.lastStoreOffset = registerOffset;
    }

    public VirtualRegister getLastStoreRegister() {
        return lastStoreRegister;
    }

    public void destroy() {
        // save register at beginning
        List<Instruction> savingInstructions = new ArrayList<>();
        List<String> savingComments = new ArrayList<>();
        int toSaveCount = 0;
        for (int i = 0; i < 16; i++) {
            if (toSavePhysicalRegisters[i]) {
                savingInstructions.add(new PUSH(GPRegister.getR(i)));
                savingComments.add(String.format("save R%d", i));
                toSaveCount++;
            }
        }
        backend.addInstructionFirst(savingInstructions, savingComments);
        backend.addCommentFirst("context save");

        backend.getStartupManager().generateStartupCode(toSaveCount);

        // if SP has an offset, remove it
        if (stackOffset != 0) {
            backend.addInstruction(new SUBSP(stackOffset));
        }

        // restore registers at the end
        backend.addComment("context restore");
        for (int i = 0; i < 16; i++) {
            if (toSavePhysicalRegisters[i]) {
                backend.addInstruction(new POP(GPRegister.getR(i)), String.format("restore R%d", i));
            }
        }

        // add RTS instruction
        backend.addInstruction(new RTS());
    }

    /**
     * getter for backend
     * @return codegen common backend
     */
    public CodeGenBackend getBackend() { return backend; }

    /**
     * getter for usable register count
     * @return usableRegistersCount
     */
    public int getUsableRegistersCount() {
        return backend.getCompiler().getCompilerOptions().getRegistersCount();
    }

    /**
     * allocate a physical register for a virtual register
     * IMPORTANT : the virtual register doesn't have physical register yet
     * @param virtualRegister virtualRegister requesting a physical register
     */
    public void AllocatePhysicalRegister(VirtualRegister virtualRegister) {
        if (lastStoreRegister != null) {
            lastStoreRegister.destroy();
        }
        lastStoreRegister = null;
        lastStoreOffset = null;

        // get registers count
        int usableRegistersCount = backend.getCompiler().getCompilerOptions().getRegistersCount();

        // if there is a free physical register
        if (currentRegisterIndex < usableRegistersCount) {
            // get a free physical register
            GPRegister register = GPRegister.getR(currentRegisterIndex);

            // if data is currently in stack
            if (virtualRegister.getIsInStack()) {
                // get index
                int localIndex = virtualRegister.getLocalIndex();

                // check if a pop is possible
                if (localIndex == stackOffset) {
                    // pop data to physical register
                    backend.addInstruction(new POP(register), "local variable is on top of stack");

                    // free stack from current data
                    stackOffset--;

                    // free unused in stack data
                    while ((stackOffset > 0) && (inStackRegisters.get(stackOffset - 1) == null)) {
                        stackOffset--;
                        inStackRegisters.remove(stackOffset);
                    }
                } else {
                    // copy data to physical register
                    backend.addInstruction(new LOAD(virtualRegister.getDVal(), register), String.format("copy from stack R%d", currentRegisterIndex));

                    // indicate that this data in stack is unused
                    inStackRegisters.set(localIndex, null);
                }
            }

            // mov virtual register from in stack to physical
            physicalRegisters[currentRegisterIndex] = virtualRegister;
            toSavePhysicalRegisters[currentRegisterIndex] = true;

            // set virtual register as physical register
            virtualRegister.setPhysical(register);

            // increment used physical registers count
            currentRegisterIndex++;
        } else {
            // need to copy last physical register in stack to free it

            // decrement physical registers count
            currentRegisterIndex--;

            // get physical register
            GPRegister register = GPRegister.getR(currentRegisterIndex);

            // get related virtual register
            VirtualRegister oldRegister = physicalRegisters[currentRegisterIndex];

            // push register to stack
            stackOffset++;
            if (stackOffset > backend.getMaxStackSize()) {
                backend.incMaxStackSize();
            }
            inStackRegisters.add(oldRegister);
            backend.addInstruction(new PUSH((GPRegister) oldRegister.getDVal()), "no more GP register available");
            oldRegister.setInStack(stackOffset);

            // add virtual register to physical registers
            physicalRegisters[currentRegisterIndex] = virtualRegister;
            toSavePhysicalRegisters[currentRegisterIndex] = true;

            // remove virtual register from stack if it's the case
            if (virtualRegister.getIsInStack()) {
                inStackRegisters.set(virtualRegister.getLocalIndex(), null);
            }

            // load into physical register
//            backend.addInstruction(new LOAD(virtualRegister.getDVal(), register), String.format("Load virtual register to R%d", currentRegisterIndex));

            // set virtual register as physical register
            virtualRegister.setPhysical(register);

            // increment used physical registers count
            currentRegisterIndex++;
        }
    }

    /**
     * method called to request a new virtual register without prerequisite
     * @return new virtual register
     */
    public VirtualRegister requestNewRegister() {
        if (lastStoreRegister != null) {
            lastStoreRegister.destroy();
        }
        lastStoreOffset = null;
        lastStoreRegister = null;

        VirtualRegister register;

        // get physical registers count
        int usableRegistersCount = backend.getCompiler().getCompilerOptions().getRegistersCount();

        // if there is a free physical register
        if (currentRegisterIndex < usableRegistersCount) {
            // create physical register
            register = new VirtualRegister(this, GPRegister.getR(currentRegisterIndex));
            physicalRegisters[currentRegisterIndex] = register;
            toSavePhysicalRegisters[currentRegisterIndex] = true;
            currentRegisterIndex++;
        }
        else { // no more free register
            // create an in stack register
            stackOffset++;
            if (stackOffset > backend.getMaxStackSize()) {
                backend.incMaxStackSize();
            }
            register = new VirtualRegister(this, stackOffset);
            inStackRegisters.add(register);
        }

        return register;
    }

    public VirtualRegister requestNewRegister(RegisterOffset registerOffset) {
        if (lastStoreOffset != null) {
            if ((registerOffset.getRegister() == lastStoreOffset.getRegister()) &&
                (registerOffset.getOffset() == lastStoreOffset.getOffset())) {
                VirtualRegister register = lastStoreRegister;
                lastStoreOffset = null;
                lastStoreRegister = null;
                return register;
            }
        }
        lastStoreOffset = null;
        lastStoreRegister = null;
        VirtualRegister register = requestNewRegister();
        getBackend().addInstruction(new LOAD(registerOffset, register.requestPhysicalRegister()));
        return register;
    }

    /**
     * method called to request a new immediate virtual register
     * @param immediate integer immediate
     * @return new virtual register
     */
    public VirtualRegister requestNewRegister(ImmediateInteger immediate) {
        return new VirtualRegister(this, immediate);
    }

    /**
     * method called to request a new immediate virtual register
     * @param immediate float immediate
     * @return new virtual register
     */
    public VirtualRegister requestNewRegister(ImmediateFloat immediate) {
        return new VirtualRegister(this, immediate);
    }

//    /**
//     * method called to request a new immediate virtual register
//     * @param immediate string immediate
//     * @return new virtual register
//     */
//    public VirtualRegister requestNewRegister(ImmediateString immediate) {
//        return new VirtualRegister(this, immediate);
//    }

    /**
     * method called to request a new immediate virtual register
     * @param immediate boolean immediate
     * @return new virtual register
     */
    public VirtualRegister requestNewRegister(boolean immediate) {
        return new VirtualRegister(this, immediate);
    }

    /**
     * remove a virtual register from physical registers
     * @param virtualRegister virtual register to remove
     */
    public void freePhysicalRegister(VirtualRegister virtualRegister) {
        // remove from physical registers
        int registerIndex = ((GPRegister) virtualRegister.getDVal()).getNumber();
        physicalRegisters[registerIndex] = null;

        // if last indexed physical register, need to decrement index until it points a used register
        if (registerIndex == (currentRegisterIndex-1)) {
            while ((currentRegisterIndex > 2) && (physicalRegisters[currentRegisterIndex-1] == null)) {
                currentRegisterIndex--;
            }
        }
    }

    /**
     * remove a virtual register from in stack registers
     * @param virtualRegister virtual register to remove
     */
    public void freeInStackRegister(VirtualRegister virtualRegister) {
        inStackRegisters.set(virtualRegister.getLocalIndex()-1, null);
        while ((stackOffset > 0) && (inStackRegisters.get(stackOffset - 1) == null)) {
            stackOffset--;
            inStackRegisters.remove(stackOffset);
        }
    }

    /**
     * getter from stackOffset
     * @return current stack offset
     */
    public int getStackOffset() {
        return stackOffset;
    }

    /**
     * pop a virtual register from operation stack
     * @return virtual register popped
     */
    public VirtualRegister operationStackPop() {
        return operationStack.pop();
    }

    /**
     * push a virtual register to operation stack
     * @param register virtual register to push
     */
    public void operationStackPush(VirtualRegister register) {
        operationStack.push(register);
    }

//    /**
//     * get operation stack length
//     * @return length of operation stack
//     */
//    public int operationStackLength() {
//        return operationStack.size();
//    }
}
