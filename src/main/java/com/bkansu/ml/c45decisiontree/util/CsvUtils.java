package com.bkansu.ml.c45decisiontree.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class CsvUtils {/* disaridan alinacak data'larin bir matrise yazilmasi isini yapan class. */
    private List<String> getLines(String path) throws IOException {
        return Files.readAllLines(new File(path).toPath());
    }
    public String[][] getInstances() throws IOException {
        List<String> lines = getLines(".\\src\\main\\resources\\trainSet.csv");
        lines.remove(0);
        String[][] instences = new String[lines.size()][lines.get(0).split(",").length];
        for (int i = 0; i < lines.size(); i++) {

            String[] split = lines.get(i).split(",");
            instences[i]=split;
        }
        return instences;
    }

    public String[][] getTestInstances() throws IOException {
        List<String> lines = getLines(".\\src\\main\\resources\\testSet.csv");
        lines.remove(0);
        String[][] instences = new String[lines.size()][lines.get(0).split(",").length];
        for (int i = 0; i < lines.size(); i++) {

            String[] split = lines.get(i).split(",");
            instences[i]=split;
        }
        return instences;
    }

    public String[] getAttributes() throws IOException {
        List<String> lines = getLines(".\\src\\main\\resources\\trainSet.csv");
        String[][] instences = new String[lines.size()][lines.get(0).split(",").length];
        String remove = lines.remove(0);
        return remove.split(",");

    }
}
