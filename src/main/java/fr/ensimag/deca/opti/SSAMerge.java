package fr.ensimag.deca.opti;

import java.util.HashSet;
import java.util.Set;

/**
 * class representing the Phi function in an SSA form control flow graph
 */
public class SSAMerge {
    private final Set<SSAVariable> operands;
    private final SSAVariable result;

    /**
     * constructor for SSAMerge
     * @param firstOperand the first input SSA variable
     * @param result the result SSA variable
     */
    public SSAMerge(SSAVariable firstOperand, SSAVariable result) {
        this.operands = new HashSet<>();
        this.result = result;
        this.operands.add(firstOperand);
    }

    /**
     * getter for operands
     * @return Phi function operands
     */
    public Set<SSAVariable> getOperands() {
        return operands;
    }

    /**
     * add an operand to the merge
     * @param variable SSA variable to merge
     */
    public void addOperand(SSAVariable variable) {
        operands.add(variable);
    }

    /**
     * remove operand from the merge
     * @param variable to remove from operands
     */
    public void removeOperand(SSAVariable variable) {
        operands.remove(variable);
    }

    /**
     * getter for Phi result SSA variable
     * @return result SSA variable
     */
    public SSAVariable getResult() {
        return result;
    }
}
