package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Dictionary associating identifier's TypeDefinition to their names.
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary
 *
 * 
 * @author gl54
 * @date 01/01/2022
 */
public class EnvironmentType {

    private Map<Symbol, TypeDefinition> dico = new HashMap<Symbol, TypeDefinition>();

    public Map getMap(){return dico;}

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public TypeDefinition get(Symbol key) {
	return (dico.get(key));
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     */
    public void declare(Symbol name, TypeDefinition def) {
	dico.put(name, def);
    }

}
