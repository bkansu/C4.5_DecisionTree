package com.bkansu.ml.c45decisiontree.model;

import java.util.List;

public class AttributeValue {
    private int value;
    private double information;
    private boolean isContinues = false ;
    private List<String> types;

    public AttributeValue(double information, List<String> types) {
        this.information = information;
        this.types = types;
    }

    public AttributeValue(int value, double information, boolean isContinues) {
        this.value = value;
        this.information = information;
        this.isContinues = isContinues;
    }

    public boolean isContinues() {
        return isContinues;
    }

    public void setContinues(boolean continues) {
        isContinues = continues;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getInformation() {
        return information;
    }

    public void setInformation(double information) {
        this.information = information;
    }
}
