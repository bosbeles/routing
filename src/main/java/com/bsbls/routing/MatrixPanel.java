package com.bsbls.routing;

import com.bsbls.routing.model.FilterDirection;
import com.bsbls.routing.model.MatrixModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Objects;
import java.util.function.Supplier;

public class MatrixPanel extends JPanel {


    protected MatrixModel model;

    protected Cell<FilterDirection>[] txCells;
    protected Cell<FilterDirection>[] rxCells;
    protected Cell<FilterDirection>[][] matrixCells;
    protected int noOfLinks;
    protected FilterDirection last;
    protected JPanel matrixPanel;
    protected JPanel columnPanel;
    protected JPanel rowPanel;
    protected JPanel cornerPanel;
    protected Point origin;
    protected JScrollPane pane;
    private boolean dragging;

    public MatrixPanel() {
        super();
        setPreferredSize(new Dimension(400, 400));
    }

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
        matrixPanel = new JPanel(new GridBagLayout());
        matrixPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDragged(e);
            }
        });


        matrixPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                origin = e.getPoint();
                dragging = false;
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                dragging = false;
                super.mouseReleased(e);
            }
        });
        Point oldPosition = null;
        if (pane != null) {
            oldPosition = pane.getViewport().getViewPosition();
        }

        pane = new JScrollPane(matrixPanel);
        pane.setMaximumSize(new Dimension(400, 400));
        pane.getVerticalScrollBar().setUnitIncrement(20);
        pane.getHorizontalScrollBar().setUnitIncrement(20);

        columnPanel = new JPanel(new GridBagLayout());
        pane.setColumnHeaderView(columnPanel);

        rowPanel = new JPanel(new GridBagLayout());
        pane.setRowHeaderView(rowPanel);

        cornerPanel = new JPanel(new GridBagLayout());
        pane.setCorner(JScrollPane.UPPER_LEFT_CORNER, cornerPanel);


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
            columnPanel.add(tx, gc);
        }

        Dimension rxDimension = new Dimension(120, 40);

        for (int i = 0; i < noOfLinks; i++) {
            Cell<FilterDirection> rx = new Cell<>(new FilterDirection(FilterDirection.FilterDirectionType.RX, i, -1), getModel().getLinks()[i], false);
            rx.setHoverEnabled(false);
            rxCells[i] = rx;

            gc.gridx = xOffset - 1;
            gc.gridy = yOffset + i;


            rx.setPreferredSize(rxDimension);
            rowPanel.add(rx, gc);
        }


        Dimension d = new Dimension(40, 40);
        gc.fill = GridBagConstraints.NONE;
        for (int i = 0; i < noOfLinks; i++) {
            for (int j = 0; j < noOfLinks; j++) {
                Cell<FilterDirection> cell = new Cell<>(new FilterDirection(FilterDirection.FilterDirectionType.ROUTING, i, j), Cell.TICK, "", false);
                cell.setPreferredSize(d);
                cell.setMinimumSize(d);
                matrixCells[i][j] = cell;


                cell.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {

                        onMouseDragged(e);

                    }
                });

                cell.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        origin = e.getPoint();
                        dragging = false;
                        super.mousePressed(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        dragging = false;
                        super.mouseReleased(e);
                    }

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
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1 && cell.isHoverEnabled()) {
                            cell.highlight();
                            last = cell.getData();
                        }
                        super.mouseClicked(e);
                    }
                });


                gc.gridx = xOffset + j;
                gc.gridy = yOffset + i;

                if (i != j) {
                    matrixPanel.add(cell, gc);
                }

            }
        }

        gc.gridx = 1;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.EAST;
        HeaderPanel headerPanel = new HeaderPanel("Kaynak", "Hedef");
        headerPanel.setPreferredSize(new Dimension(120, 120));
        cornerPanel.add(headerPanel, gc);


        if (noOfLinks > 0) {
            gc.gridx = 0;
            gc.gridwidth = xOffset + noOfLinks + 2;
            gc.gridy = yOffset + noOfLinks + 2;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.BOTH;
            gc.anchor = GridBagConstraints.CENTER;
            matrixPanel.add(new JPanel(), gc);
            columnPanel.add(new JPanel(), gc);
            rowPanel.add(new JPanel(), gc);

        } else {
            gc.fill = GridBagConstraints.BOTH;
            gc.weightx = 1;
            gc.weighty = 1;
            Supplier<JPanel> panelSupplier = emptyPanel(120, 120);
            matrixPanel.add(panelSupplier.get(), gc);
            columnPanel.add(panelSupplier.get(), gc);
            rowPanel.add(panelSupplier.get(), gc);
        }


        setLayout(new BorderLayout());
        add(pane);
        if (oldPosition != null) {
            pane.getViewport().setViewPosition(oldPosition);
        }

        revalidate();
        repaint();
        refresh();

    }

    private void onMouseDragged(MouseEvent e) {
        JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, matrixPanel);
        if (viewPort != null && origin != null) {
            int deltaX = origin.x - e.getX();
            int deltaY = origin.y - e.getY();

            if (Math.abs(deltaX) > 20 || Math.abs(deltaY) > 20) {
                if (!dragging) {
                    dragging = true;
                    origin = e.getPoint();
                    return;
                }

            }
            if (dragging) {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                Rectangle view = viewPort.getViewRect();
                view.x += deltaX;
                view.y += deltaY;

                matrixPanel.scrollRectToVisible(view);
            }

        }
    }

    public Supplier<JPanel> emptyPanel(int w, int h) {
        return () -> {
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(w, h));
            return panel;
        };
    }

    public MatrixModel getModel() {
        return model;
    }

    public void setModel(MatrixModel model) {
        EventQueue.invokeLater(() -> {
            if (!Objects.equals(this.model, model)) {
                this.model = model;
                updatePanel();
            }
        });

    }


}
