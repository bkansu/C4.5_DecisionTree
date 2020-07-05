package com.bkansu.ml.c45decisiontree.algorithm;

import com.bkansu.ml.c45decisiontree.model.Node;

import javax.swing.*;
import java.awt.*;

public class TreePanel extends JPanel {
    /* Temel java swing metodlarini kullanarak agaci ekrana yazdiran class  */
    private Node node;
    private int y;

    public TreePanel(Node node) {
        this.node = node;
        y = 15;
    }

    @Override
    protected void paintComponent(Graphics g) {
        drawNode(g, 300, y, node, 300);
    }

    private void drawNode(Graphics g, int x, int y, Node node, int elli) {
        int agacBoslugu=0;
        int cizgiPayi = 80;
        double artanGenislik = 0.8;
        int yaziPayi = 15;
        if(node.isLeaf()){
            if (node.getAttributeValue().isContinues()) {
                g.drawString(node.getAttributeIndex()+"/"+ node.getAttributeValue().getValue(),x, y);
                g.drawLine(x, y,x- elli, y + cizgiPayi);
                g.drawLine(x, y,x+ elli, y + cizgiPayi);
                g.drawString(String.valueOf(node.getResults().get(0)),x- elli, y + cizgiPayi + yaziPayi);
                g.drawString(String.valueOf(node.getResults().get(1)),x+ elli, y + cizgiPayi + yaziPayi);
            }
            else{
                g.drawString(String.valueOf(node.getAttributeIndex()),x, y);
                int size = node.getAttributeValue().getTypes().size();
                for (int i = 0; i < size; i++) {
                    int xNew = x - size / 2 * elli + (i * elli);
                    g.drawLine(x, y,xNew, y + cizgiPayi);
                    g.drawString(node.getAttributeValue().getTypes().get(i), xNew, y + cizgiPayi + yaziPayi);
                    g.drawString(node.getResults().get(i),xNew, y + cizgiPayi+yaziPayi+yaziPayi);
                }
            }
        }
        else{
            if(node.getAttributeValue().isContinues()){
                g.drawString(node.getAttributeIndex()+"/"+ node.getAttributeValue().getValue(),x, y);
                g.drawLine(x, y,x- elli +agacBoslugu, y + cizgiPayi);
                g.drawLine(x, y,x+ elli +agacBoslugu, y + cizgiPayi);
                drawNode(g,x- elli +agacBoslugu,y+ cizgiPayi + yaziPayi,node.getChilds().get(0), (int) (elli/ artanGenislik));
                drawNode(g,x+ elli +agacBoslugu,y+ cizgiPayi + yaziPayi,node.getChilds().get(1), (int) (elli/ artanGenislik));
            }
            else{
                g.drawString(String.valueOf(node.getAttributeIndex()),x, y);
                int size = node.getAttributeValue().getTypes().size();
                for (int i = 0; i < size-1; i++) {
                    g.drawLine(x, y,x-size/2* (elli +agacBoslugu) +(i* (elli +agacBoslugu)), y + cizgiPayi);

                    drawNode(g,x-size/2* (elli +agacBoslugu) +(i* (elli +agacBoslugu)),y+ cizgiPayi + yaziPayi,node.getChilds().get(i), (int) (elli/ artanGenislik));
                }
            }
        }
    }
}
