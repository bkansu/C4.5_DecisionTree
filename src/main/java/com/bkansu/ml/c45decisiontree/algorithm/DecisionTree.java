package com.bkansu.ml.c45decisiontree.algorithm;

import com.bkansu.ml.c45decisiontree.model.AttributeValue;
import com.bkansu.ml.c45decisiontree.model.Node;
import com.bkansu.ml.c45decisiontree.util.CsvUtils;

import java.io.IOException;
import java.util.*;

public class DecisionTree {

    public Node calculateTree() throws IOException {

        CsvUtils csvUtils = new CsvUtils();
        String[][] instances = csvUtils.getInstances();
        String[] attributes = csvUtils.getAttributes();
        Map<Integer, Integer> isContinues = new HashMap<>();/* Map formatinda tuttugumuz degerlerden ilk integer kacinci sutundaki degeroldugu, ikinci integer ise continues value olup olmadigini tutuyor. */
        for (int i = 0; i < attributes.length - 1; i++) {
            isContinues.put(i, 0);
        }
        isContinues.put(1, 1);
        isContinues.put(4, 1);

        int treeDepth = 0;/* Burada on budama islemi icin kullanacagımız agac derinligi degerri icerideartırılmak uzere 0 olarak tanımlanıyor. */
        Node node = prepareNode(instances, isContinues, treeDepth);

        return node;

    }

    private Node prepareNode(String[][] instances, Map<Integer, Integer> isContinues, int treeDepth) {
        List<AttributeValue> attributeValues = getAttributeValues(instances, isContinues);
        int yalanDex = getMaxInformationGainIndex(instances, attributeValues);
        AttributeValue attributeValue = attributeValues.get(yalanDex);
        int index = (int) isContinues.keySet().toArray()[yalanDex];
        Node node;
        if (isContinues.size() == 1 || treeDepth > 2 || instances.length == 1) {
            /* Bu kosul leaf node yapma kosuludur ve buradaki treeDepth BUDAMA isleminde kullanılmıştır.
          Eger agacderinligi 2'den fazla ise orayı leaf node yapar ve daha fazla dallanmasını engeller.
          TREEDEPTH'in buyuk olma kosulu degistirildiginde "ACCURACY ve TP-TN RATE" degerlerindeki degisim gozlemlenmektedir. */
            node = prepareLeafNode(instances, index, attributeValue);
        } else {
            HashMap<Integer, Integer> newIsContinues = new HashMap<>(isContinues);
            newIsContinues.remove(index);/* fonksiyon rekürsif calistigi icin isContinues listesini guncelleyerek gonderiyoruz. */

            List<Node> childs = new ArrayList<>();
            if (!attributeValue.isContinues()) {
                double min = Double.MAX_VALUE;
                int mindex = 0;
                double minInformationGainIndex;
                for (int i = 0; i < attributeValue.getTypes().size(); i++) {
                    String[][] newInstances = filterInstances(instances, index, attributeValue.getTypes().get(i));
                    List<AttributeValue> attributeValues1 = getAttributeValues(newInstances, newIsContinues);
                    minInformationGainIndex = getMinInformationGainIndex(newInstances, attributeValues1);

                    if (min < minInformationGainIndex) {
                        min = minInformationGainIndex;
                        mindex = i;
                    }

                }
                /* Yukaridaki kosullarda information gain hesabı yapıldı ve information degeri min olan deger dondu. Cunku information degeri min olanın information gaini max  olur. */
                for (int i = 0; i < attributeValue.getTypes().size(); i++) {
                    if (i == mindex) continue;
                    String[][] newInstances = filterInstances(instances, index, attributeValue.getTypes().get(i));
                    /* information gaini max olan degerin tum distnict degerleri new node olarak eklendi ve agac derinligi bir arttırıldı. */
                    childs.add(prepareNode(newInstances, newIsContinues, treeDepth + 1));
                }


            } else {
                /* Continues degerler icin 2 node ekleneceginden ikisi de ayrı ayrı hesaplanıp eklendi. */
                String[][] newInstancesLower = filterInstancesForContinues(instances, index, attributeValue.getValue(), false);
                String[][] newInstancesUpper = filterInstancesForContinues(instances, index, attributeValue.getValue(), true);
                childs.add(prepareNode(newInstancesLower, newIsContinues, treeDepth + 1));
                childs.add(prepareNode(newInstancesUpper, newIsContinues, treeDepth + 1));
            }
            node = new Node(index, attributeValue, childs);
        }
        return node;
    }

