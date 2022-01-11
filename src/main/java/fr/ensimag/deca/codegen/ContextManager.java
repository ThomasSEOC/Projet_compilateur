package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * class responsable de la gestion des registres
 */
public class ContextManager {
    private final CodeGenBackend backend;
    private final int usableRegistersCount;
    private int currentRegisterIndex = 2;
    private int stackOffset = 0;
    private VirtualRegister[] physicalRegisters = new VirtualRegister[16];
    private List<VirtualRegister> inStackRegisters;

    /**
     * create the context manager, must be called only once by {@link CodeGenBackend}
     * @param backend {@link CodeGenBackend}
     * @param usableRegisterCount usable registers count
     */
    public ContextManager(CodeGenBackend backend, int usableRegisterCount) {
        this.backend = backend;
        this.usableRegistersCount = usableRegisterCount;

        IntStream.range(0, 16).forEach(i -> physicalRegisters[i] = null);

        inStackRegisters = new ArrayList<>();
    }

    public CodeGenBackend getBackend() { return backend; }

    /**
     * getter for usable register count
     * @return usableRegistersCount
     */
    public int getUsableRegistersCount() {
        return usableRegistersCount;
    }

    public void AllocatePhysicalRegister(VirtualRegister virtualRegister) {
        if (currentRegisterIndex < usableRegistersCount) {
            // a register is available
            GPRegister register = GPRegister.getR(currentRegisterIndex);

            int localIndex = virtualRegister.getLocalIndex();
            // if a pop is possible
            if (localIndex == stackOffset) {
                stackOffset--;
                while ((stackOffset > 0) && (inStackRegisters.get(stackOffset - 1) == null)) {
                    stackOffset--;
                    inStackRegisters.remove(stackOffset);
                }
                backend.getCompiler().addInstruction(new POP(register), "local variable is on top of stack");
            }
            else {
                inStackRegisters.set(localIndex, null);
                backend.getCompiler().addInstruction(new LOAD(virtualRegister.getDVal(), register), String.format("copy from stack R%d", currentRegisterIndex));
            }

            // mov from in stack to physical
            physicalRegisters[currentRegisterIndex] = virtualRegister;

            virtualRegister.setPhysical(register);

            currentRegisterIndex++;
        }
        else {
            currentRegisterIndex--;
            GPRegister register = GPRegister.getR(currentRegisterIndex);

            // no more register available, need to copy one to stack
            VirtualRegister oldRegister = physicalRegisters[currentRegisterIndex];

            // push register to stack
            stackOffset++;
            inStackRegisters.add(oldRegister);
            oldRegister.setInStack(stackOffset);
            backend.getCompiler().addInstruction(new PUSH((GPRegister) oldRegister.getDVal()), "no more GP register available");

            // mov from stack to physical
            physicalRegisters[currentRegisterIndex] = virtualRegister;
            inStackRegisters.set(virtualRegister.getLocalIndex(), null);

            // load into physical register
            backend.getCompiler().addInstruction(new LOAD(virtualRegister.getDVal(), register), String.format("Load from stack to R%d", currentRegisterIndex));

            virtualRegister.setPhysical(register);

            currentRegisterIndex++;
        }
    }

    public VirtualRegister requestNewRegister() {
        VirtualRegister register;
        if (currentRegisterIndex < usableRegistersCount) {
            // physical register available
            register = new VirtualRegister(this, GPRegister.getR(currentRegisterIndex));
            physicalRegisters[currentRegisterIndex] = register;
            currentRegisterIndex++;
        }
        else {
            // no more free register
            stackOffset++;
            register = new VirtualRegister(this, stackOffset);
            inStackRegisters.add(register);
        }
        return register;
    }

    public void freeRegister(VirtualRegister virtualRegister) {
        if (virtualRegister.getIsPhysicalRegister()) {
            physicalRegisters[((GPRegister) virtualRegister.getDVal()).getNumber()] = null;
        }
        else {
            inStackRegisters.set(virtualRegister.getLocalIndex(), null);
            while ((stackOffset > 0) && (inStackRegisters.get(stackOffset - 1) == null)) {
                stackOffset--;
                inStackRegisters.remove(stackOffset);
            }
        }
    }

    public int getStackOffset() {
        return stackOffset;
    }
}
