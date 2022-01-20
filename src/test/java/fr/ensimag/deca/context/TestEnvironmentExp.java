package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

/**
 * Classe de tests unitaires pour la classe EnvironmentExp.
 *
 * @author gl54
 * @date 10/01/2022
 */ 

public class TestEnvironmentExp {
    public static void main(String[] args) {

	EnvironmentExp env0 = new EnvironmentExp(null);
	SymbolTable table = new SymbolTable();
	Symbol vrai =  table.create("true");
	Symbol bool = table.create("boolean");
        Symbol number = table.create("number");
	Symbol integer = table.create("int");
	Symbol floating = table.create("float");
	Symbol string = table.create("string");
	
	VariableDefinition defNumber = new VariableDefinition(new BooleanType(integer), new Location(0,0,"test.deca"));
	try {
	    env0.declare(number, defNumber);
	} catch (DoubleDefException e) {
	    System.out.println(number + " : " + e);
	    System.exit(1);
	}
	System.out.println("Recherche du symbole nombre dans l'environnement de niveau 0 :");
	System.out.println(number + " : " + env0.get(number)); //Doit afficher number et int

	VariableDefinition defTrue = new VariableDefinition(new BooleanType(bool), new Location(0,0,"test.deca"));
	try {
	    env0.declare(vrai, defTrue);
	} catch (DoubleDefException e) {
	    System.out.println(vrai + " : " + e);
	    System.exit(1);
	}
	
	EnvironmentExp env1 = new EnvironmentExp(env0);
	System.out.println("\n\nRecherche du symbole vrai défini au niveau dans l'environnement de niveau 1 :");
	System.out.println(vrai + " : " + env1.get(vrai)); //Doit afficher vrai et boolean ; vrai est cherché à partir du niveau 1 et est trouvé dans le niveau 0

	System.out.println("\n\nRecherche du symbole string non défini dans l'environnement :");
	System.out.println(string + " : " + env1.get(string)); //Doit afficher string et null, puisque string n'est pas défini dans l'environnement
			   
	VariableDefinition defNewNumber = new VariableDefinition(new BooleanType(floating), new Location(0, 0, "test.deca"));
	try {
	    env1.declare(number, defNewNumber);
	} catch (DoubleDefException e) {
	    System.out.println(number + " : " + e);
	    System.exit(1);
	}
	System.out.println("\n\nRecherche du symbole nombre redéfini dans l'environnement de niveau 1 :");
	System.out.println(number + " : " + env1.get(number)); //Doit afficher number et float : la définition du niveau 0 a été cachée
	
	System.out.println("\n\nRecherche du symbole nombre dans l'environnement de niveau 0 :");
	System.out.println(number + " : " + env0.get(number)); //Doit afficher number et int : la définition du niveau 0 a été seulement cachée et non supprimée

	System.out.println("\n\nTest d'une deuxième redéfinition de number dans l'evironnement de niveau 1 :");
	VariableDefinition defErrorNumber = new VariableDefinition(new BooleanType(string), new Location(0, 0, "test.deca"));
	try {
	    env1.declare(number, defErrorNumber);
	} catch (DoubleDefException e) {
	    //L'erreur est atteinte puisque number avait déjà été défini dans le niveau 1
	    System.out.println(number + " : " + e);
	    System.exit(1);
	}	
	
    }
}
