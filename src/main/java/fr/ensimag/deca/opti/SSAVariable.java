package fr.ensimag.deca.opti;

/**
 * class representing a variable with SSA decoration
 */
public class SSAVariable {
    private final String name;
    private int id;

    /**
     * getter for SSAVariable
     * @param name variable name
     * @param id unique SSA ID
     */
    public SSAVariable(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * getter for name
     * @return variable name
     */
    public String getName() {
        return name;
    }

    /**
     * getter for id
     * @return variable unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * setter for id
     * @param id variable unique ID
     */
    public void setId(int id) {
        this.id = id;
    }
}
