package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl54
 * @date 01/01/2022
 */
public class EnvironmentExp {
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).

    private Map<Symbol, ExpDefinition> dico = new HashMap<Symbol, ExpDefinition>();

    EnvironmentExp parentEnvironment; //Superclass
    
    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        //permet de conserver la hiérarchie
        this.parentEnvironment = parentEnvironment;
    }

    public static class DoubleDefException extends Exception {
	
        private static final long serialVersionUID = -2733379901827316441L;
	public DoubleDefException(String message) {
	    super(message);
	}
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public ExpDefinition get(Symbol key) {
        if (dico.containsKey(key)) {
            return (dico.get(key));
        }

	//Condition d'arrêt
        if (parentEnvironment == null){
            return null;
        }
	
        return parentEnvironment.get(key); //On choisit une fonction récursive
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
	if (dico.containsKey(name)) {
	    throw new DoubleDefException("Arleady defined");
	}
	
        dico.put(name, def);
        return;
    }

}
