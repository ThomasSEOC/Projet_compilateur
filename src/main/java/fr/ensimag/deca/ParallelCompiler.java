package fr.ensimag.deca;

public class ParallelCompiler implements Runnable {
    private final DecacCompiler compiler;
    private boolean error = false;

    public ParallelCompiler(DecacCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public void run() {
        if (compiler.compile()) {
            error = true;
        }
    }

    public boolean getError() {
        return error;
    }
}
