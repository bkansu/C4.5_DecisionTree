package com.bkansu.ml.c45decisiontree.model;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int attributeIndex;
    private AttributeValue attributeValue;
    private List<Node> childs;
    private List<String> results;
    private boolean isLeaf;

    public Node(int attributeIndex, AttributeValue attributeValue, List<String> results, boolean isLeaf) {
        this.attributeIndex = attributeIndex;
        this.attributeValue = attributeValue;
        this.results = results;
        this.isLeaf = isLeaf;
    }

    public Node(int attributeIndex, AttributeValue attributeValue, List<Node> childs) {
        this.attributeIndex = attributeIndex;
        this.attributeValue = attributeValue;
        this.childs = childs;
    }

    public Node getNextChild(String[] instance) {
        if (attributeValue.isContinues()) {
            Integer value = Integer.valueOf(instance[attributeIndex]);
            if (value > attributeValue.getValue()) {
                return childs.get(1);
            }
            return childs.get(0);

        } else {
            int index = attributeValue.getTypes().indexOf(instance[attributeIndex]);
            if(index==-1){
                ArrayList<String> results = new ArrayList<>();
               attributeValue.getTypes().add(instance[attributeIndex]);
                for (int i = 0; i < attributeValue.getTypes().size(); i++) {
                    results.add("bad");
                }
                return new Node(attributeIndex,attributeValue, results,true);
            }
            return childs.get(index);
        }
    }

    public int getAttributeIndex() {
        return attributeIndex;
    }

    public void setAttributeIndex(int attributeIndex) {
        this.attributeIndex = attributeIndex;
    }

    public List<Node> getChilds() {
        return childs;
    }

    public void setChilds(List<Node> childs) {
        this.childs = childs;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public AttributeValue getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(AttributeValue attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getResult(String[] instance) {
        if (attributeValue.isContinues()) {
            Integer value = Integer.valueOf(instance[attributeIndex]);
            if (value > attributeValue.getValue()) {
                return results.get(1);
            }
            return results.get(0);

        } else {
            int index = attributeValue.getTypes().indexOf(instance[attributeIndex]);
            if(index==-1){
                return  "bad";
            }
            return results.get(index);
        }
    }

    private <T> T getResult(String[] instance, List<T> results) {
        if (attributeValue.isContinues()) {
            Integer value = Integer.valueOf(instance[attributeIndex]);
            if (value > attributeValue.getValue()) {
                return results.get(1);
            }
            return results.get(0);

        } else {
            int index = attributeValue.getTypes().indexOf(instance[attributeIndex]);
            if(index==-1){
                return (T) "good";
            }
            return results.get(index);
        }
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    @Override
    public String toString() {
        String str= new String("");
        String childStr = "";
        if(!attributeValue.isContinues()&&isLeaf){
            for (int i = 0; i < attributeValue.getTypes().size(); i++) {
                str+="\n\t="+attributeValue.getTypes().get(i)+"\t"+results.get(i);
            }
        }
        if(!isLeaf){

            for (int i = 0; i < childs.size(); i++) {
                childStr+="\n"+childs.get(i).toString();
            }
        }
        return
                    //"attributeIndex=" + attributeIndex +
                            (isLeaf?
                                    "\n\t" + (attributeValue.isContinues()? attributeValue.getValue()+"\n\t< "+results.get(0)+"\n\t> "+results.get(1):str) :
                                    "\n\t" + childStr);

    }
}