    private double getMinInformationGainIndex(String[][] instances, List<AttributeValue> attributeValues) {
        double max = Double.MAX_VALUE;
        double entropy = getEntropy(instances);

        for (int i = 0; i < attributeValues.size(); i++) {
            double informationGain = entropy - attributeValues.get(i).getInformation();
            if (informationGain < max) {
                max = informationGain;
            }
        }
        return max;
        /*  information gaini min olan degerin information gainini doner. */
    }

    private String[][] filterInstancesForContinues(String[][] instances, int index, int value, boolean flag) {
      /* Continues degerler icin veriyi duzenleyen fonksiyon */
        List<String[]> newInstances = new ArrayList<>();
        for (int i = 0; i < instances.length; i++) {
            if (!flag) {
                if (Integer.parseInt(instances[i][index]) <= value) {
                    newInstances.add(instances[i]);
                }

            } else {
                if (Integer.parseInt(instances[i][index]) > value) {
                    newInstances.add(instances[i]);
                }
            }
        }
        return newInstances.toArray(new String[0][]);

    }

    private String[][] filterInstances(String[][] instances, int index, String type) {
        /* Continues olmayan degerler icin veriyi duzenleyen fonksiyon */

        List<String[]> newInstances = new ArrayList<>();
        for (int i = 0; i < instances.length; i++) {
            if (instances[i][index].equals(type)) {
                newInstances.add(instances[i]);
            }
        }
        return newInstances.toArray(new String[0][]);
    }

    private Node prepareLeafNode(String[][] instances, int attributeIndex, AttributeValue attributeValue) {
        /* Leaf node olusuran fonksiyon */
        List<String> results = new ArrayList<>();
        String result = instances[0][instances[0].length - 1];
        if (attributeValue.isContinues()) {
            if (Integer.parseInt(instances[0][attributeIndex]) > attributeValue.getValue()) {
                results.add(0, result.equals("good") ? "bad" : "good");
                results.add(1, result);
            } else {
                results.add(0, result);
                results.add(1, result.equals("good") ? "bad" : "good");
            }
        } else {
            for (int i = 0; i < attributeValue.getTypes().size(); i++) {
                results.add(i, getResultForType(instances, attributeIndex, attributeValue.getTypes().get(i)));
            }
        }

        return new Node(attributeIndex, attributeValue, results, true);
    }

    private String getResultForType(String[][] instances, int index, String type) {
        /* Continues olmayan degerlerin distnict typelari icin result donen fonksiyon */
        for (int i = 0; i < instances.length; i++) {
            if (instances[i][index].equals(type)) {
                return instances[i][instances[0].length - 1];
            }
        }
        return null;
    }

    private int getMaxInformationGainIndex(String[][] instances, List<AttributeValue> attributeValues) {
        /* information gain hesabini tum attributelaricin yaparak max informatin gaini olan attribute'ın indexini donen fonksion */
        double max = Double.MIN_VALUE;
        double entropy = getEntropy(instances);
        int index = 0;

        for (int i = 0; i < attributeValues.size(); i++) {
            double informationGain = entropy - attributeValues.get(i).getInformation();
            if (informationGain > max) {
                max = informationGain;
                index = i;
            }
        }
        return index;
    }

    private List<AttributeValue> getAttributeValues(String[][] instances, Map<Integer, Integer> isContinues) {
        List<AttributeValue> attributeValues = new ArrayList<>();
        for (Integer index : isContinues.keySet()) {
            attributeValues.add(isContinues.get(index) == 0 ?
                    getInformationForDiscreteAttr(instances, index) :
                    getInformationForContinuesAttr(instances, index));
        }

        return attributeValues;
    }


