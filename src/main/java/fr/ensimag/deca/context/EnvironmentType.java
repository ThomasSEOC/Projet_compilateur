package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
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

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public TypeDefinition get(Symbol key) {
	    return (dico.get(key));
    }

    public Map<Symbol, TypeDefinition> getDico() {
        return dico;
    }


    public void cloneMapExp (Map<Symbol, ExpDefinition> dicoRecep) {
        dicoRecep = (HashMap<SymbolTable.Symbol, ExpDefinition>)(((HashMap<SymbolTable.Symbol, TypeDefinition>)dico).clone());
    }

    public HashMap<SymbolTable.Symbol, TypeDefinition> cloneMapType () {
        return ((HashMap<SymbolTable.Symbol, TypeDefinition>)(((HashMap<SymbolTable.Symbol, TypeDefinition>)dico).clone()));
    }
    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     */
    public void declare(Symbol name, TypeDefinition def) throws EnvironmentExp.DoubleDefException {
        if (dico.containsKey(name)) {
            throw new EnvironmentExp.DoubleDefException(name + " is arleady defined at " + dico.get(name).getLocation(), def.getLocation());

        }
	    dico.put(name, def);
    }

}
