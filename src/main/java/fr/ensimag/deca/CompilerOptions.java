package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static java.lang.Integer.parseInt;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl54
 * @date 01/01/2022
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    public static final int PARSE_ONLY = 1;
    public static final int PARSE_AND_VERIF = 2;
    public static final int ALL = 3;

    public static final int NO_BANNER = 0;
    public static final int BANNER_OK = 1;
    public static final int BANNER_ERROR = 2;

    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public int getPrintBanner() {
        return printBanner;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public boolean getNoCheckStatus() { return noCheck; }

    public int getRegistersCount() { return registersCount; }

    public int getCompilerStages() { return compilerStages; }

    public int getOptimize() { return optimize; }

    public boolean getCreateGraphFile() { return createGraphFile; }

    private int debug = 0;
    private boolean parallel = false;
    private int printBanner = 0;
    private List<File> sourceFiles = new ArrayList<File>();
    private int compilerStages = 3;
    private boolean noCheck = false;
    private int registersCount = 16;
    private int optimize = 0;
    private boolean createGraphFile = false;
    
    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                // banner
                case "-b":
                    printBanner = 1;
                    if (args.length > 1) {
                        printBanner = 2;
                    }
                    break;
                // parse only
                case "-p":
                    if (compilerStages == 2) {
                        throw new CLIException("les options -p et -v sont incompatibles en elles");
                    }
                    compilerStages = 1;
                    break;
                // stop after verification
                case "-v":
                    if (compilerStages == 1) {
                        throw new CLIException("les options -p et -v sont incompatibles en elles");
                    }
                    compilerStages = 2;
                    break;
                // no check
                case "-n":
                    noCheck = true;
                    break;
                // specify max registers number
                case "-r":
                    i++;
                    if (i == args.length) {
                        throw new CLIException("Merci d'indiquer un nombre de registres");
                    }
                    int value = parseInt(args[i]);
                    if ((value >= 4) && (value <= 16)) {
                        registersCount = value;
                    }
                    else {
                        throw new CLIException("le nombre de registre doit être compris entre 4 et 16 inclus");
                    }
                    break;
                // debug
                case "-d":
                    if (debug < 3) {
                        debug++;
                    }
                    break;
                // parallel
                case "-P":
                    parallel = true;
                    break;
                // parallel
                case "-O0":
                    optimize = 0;
                    break;
                case "-O1":
                    optimize = 1;
                    break;
                case "-O2":
                    optimize = 2;
                    break;
                case "-O2g":
                    optimize = 2;
                    createGraphFile = true;
                    break;
                default:
                    // ce doit être un fichier
                    sourceFiles.add(new File(args[i]));
                    break;
            }
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

        //throw new UnsupportedOperationException("not yet implemented");
    }

    protected void displayUsage() {
        // usage copié du sujet
        System.out.println("Usage: decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        //throw new UnsupportedOperationException("not yet implemented");
    }
}
