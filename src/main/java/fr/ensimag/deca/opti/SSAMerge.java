package fr.ensimag.deca.opti;

import java.util.HashSet;
import java.util.Set;

public class SSAMerge {
    private final Set<SSAVariable> operands;
    private final SSAVariable result;

    public SSAMerge(SSAVariable firstOperand, SSAVariable result) {
        this.operands = new HashSet<>();
        this.result = result;
        this.operands.add(firstOperand);
    }

    public Set<SSAVariable> getOperands() {
        return operands;
    }

    public void addOperand(SSAVariable variable) {
        operands.add(variable);
    }

    public SSAVariable getResult() {
        return result;
    }
}
