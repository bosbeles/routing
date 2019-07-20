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


    protected final int padx;
    protected final float fontSize;
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
    protected Cell<FilterDirection> lastCell;

    public MatrixPanel(float fontSize, int padx) {
        super();
        this.fontSize = fontSize;
        this.padx = padx;
        setMinimumSize(new Dimension(400, 400));
        setMaximumSize(new Dimension(800,800));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension minimumSize = getMinimumSize();

        int w = Math.min(Math.max(super.getPreferredSize().width + 60, minimumSize.width), getMaximumSize().width);


        return new Dimension(w, w);
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

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dehighlight();
            }
        });
        Point oldPosition = null;
        if (pane != null) {
            oldPosition = pane.getViewport().getViewPosition();
        }

        pane = new JScrollPane(matrixPanel);
        pane.getVerticalScrollBar().setUnitIncrement(HEIGHT / 2);
        pane.getHorizontalScrollBar().setUnitIncrement(HEIGHT / 2);

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
        for (int i = 0; i < noOfLinks; i++) {
            for (int j = 0; j < noOfLinks; j++) {
                FilterDirection filterDirection = new FilterDirection(FilterDirection.FilterDirectionType.ROUTING, i, j);
                Cell<FilterDirection> cell = new Cell<FilterDirection>(filterDirection, Cell.TICK, "", false, fontSize) {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension d = rxCells[getData().getFrom()].getPreferredSize();
                        int max = Math.max(d.height, d.height);
                        return new Dimension(max, max);
                    }
                };

                matrixCells[i][j] = cell;

                JPanel cellPanel = i == j ? new JPanel() : cell;

                cellPanel.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {

                        onMouseDragged(e);

                    }
                });

                cellPanel.addMouseListener(new MouseAdapter() {

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
                        FilterDirection fd = last;
                        if(fd != null) {
                            rxCells[fd.getFrom()].dehighlight();
                            txCells[fd.getTo()].dehighlight();

                        }
                        if(lastCell != null) {
                            lastCell.dehighlight();
                        }
                        fd = cell.getData();

                        if(!dragging) {
                            rxCells[fd.getFrom()].highlight();
                            txCells[fd.getTo()].highlight();
                            last = fd;
                            lastCell = cell;
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
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


                matrixPanel.add(cellPanel, gc);
            }
        }

        for (int i = 0; i < noOfLinks; i++) {
            Cell<FilterDirection> rx = new Cell<>(new FilterDirection(FilterDirection.FilterDirectionType.RX, i, -1), getModel().getLinks()[i], false, padx, fontSize);
            rx.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    dehighlight();
                }
            });
            rx.setHoverEnabled(false);
            rxCells[i] = rx;

            gc.gridx = xOffset - 1;
            gc.gridy = yOffset + i;

            rowPanel.add(rx, gc);
        }

        gc.fill = GridBagConstraints.BOTH;
        for (int i = 0; i < noOfLinks; i++) {
            Cell<FilterDirection> tx = new Cell<FilterDirection>(new FilterDirection(FilterDirection.FilterDirectionType.TX, -1, i), getModel().getLinks()[i], true, fontSize) {
                @Override
                public Dimension getPreferredSize() {
                    int index = getData().getTo();
                    Dimension d = rxCells[index].getPreferredSize();
                    return new Dimension(d.height, d.width);
                }
            };
            tx.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    dehighlight();
                }
            });
            tx.setHoverEnabled(false);
            txCells[i] = tx;

            gc.gridx = xOffset + i;
            gc.gridy = yOffset - 1;

            columnPanel.add(tx, gc);
        }


        gc.gridx = 1;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        gc.weightx = 1;
        HeaderPanel headerPanel = new HeaderPanel("Kaynak", "Hedef");
        cornerPanel.add(headerPanel, gc);

        if (noOfLinks > 0) {
            gc.gridx = 0;
            gc.gridwidth = xOffset + noOfLinks + 2;
            gc.gridy = yOffset + noOfLinks + 2;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.BOTH;
            gc.anchor = GridBagConstraints.CENTER;
            gc.insets = new Insets(0,0,0,0);
            matrixPanel.add(new JPanel(), gc);
            columnPanel.add(new JPanel(), gc);
            rowPanel.add(new JPanel(), gc);

        } else {
            gc.fill = GridBagConstraints.BOTH;
            gc.weightx = 1;
            gc.weighty = 1;
            Supplier<JPanel> panelSupplier = emptyPanel(headerPanel.getMinimumSize().width, headerPanel.getMinimumSize().height);
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

    private void dehighlight() {
        FilterDirection fd = last;
        if (fd != null) {
            rxCells[fd.getFrom()].dehighlight();
            txCells[fd.getTo()].dehighlight();

        }
        if (lastCell != null) {
            lastCell.dehighlight();
        }
        lastCell = null;
        last = null;
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
                SwingUtilities.getWindowAncestor(this).pack();
            }
        });

    }


}
