package com.bsbls.routing;

import com.bsbls.routing.model.MatrixModel;
import com.bsbls.test.GUITester;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ConnectivityMatrixPanel extends MatrixPanel {


    @Override
    public void setModel(MatrixModel model) {
        if (model != null) {
            boolean[][] matrix = model.getMatrix();
            boolean[] tx = model.getTx();
            boolean[] rx = model.getRx();
            Arrays.fill(tx, false);
            Arrays.fill(rx, false);

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[i][j]) {
                        rx[i] = true;
                        tx[j] = true;
                    }
                }
            }
        }
        super.setModel(model);
    }

    public static void main(String[] args) {
        GUITester.test(ConnectivityMatrixPanel::test);
    }

    private static JComponent test() {
        ConnectivityMatrixPanel panel = new ConnectivityMatrixPanel();


        panel.setModel(MatrixModel.randomModel(1 + (int) (Math.random() * 5)));


        JPanel combo = new JPanel();
        JLabel label1 = new JLabel("Link ID: ");
        JComboBox<String> combo1 = new JComboBox<>(new String[]{"1001", "1002", "1003"});
        combo1.setPrototypeDisplayValue("65535");
        JLabel label2 = new JLabel("Sender ID: ");
        JComboBox<String> combo2 = new JComboBox<>(new String[]{"5", "6", "7"});
        combo2.setPrototypeDisplayValue("65535");
        combo.add(label1);
        combo.add(combo1);
        combo.add(label2);
        combo.add(combo2);

        combo2.addItemListener(e -> {

            panel.setModel(MatrixModel.randomModel(0 + (int) (Math.random() * 15)));
            //SwingUtilities.getWindowAncestor(panel).pack();


        });

        JPanel p = new JPanel(new BorderLayout());

        p.add(combo, BorderLayout.NORTH);
        //JScrollPane pane = new JScrollPane(panel);
        //pane.setPreferredSize(new Dimension(600, 600));
        p.add(panel, BorderLayout.CENTER);


        return p;

    }
}
