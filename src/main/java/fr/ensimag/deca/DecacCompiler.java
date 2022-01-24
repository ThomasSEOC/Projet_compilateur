package fr.ensimag.deca;

import fr.ensimag.deca.codegen.CodeGenBackend;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.invoke.MethodType;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;
import fr.ensimag.deca.tools.SymbolTable;
//import java.util.HashMap;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.context.DoubleDefException;


/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl54
 * @date 01/01/2022
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    private SymbolTable symbolTable = new SymbolTable();

    private EnvironmentType envTypesPredef = new EnvironmentType();
    private EnvironmentExp envExpPredef = new EnvironmentExp(null);
    private EnvironmentType envTypes = new EnvironmentType();

    // Getters
    public EnvironmentType getTypesPredef() {
        return envTypesPredef;
    }
    public EnvironmentExp getExpPredef(){
        return envExpPredef;
    }
    public EnvironmentType getTypes() {
        return envTypes;
    }

    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");


    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;
        this.codeGenBackend = new CodeGenBackend(this);


        // symbols predefined
        symbolTable.create("void");
        symbolTable.create("boolean");
        symbolTable.create("float");
        symbolTable.create("int");
        symbolTable.create("Object");
        symbolTable.create("equals");

        // Definitions of the predef types
        TypeDefinition booleanDef = new TypeDefinition(new BooleanType(symbolTable.getMap().get("boolean")), Location.BUILTIN);
        TypeDefinition voidDef = new TypeDefinition(new VoidType(symbolTable.getMap().get("void")), Location.BUILTIN);
        TypeDefinition floatDef = new TypeDefinition(new FloatType(symbolTable.getMap().get("float")), Location.BUILTIN);
        TypeDefinition intDef = new TypeDefinition(new IntType(symbolTable.getMap().get("int")), Location.BUILTIN);


        // Definition for the class Object
        ClassType objectType =  new ClassType(symbolTable.getMap().get("Object"), Location.BUILTIN, null);
        ClassDefinition objectDef = objectType.getDefinition();

        // Creation of the equals method
        Signature equalsSignature = new Signature();
        equalsSignature.add(objectType);
        Type returnType = new BooleanType(symbolTable.getMap().get("boolean"));
        objectDef.incNumberOfMethods();
        MethodDefinition equals = new MethodDefinition(returnType, Location.BUILTIN, equalsSignature, 1);
        try {
            objectDef.getMembers().declare(symbolTable.create("equals"), equals);
        } catch (DoubleDefException e) {}


        // Declare in the envTypePredef and envTypes
        try {
            envTypesPredef.declare(symbolTable.getSymbol("void"), voidDef);
            envTypes.declare(symbolTable.getSymbol("void"), voidDef);
        } catch (DoubleDefException e) {}

        try {
            envTypesPredef.declare(symbolTable.getSymbol("boolean"), booleanDef);
            envTypes.declare(symbolTable.getSymbol("boolean"), booleanDef);
        } catch (DoubleDefException e) {}

        try {
            envTypesPredef.declare(symbolTable.getSymbol("float"), floatDef);
            envTypes.declare(symbolTable.getSymbol("float"), floatDef);
        } catch (DoubleDefException e) {}

        try {
            envTypesPredef.declare(symbolTable.getSymbol("int"), intDef);
            envTypes.declare(symbolTable.getSymbol("int"), intDef);
        } catch (DoubleDefException e) {}

        try {
            envTypesPredef.declare(symbolTable.getSymbol(("Object")), objectDef);
            envTypes.declare(symbolTable.getSymbol(("Object")), objectDef);
        } catch (DoubleDefException e) {}
    }




    public SymbolTable getSymbolTable(){
        return symbolTable;
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        program.addInstruction(instruction);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        program.addInstruction(instruction, comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }

    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();

    private final CodeGenBackend codeGenBackend;

    /**
     * getter for code generation backend
     * @return codeGenBackend
     */
    public CodeGenBackend getCodeGenBackend() {
        return codeGenBackend;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Instruction)
     * added by gl54
     */
    public void addFirst(Instruction instruction) {
        program.addFirst(instruction);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     * added by gl54
     */
    public void addFirst(Instruction instruction, String comment) {
        program.addFirst(instruction, comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addFirst(java.lang.String)
     * added by gl54
     */
    public void addFirst(String comment) {
        program.addFirst(comment);
    }

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        // le fichier de sortie est créé par défaut au même endroit que le fichier source, mais avec l'extension .ass
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.replaceFirst("[.][^.]+$", "") + ".ass";
        // A FAIRE: calculer le nom du fichier .ass à partir du nom du
        // A FAIRE: fichier .deca.
        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
                              PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }

        if (compilerOptions.getCompilerStages() == CompilerOptions.PARSE_ONLY) {
            System.out.println(prog.decompile());
            return false;
        }

        assert(prog.checkAllLocations());
        prog.verifyProgram(this);
        assert(prog.checkAllDecorations());

        if (compilerOptions.getCompilerStages() == CompilerOptions.PARSE_AND_VERIF) {
            return false;
        }

        prog.codeGenProgram(this);
        addComment("end main program");
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");

        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");

        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     * compiler.
     * @throws LocationException When a compilation error (incorrect program)
     * occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

}
