package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;

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

    EnvironmentExp parentEnvironment; //Superclass

    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
    }

    private Map<Symbol, ExpDefinition> dico = new HashMap<Symbol, ExpDefinition>();

    public Map<Symbol, ExpDefinition> getDico() {
        return dico;
    }

    public void initDico(Map<Symbol, ExpDefinition> dico) {
        this.dico = new HashMap<Symbol, ExpDefinition>(dico);
    }


    public void addDico(Map<Symbol, ExpDefinition> dicoAdd) {
        for (Symbol key : dicoAdd.keySet()){
            dico.put(key,dicoAdd.get(key));
        }

    }
    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public ExpDefinition get(Symbol key) {

        // Symbol already in the dictionary
        if (dico.containsKey(key)) {
            return (dico.get(key));
        }
        // If the dictionary doesn't have any parent
        if (parentEnvironment == null){
            return null;
        }

        //Recursion : search the symbol in the parent dictionary
        return parentEnvironment.get(key);
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
	    throw new DoubleDefException(name + " is already defined", dico.get(name).getLocation());
	}
        dico.put(name, def);
    }

    public void declareforce(Symbol name, ExpDefinition def)  {
        dico.put(name, def);
    }

}
