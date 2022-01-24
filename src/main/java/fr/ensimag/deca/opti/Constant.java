package fr.ensimag.deca.opti;

/**
 * class used to represent a constant created with constant folding
 */
public class Constant {
    private final boolean isFloat;
    private final boolean isBool;
    private int valueInt;
    private float valueFloat;
    private boolean valueBool;

    /**
     * constructor for integer Constant
     * @param value integer value
     */
    public Constant(int value) {
        isFloat = false;
        isBool = false;
        valueInt = value;
    }

    /**
     * constructor for float Constant
     * @param value float value
     */
    public Constant(float value) {
        isFloat = true;
        isBool = false;
        valueFloat = value;
    }

    /**
     * constructor for boolean Constant
     * @param value boolean value
     */
    public Constant(boolean value) {
        isBool = true;
        isFloat = false;
        valueBool = value;
    }

    /**
     * getter for isFloat
     * @return true if value is a float
     */
    public boolean getIsFloat() {
        return isFloat;
    }

    /**
     * getter for isBool
     * @return true if value is boolean
     */
    public boolean getIsBoolean() {
        return isBool;
    }

    /**
     * getter fr integer value
     * @return integer value
     */
    public int getValueInt() {
        return valueInt;
    }

    /**
     * getter for float value
     * @return float value
     */
    public float getValueFloat() {
        return valueFloat;
    }

    /**
     * getter for boolean value
     * @return boolean value
     */
    public boolean getValueBoolean() {
        return valueBool;
    }
}
