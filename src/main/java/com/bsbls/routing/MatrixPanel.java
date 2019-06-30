package com.bsbls.routing;

import com.bsbls.routing.model.FilterDirection;
import com.bsbls.routing.model.MatrixModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MatrixPanel extends JPanel {


    protected MatrixModel model;

    protected Cell<FilterDirection>[] txCells;
    protected Cell<FilterDirection>[] rxCells;
    protected Cell<FilterDirection>[][] matrixCells;
    protected int noOfLinks;
    protected FilterDirection last;


    public void refresh() {
        for (int i = 0; i < noOfLinks; i++) {
            Cell<?> tx = txCells[i];
            tx.setSelected(getModel().getTx()[i]);
        }

        for (int i = 0; i < noOfLinks; i++) {
            Cell<?> rx = rxCells[i];
            rx.setSelected(getModel().getRx()[i]);
        }

        for (int i = 0; i < noOfLinks; i++) {
            for (int j = 0; j < noOfLinks; j++) {
                Cell<FilterDirection> cell = matrixCells[i][j];
                cell.setSelected(getModel().getMatrix()[i][j]);
            }
        }

    }


    public void updatePanel() {
        this.noOfLinks = model == null ? 0 : model.getMatrix().length;
        txCells = new Cell[noOfLinks];
        rxCells = new Cell[noOfLinks];
        matrixCells = new Cell[noOfLinks][noOfLinks];

        removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);

        int xOffset = 2;
        int yOffset = 2;

        gc.fill = GridBagConstraints.BOTH;
        Dimension txDimension = new Dimension(40, 120);
        for (int i = 0; i < noOfLinks; i++) {
            Cell<FilterDirection> tx = new Cell<>(new FilterDirection(FilterDirection.FilterDirectionType.TX, -1, i), getModel().getLinks()[i], true);
            tx.setHoverEnabled(false);
            txCells[i] = tx;

            gc.gridx = xOffset + i;
            gc.gridy = yOffset - 1;

            tx.setPreferredSize(txDimension);
            add(tx, gc);
        }

        Dimension rxDimension = new Dimension(120, 40);

        for (int i = 0; i < noOfLinks; i++) {
            Cell<FilterDirection> rx = new Cell<>(new FilterDirection(FilterDirection.FilterDirectionType.RX, i, -1), getModel().getLinks()[i], false);
            rx.setHoverEnabled(false);
            rxCells[i] = rx;

            gc.gridx = xOffset - 1;
            gc.gridy = yOffset + i;


            rx.setPreferredSize(rxDimension);
            add(rx, gc);
        }


        Dimension d = new Dimension(40, 40);
        gc.fill = GridBagConstraints.NONE;
        for (int i = 0; i < noOfLinks; i++) {
            for (int j = 0; j < noOfLinks; j++) {
                Cell<FilterDirection> cell = new Cell<>(new FilterDirection(FilterDirection.FilterDirectionType.ROUTING, i, j), Cell.TICK, "", false);
                cell.setPreferredSize(d);
                cell.setMinimumSize(d);
                matrixCells[i][j] = cell;

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        FilterDirection fd = cell.getData();
                        rxCells[fd.getFrom()].highlight();
                        txCells[fd.getTo()].highlight();
                        last = fd;
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        FilterDirection fd = cell.getData();
                        rxCells[fd.getFrom()].dehighlight();
                        txCells[fd.getTo()].dehighlight();
                        last = null;
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1 && cell.isHoverEnabled()) {
                            cell.highlight();
                            last = cell.getData();
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
        add(new HeaderPanel("Kaynak", "Hedef"), gc);

        gc.gridx = 0;
        gc.gridwidth = xOffset + noOfLinks + 2;
        gc.gridy = yOffset + noOfLinks + 2;
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
        EventQueue.invokeLater(() -> {
            this.model = model;
            updatePanel();
        });

    }


}
