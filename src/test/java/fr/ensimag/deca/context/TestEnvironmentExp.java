package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;


/**
 * Classe de tests unitaires pour la classe EnvironmentExp.
 *
 * @author gl54
 * @date 10/01/2022
 */

public class TestEnvironmentExp {
    EnvironmentExp env0 = new EnvironmentExp(null);
    SymbolTable table = new SymbolTable();
    Symbol vrai =  table.create("true");
    Symbol bool = table.create("boolean");
    Symbol number = table.create("number");
    Symbol integer = table.create("int");
    Symbol floating = table.create("float");
    Symbol string = table.create("string");
    
    @Test
    public void testVarDef() throws DoubleDefException {
	EnvironmentExp env0 = new EnvironmentExp(null);
	EnvironmentType env1 = new EnvironmentType();
	TypeDefinition defInt = new TypeDefinition(new IntType(integer), Location.BUILTIN);
	VariableDefinition defNumber = new VariableDefinition(new IntType(integer), new Location(0,0,"test.deca"));
	try {
	    env0.declare(number, defNumber);
	    env1.declare(integer, defInt);
	} catch (DoubleDefException e) {
	    System.out.println(number + " : " + e);
	    System.exit(1);
	}
	//Recherche du symbole int dans l'environnement de niveau 0
	System.out.println(integer + " " + env1.get(integer)); //Doit afficher number et int
    
	//Recherche du symbole nombre dans l'environnement de niveau 0
	System.out.println(number.toString() + env0.get(number).toString()); //Doit afficher number et int
    }

    @Test
    public void chercheDef() throws DoubleDefException {
	EnvironmentExp env0 = new EnvironmentExp(null);
	VariableDefinition defTrue = new VariableDefinition(new BooleanType(bool), new Location(0,0,"test.deca"));
	try {
	    env0.declare(vrai, defTrue);
	} catch (DoubleDefException e) {
	    System.out.println(vrai + " : " + e);
	    System.exit(1);
	}
	
	EnvironmentExp env1 = new EnvironmentExp(env0);
	System.out.println(env1.get(vrai));
    }

    @Test
    public void chercheNonDefini() {
	EnvironmentExp env0 = new EnvironmentExp(null);
	//Recherche du symbole string non défini dans l'environnement :
	System.out.println(string.toString() + env0.get(string)); //Doit afficher string et null, puisque string n'est pas défini dans l'environnement
    }

    @Test
    public void redefinition() throws DoubleDefException {
	EnvironmentExp env0 = new EnvironmentExp(null);
	EnvironmentExp env1 = new EnvironmentExp(env0);
	VariableDefinition defNumber = new VariableDefinition(new IntType(integer), new Location(0,0,"test.deca"));
	try {
	    env0.declare(number, defNumber);
	} catch (DoubleDefException e) {
	    System.out.println(number + " : " + e);
	    System.exit(1);
	}

	VariableDefinition defNewNumber = new VariableDefinition(new BooleanType(floating), new Location(0, 0, "test.deca"));
	try {
	    env1.declare(number, defNewNumber);
	} catch (DoubleDefException e) {
	    System.out.println(number + " : " + e);
	    System.exit(1);
	}
	//Recherche du symbole nombre redéfini dans l'environnement de niveau 1
	System.out.println(number.toString() + env1.get(number).toString()); //Doit afficher number et float : la définition du niveau 0 a été cachée
	
	//Recherche du symbole nombre dans l'environnement de niveau 0
	System.out.println(number.toString() + env0.get(number).toString()); //Doit afficher number et int : la définition du niveau 0 a été seulement cachée et non supprimée
    }

    @Test
    public void recError() { 
	//Test d'une deuxième redéfinition de number dans l'evironnement de niveau 1
	EnvironmentExp env0 = new EnvironmentExp(null);
	VariableDefinition defNumber = new VariableDefinition(new IntType(integer), new Location(0,0,"test.deca"));
	VariableDefinition defErrorNumber = new VariableDefinition(new BooleanType(string), new Location(0, 0, "test.deca"));
	try {
	    env0.declare(vrai, defNumber);
	    //env0.declare(number, defErrorNumber);
	} catch (DoubleDefException e) {
	    //L'erreur est atteinte puisque number avait déjà été défini dans le niveau 1

	    System.out.println(number + " : " + e);
	}
    }
}
