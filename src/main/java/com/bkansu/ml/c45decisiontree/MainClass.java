package com.bkansu.ml.c45decisiontree;

import com.bkansu.ml.c45decisiontree.algorithm.DecisionTree;
import com.bkansu.ml.c45decisiontree.algorithm.TreePanel;
import com.bkansu.ml.c45decisiontree.model.Node;
import com.bkansu.ml.c45decisiontree.util.CsvUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainClass {
    public static void main(String[] args) throws IOException {

        /* Programin tamaminda (agacin hesaplanmasi,olusturulmasi,ekrana cizdirilmesi,budama isleminin yapilmasi vs.) herhangi bir hazir fonksiyon, kutuphane vs. kullanilmamistir. */

        Node root = new DecisionTree().calculateTree();/* Agaci gezmek icin agacin root'unu DecisionTree classındaki calculateTree fonksiyonundan alıyoruz. */
        String[][] testInstances = new CsvUtils().getTestInstances();/* Test verisini bir degiskene cektik */
        printTree(root);/* asagidaki dongude agacin kok degerini kaybedecegimiz icin onde agacı yazdiriyoruz.*/

        int tp=0,tn=0,fp=0,fn=0;
        for (int i = 0; i < testInstances.length; i++) {

            while(!root.isLeaf()){

                root=root.getNextChild(testInstances[i]);
            }
            String result = root.getResult(testInstances[i]);/* result degeri test verisinin karar agacina girerek donderdigi degerdir. */
            String realResult = testInstances[i][5];  /* realResult degeri verilen test verisindeki sinif degiskeninin gercek degeridir. */
            if(realResult.equals("good")){/* Buradaki sorgular tamamen result ve realResult degerlerine bakarak true positive, true negative gibi degerleri hesaplar.*/
                if(result.equals("good")){
                    tp++;
                }
                else {
                    fn++;
                }
            }
            else{
                if(result.equals("good")){
                    fp++;
                }
                else{
                    tn++;
                }
            }

        }
        System.out.println("Accuracy ="+ (tp+tn)/(double)(tp+tn+fn+fp));
        System.out.println("tp adedi = " + tp);
        System.out.println("tn adedi = " + tn);
        System.out.println("tp rate ="+(tp/(double)(tp+fn)));
        System.out.println("tn rate ="+(tn/(double)(fp+tn)));

    }

    private static void printTree(Node root) {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel contentPane = new TreePanel(root);
        jFrame.setContentPane(new JScrollPane(contentPane));
        contentPane.setBounds(new Rectangle(new Dimension(1000,500)));
        jFrame.setSize(1000,500);
        jFrame.setVisible(true);
    }

}
