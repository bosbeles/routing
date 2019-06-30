package com.bsbls.routing.model;

import java.util.Arrays;
import java.util.Random;

public class MatrixModel {

    private String[] links;
    private boolean[] tx;
    private boolean[] rx;
    private boolean[][] matrix;


    public MatrixModel(MatrixModel copy) {
        this.links = Arrays.copyOf(copy.links, copy.links.length);
        this.tx = Arrays.copyOf(copy.tx, copy.tx.length);
        this.rx = Arrays.copyOf(copy.rx, copy.rx.length);

        matrix = new boolean[copy.matrix.length][copy.matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = Arrays.copyOf(copy.matrix[i], copy.matrix[i].length);
        }
    }

    public MatrixModel(String[] links, boolean[] tx, boolean[] rx, boolean[][] matrix) {
        this.links = links;
        this.tx = tx;
        this.rx = rx;
        this.matrix = matrix;
    }


    public String[] getLinks() {
        return links;
    }

    public void setLinks(String[] links) {
        this.links = links;
    }

    public boolean[] getTx() {
        return tx;
    }

    public void setTx(boolean[] tx) {
        this.tx = tx;
    }

    public boolean[] getRx() {
        return rx;
    }

    public void setRx(boolean[] rx) {
        this.rx = rx;
    }

    public boolean[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(boolean[][] matrix) {
        this.matrix = matrix;
    }

    public static MatrixModel random(int N) {
        String[] links = new String[N];
        for (int i = 0; i < N; i++) {
            links[i] = String.valueOf(i + 1000);
        }

        boolean[] tx = new boolean[links.length];
        boolean[] rx = new boolean[links.length];
        boolean[][] matrix = new boolean[links.length][links.length];

        Random rand = new Random();
        for (int i = 0; i < links.length; i++) {
            for (int j = 0; j < links.length; j++) {
                if (i != j) {
                    matrix[i][j] = rand.nextBoolean();
                }
            }
        }

        for (int i = 0; i < tx.length; i++) {
            tx[i] = rand.nextBoolean();
        }

        for (int i = 0; i < rx.length; i++) {
            rx[i] = rand.nextBoolean();
        }

        MatrixModel model = new MatrixModel(links, tx, rx, matrix);
        return model;
    }
}