    private AttributeValue getInformationForContinuesAttr(String[][] instances, int attributeIndex) {
        /* Continues deferler icin information hesabi yapan fonksiyon */
        int[][] sortedMatrix = getSortedMatrix(instances, attributeIndex);
        double min = Double.MAX_VALUE;
        int value = 0;
        for (int i = 0; i < sortedMatrix.length; i++) {
            double informationValueOf = getInformationValueOf(sortedMatrix, sortedMatrix[i][0]);
            if (informationValueOf < min) {
                min = informationValueOf;
                value = sortedMatrix[i][0];
            }
        }

        return new AttributeValue(value, min, true);
    }

    private int[][] getSortedMatrix(String[][] instances, int attributeIndex) {
        /* Continues deferler icin gerekli olan siralanmis matrisi donen fonksiyon */
        int[][] sortedMatrix = new int[instances.length][2];
        for (int i = 0; i < instances.length; i++) {
            sortedMatrix[i][0] = Integer.parseInt(instances[i][attributeIndex]);
            sortedMatrix[i][1] = (instances[i][instances[0].length - 1]).equals("good") ? 1 : 0;
        }
        Arrays.sort(sortedMatrix, Comparator.comparingInt(o -> o[0]));
        return sortedMatrix;
    }

    private AttributeValue getInformationForDiscreteAttr(String[][] instances, int attributeIndex) {
        /* Continues olmayan (discrete) attributelar icin information hesabi yapan fonksiyon */
        List<String> types = getTypes(instances, attributeIndex);
        double information = 0;
        for (int i = 0; i < types.size(); i++) {
            information += getInformationValueOf(instances, types.get(i), attributeIndex);
        }
        return new AttributeValue(information, types);
    }

    private List<String> getTypes(String[][] instances, int attributeIndex) {
        List<String> types = new ArrayList<>();
        for (int i = 0; i < instances.length; i++) {
            if (!types.contains(instances[i][attributeIndex])) {
                types.add(instances[i][attributeIndex]);
            }
        }
        return types;
    }

    private double getInformationValueOf(String[][] instances, String type, int attributeIndex) {
        int totalCount = 0;
        int goodCount = 0;
        for (int i = 0; i < instances.length; i++) {
            if (instances[i][attributeIndex].equals(type)) {
                if (instances[i][instances[0].length - 1].equals("good")) {
                    goodCount++;
                }

                totalCount++;
            }

        }
        int badCount = totalCount - goodCount;
        return totalCount / (double) instances.length * calculateEntropyFormula(goodCount, badCount, totalCount);
    }

    private double getInformationValueOf(int[][] instances, int selectedElement) {
        int totalCountForLower = 0;
        int goodCountForLower = 0;
        int totalCountForUpper = 0;
        int goodCountForUpper = 0;
        for (int i = 0; i < instances.length; i++) {
            if (instances[i][0] <= selectedElement) {
                if (instances[i][1] == 1) {
                    goodCountForLower++;
                }

                totalCountForLower++;
            } else {
                if (instances[i][1] == 1) {
                    goodCountForUpper++;
                }
                totalCountForUpper++;

            }

        }
        int badCountForUpper = totalCountForUpper - goodCountForLower;
        int badCountForLower = totalCountForLower - goodCountForLower;
        double lower = totalCountForLower / (double) instances.length * calculateEntropyFormula(goodCountForLower, badCountForLower, totalCountForLower);
        double upper = totalCountForUpper / (double) instances.length * calculateEntropyFormula(goodCountForUpper, badCountForUpper, totalCountForUpper);
        return lower + upper;
    }

    private double getEntropy(String[][] instances) {
        int goodCount = 0;

        for (String[] instance : instances) {
            if (instance[instances[0].length - 1].equals("good")) {
                goodCount++;
            }
        }
        int badCount = instances.length - goodCount;

        return calculateEntropyFormula(goodCount, badCount, instances.length);
    }

    private double calculateEntropyFormula(int goodCount, int badCount, int totalCount) {
        if (goodCount == totalCount) return 1;
        if (goodCount == 0) return 0;
        return -1 * (goodCount / (double) totalCount) * (Math.log((goodCount / (double) totalCount)) / Math.log(2)) - 1 * (badCount / (double) totalCount) * (Math.log((badCount / (double) totalCount)) / Math.log(2));
    }

}
