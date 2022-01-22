package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import static fr.ensimag.deca.CompilerOptions.*;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl54
 * @date 01/01/2022
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) throws InterruptedException {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner() == BANNER_OK) {
            System.out.println("EQUIPE GL54 : ");
            System.out.println(" - WANG Caroline");
            System.out.println(" - HO-SUN Jules");
            System.out.println(" - FALGARYRAC Loïc");
            System.out.println(" - DIJS Thomas");
            System.out.println(" - Noiry Sylvain");
            System.exit(0);
        }
        else if (options.getPrintBanner() == BANNER_ERROR) {
            System.out.println("L'option -b s'utilise seule");
            options.displayUsage();
            System.exit(1);
        }
        if (options.getSourceFiles().isEmpty()) {
            System.out.println("Aucun fichier source spécifié");
            options.displayUsage();
            //throw new UnsupportedOperationException("decac without argument not yet implemented");
        }
        if (options.getParallel()) {
            List<ParallelCompiler> compilers = new ArrayList<>();
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                compilers.add(new ParallelCompiler(compiler));
            }

            ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for(ParallelCompiler compiler : compilers) {
                exec.execute(compiler);
            }

            exec.shutdown();
            if (!exec.awaitTermination(60, TimeUnit.SECONDS)) {
                error = true;
            }

            for(ParallelCompiler compiler : compilers) {
                if (compiler.getError()) {
                    error = true;
                    break;
                }
            }
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
