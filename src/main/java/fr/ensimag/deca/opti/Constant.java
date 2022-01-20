package fr.ensimag.deca.opti;

import sun.security.provider.certpath.OCSP;

public class Constant {
    private boolean isFloat;
    private boolean isBool;
    private int valueInt;
    private float valueFloat;
    private boolean valueBool;

    public Constant(int value) {
        isFloat = false;
        valueInt = value;
    }

    public Constant(float value) {
        isFloat = true;
        valueFloat = value;
    }
    
    public Constant(boolean value) {
        isBool = true;
	isFloat = false;
	valueBoolean = value;
    }

    public boolean getIsFloat() {
        return isFloat;
    }

    public int getValueInt() {
        return valueInt;
    }

    public float getValueFloat() {
        return valueFloat;
    }
}
