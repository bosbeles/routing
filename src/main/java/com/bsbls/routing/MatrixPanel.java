package com.bsbls.routing;

import com.bsbls.routing.model.MatrixModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MatrixPanel extends JPanel {


    protected MatrixModel model;

    protected Cell<?>[] txCells;
    protected Cell<?>[] rxCells;
    protected Cell<?>[][] matrixCells;
    protected int N;


    public void refresh() {
        for (int i = 0; i < N; i++) {
            Cell<?> tx = txCells[i];
            tx.setSelected(getModel().getTx()[i]);
        }

        for (int i = 0; i < N; i++) {
            Cell<?> rx = rxCells[i];
            rx.setSelected(getModel().getRx()[i]);
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Cell<?> cell = matrixCells[i][j];
                cell.setSelected(getModel().getMatrix()[i][j]);
            }
        }

    }



    public void updatePanel() {
        this.N = model == null ? 0 : model.getMatrix().length;
        txCells = new Cell<?>[N];
        rxCells = new Cell<?>[N];
        matrixCells = new Cell<?>[N][N];

        removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);

        int xOffset = 2;
        int yOffset = 2;

        gc.fill = GridBagConstraints.BOTH;
        Dimension txDimension = new Dimension(40, 120);
        for (int i = 0; i < N; i++) {
            Cell<?> tx = new Cell<>(null, getModel().getLinks()[i], true);
            tx.setHoverEnabled(false);
            txCells[i] = tx;

            gc.gridx = xOffset + i;
            gc.gridy = yOffset - 1;

            tx.setPreferredSize(txDimension);
            add(tx, gc);
        }

        Dimension rxDimension = new Dimension(120, 40);

        for (int i = 0; i < N; i++) {
            Cell<?> rx = new Cell<>(null, getModel().getLinks()[i], false);
            rx.setHoverEnabled(false);
            rxCells[i] = rx;

            gc.gridx = xOffset - 1;
            gc.gridy = yOffset + i;


            rx.setPreferredSize(rxDimension);
            add(rx, gc);
        }


        Dimension d = new Dimension(40, 40);
        gc.fill = GridBagConstraints.NONE;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                final int x = j;
                final int y = i;
                Cell<?> cell = new Cell<>(null, Cell.TICK, "", false);
                cell.setPreferredSize(d);
                cell.setMinimumSize(d);
                matrixCells[i][j] = cell;

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        rxCells[y].highlight();
                        txCells[x].highlight();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        rxCells[y].dehighlight();
                        txCells[x].dehighlight();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if(cell.isHoverEnabled()) {
                                cell.highlight();
                            }
                        }
                        super.mousePressed(e);

                    }
                });


                gc.gridx = xOffset + j;
                gc.gridy = yOffset + i;

                if (i != j) {
                    add(cell, gc);
                }

            }
        }

        gc.gridx = 1;
        gc.gridy = 1;
        gc.fill = GridBagConstraints.BOTH;
        add(new HeaderPanel("From", "To"), gc);

        gc.gridx = 0;
        gc.gridwidth = xOffset + N + 2;
        gc.gridy = yOffset + N + 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gc);

        revalidate();
        repaint();
        refresh();

    }

    public MatrixModel getModel() {
        return model;
    }

    public void setModel(MatrixModel model) {
        EventQueue.invokeLater(()->{
            this.model = model;
            updatePanel();
        });

    }


}
