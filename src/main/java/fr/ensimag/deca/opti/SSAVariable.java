package fr.ensimag.deca.opti;

public class SSAVariable {
    private final String name;
    private int id;

    public SSAVariable(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
