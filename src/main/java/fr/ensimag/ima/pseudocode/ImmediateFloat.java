package fr.ensimag.ima.pseudocode;

/**
 * Immediate operand containing a float value.
 * 
 * @author Ensimag
 * @date 01/01/2022
 */
public class ImmediateFloat extends DVal {
    private float value;

    public ImmediateFloat(float value) {
        super();
        this.value = value;
    }
    
    public float getValue() {
	return value;
    }

    @Override
    public String toString() {
        return "#" + Float.toHexString(value);
    }
}
