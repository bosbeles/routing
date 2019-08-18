package com.bsbls.routing;

import com.bsbls.routing.model.FilterDirection;
import com.bsbls.routing.model.MatrixModel;
import com.bsbls.test.GUITester;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;

import static com.bsbls.routing.icons.RoutingIconTest.*;

public class RoutingMatrixPanel extends MatrixPanel {


    private static Timer timer;
    private final Insets insets = new Insets(2, 2, 2, 2);
    private Cell<?> rxCell;
    private Cell<?> txCell;
    private final JPopupMenu popupMenu;

    @Override
    public void setModel(MatrixModel model) {
        super.setModel(model);
    }

    private enum TxRx {NO_LABEL, TX_RX_LABEL, TX_RX_BUTTON}

    private MatrixModel filterModel;
    private transient ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private transient ScheduledFuture<?> future;
    private TxRx txRx = TxRx.TX_RX_BUTTON;


    public RoutingMatrixPanel() {
        super(14f, 15);
        popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Go to Filters...");
        menuItem.addActionListener(e -> {
            Component comp = popupMenu.getInvoker();
            if (comp instanceof Cell) {
                goToFilterWindow(((Cell<FilterDirection>) comp).getData());
            }

        });
        popupMenu.add(menuItem);

    }

    public void goToFilterWindow(FilterDirection fd) {
        String[] links = getModel().getLinks();
        int from = -1;
        if (fd.getFrom() > 0 && fd.getFrom() < links.length) {
            from = Integer.parseInt(links[fd.getFrom()]);
        }
        int to = -1;
        if (fd.getTo() > 0 && fd.getTo() < links.length) {
            to = Integer.parseInt(links[fd.getTo()]);
        }

        System.out.println("Filter: " + fd.getFilterDirectionType() + " (" + from + ", " + to + ")");
    }

    public void reset() {
        reset((i, j) -> i == 0 || j == 0);
    }

    public void reset(BiPredicate<Integer, Integer> cellPredicate) {

        if (model != null) {
            if(rxCell != null) {
                rxCell.setSelected(true);
            }

            if(txCell != null) {
                txCell.setSelected(true);
            }

            Arrays.fill(model.getTx(), true);
            Arrays.fill(model.getRx(), true);
            for (int i = 0; i < noOfLinks; i++) {
                for (int j = 0; j < noOfLinks; j++) {
                    model.getMatrix()[i][j] = cellPredicate.test(i, j);
                }
            }
        }


        refresh();
        modelChanged();
    }

    private void modelChanged() {
        MatrixModel newModel = new MatrixModel(getModel());
        if (future != null) {
            future.cancel(false);
        }
        future = scheduler.schedule(() -> setModel(newModel), 1000, TimeUnit.MILLISECONDS);

    }

    @Override
    public void refresh() {
        super.refresh();
        if (filterModel != null) {
            for (int i = 0; i < noOfLinks; i++) {
                boolean filtered = false;
                if (i < filterModel.getTx().length) {
                    filtered = filterModel.getTx()[i];
                }
                txCells[i].setFiltered(filtered);
            }
            for (int i = 0; i < noOfLinks; i++) {
                boolean filtered = false;
                if (i < filterModel.getRx().length) {
                    filtered = filterModel.getRx()[i];
                }
                rxCells[i].setFiltered(filtered);
            }
            for (int i = 0; i < noOfLinks; i++) {
                for (int j = 0; j < noOfLinks; j++) {
                    boolean filtered = false;
                    if (i < filterModel.getMatrix().length && j < filterModel.getMatrix().length) {
                        filtered = filterModel.getMatrix()[i][j];
                    }

                    matrixCells[i][j].setFiltered(filtered);
                }
            }
        }

        for (int i = 0; i < noOfLinks; i++) {
            for (int j = 0; j < noOfLinks; j++) {
                matrixCells[i][j].setEnabled(rxCells[i].isSelected() && txCells[j].isSelected());
                if (last != null && last.getTo() == j && last.getFrom() == i) {
                    lastCell = matrixCells[last.getFrom()][last.getTo()];
                    last = lastCell.getData();
                    lastCell.highlight();
                    txCells[last.getTo()].highlight();
                    rxCells[last.getFrom()].highlight();

                }
            }
        }

    }

    @Override
    public void updatePanel() {
        super.updatePanel();
        for (int i = 1; i < noOfLinks; i++) {
            Cell<?> cell = txCells[i];
            cell.setHoverEnabled(true);
            final int index = i;
            cell.addActionListener(e -> {
                cell.setSelected(!cell.isSelected());
                getModel().getTx()[index] = cell.isSelected();
                updateCells(txCells, txCell);
                refresh();
                modelChanged();
            });
            cell.setComponentPopupMenu(popupMenu);
        }
        for (int i = 1; i < noOfLinks; i++) {
            Cell<?> cell = rxCells[i];
            cell.setHoverEnabled(true);
            final int index = i;
            cell.addActionListener(e -> {
                cell.setSelected(!cell.isSelected());
                getModel().getRx()[index] = cell.isSelected();
                updateCells(rxCells, rxCell);
                refresh();
                modelChanged();
            });
            cell.setComponentPopupMenu(popupMenu);
        }
        for (int i = 0; i < noOfLinks; i++) {
            for (int j = 0; j < noOfLinks; j++) {
                Cell<FilterDirection> cell = matrixCells[i][j];
                cell.addActionListener(e -> {
                    cell.setSelected(!cell.isSelected());
                    getModel().getMatrix()[cell.getData().getFrom()][cell.getData().getTo()] = cell.isSelected();
                    modelChanged();
                });
                cell.setComponentPopupMenu(popupMenu);
            }
        }

        switch (txRx) {
            case TX_RX_LABEL:
                addTxRxLabel();
                break;
            case TX_RX_BUTTON:
                addTxRxButton();
                break;
            default:
        }


    }

    private void addTxRxLabel() {
        if (noOfLinks < 2) return;

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = insets;
        gc.anchor = GridBagConstraints.CENTER;

        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 1;
        gc.gridheight = noOfLinks - 1;
        gc.fill = GridBagConstraints.VERTICAL;


        Font font = Cell.FONT.deriveFont(Font.BOLD);
        VerticalLabel rxLabel = new VerticalLabel("Rx", VerticalLabel.CENTER);
        rxLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        rxLabel.setFont(font);
        rxLabel.setRotation(VerticalLabel.ROTATE_LEFT);

        rowPanel.add(rxLabel, gc);

        gc.gridx = 3;
        gc.gridy = 0;
        gc.gridwidth = noOfLinks - 1;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel txLabel = new JLabel("Tx", JLabel.CENTER);
        txLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        txLabel.setFont(font);
        columnPanel.add(txLabel, gc);

        gc = new GridBagConstraints();
        gc.insets = insets;
        gc.gridx = 0;
        gc.gridy = 0;

        JPanel panel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(rxLabel.getWidth(), txLabel.getHeight());
            }
        };

        cornerPanel.add(panel, gc);

    }

    private void addTxRxButton() {
        if (noOfLinks < 2) return;

        GridBagConstraints gc = new GridBagConstraints();
        Insets insets = this.insets;
        gc.insets = insets;
        gc.anchor = GridBagConstraints.CENTER;

        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 1;
        gc.gridheight = noOfLinks - 1;
        gc.fill = GridBagConstraints.VERTICAL;

        rxCell = new Cell(null, "Rx", "Rx", true, 0, 5, fontSize) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(txCell.getHeight(), super.getPreferredSize().width);
            }
        };
        rxCell.addActionListener(e -> {
            rxCell.setSelected(!rxCell.isSelected());
            Arrays.fill(model.getRx(), rxCell.isSelected());
            model.getRx()[0] = true;
            refresh();
            modelChanged();
        });

        updateCells(rxCells, rxCell);

        rowPanel.add(rxCell, gc);

        gc.gridx = 3;
        gc.gridy = 0;
        gc.gridwidth = noOfLinks - 1;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;

        txCell = new Cell<>(null, "Tx", "Tx", false, 0, 5, fontSize);
        txCell.addActionListener(e -> {
            txCell.setSelected(!txCell.isSelected());
            Arrays.fill(model.getTx(), txCell.isSelected());
            model.getTx()[0] = true;
            refresh();
            modelChanged();
        });
        updateCells(txCells, txCell);

        columnPanel.add(txCell, gc);

        gc = new GridBagConstraints();
        gc.insets = insets;
        gc.gridx = 0;
        gc.gridy = 0;

        JPanel panel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(rxCell.getWidth(), txCell.getHeight());
            }
        };


        cornerPanel.add(panel, gc);
    }

    private void updateCells(Cell<?>[] cells, Cell<?> cell) {
        boolean allSelected = true;
        for (int i = 1; i < cells.length; i++) {
            if (!cells[i].isSelected()) {
                allSelected = false;
                break;
            }
        }
        if (cell != null) {
            cell.setSelected(allSelected);
        }

    }

    public MatrixModel getRoutingModel() {
        return getModel();
    }

    public void setRoutingModel(MatrixModel routingModel) {
        setModel(routingModel);
    }

    public MatrixModel getFilterModel() {
        return filterModel;
    }

    public void setFilterModel(MatrixModel filterModel) {
        this.filterModel = filterModel;
        refresh();
    }

    public static void main(String[] args) {
        GUITester.test(RoutingMatrixPanel::test);
    }

    private static JComponent test() {
        RoutingMatrixPanel panel = new RoutingMatrixPanel();

        int noOfLinks = 1 + (int) (Math.random() * 10);

        MatrixModel model = MatrixModel.randomModel(noOfLinks);
        MatrixModel filterModel = MatrixModel.randomModel(noOfLinks);

        fixModel(model);
        fixFilterModel(filterModel);

        panel.setModel(model);
        panel.setFilterModel(filterModel);


        JPanel p = new JPanel(new BorderLayout());

        JButton modelUpdate = new JButton("Update Model");
        modelUpdate.addActionListener(e -> {
            int newN = 1 + (int) (Math.random() * 10);
            MatrixModel newModel = MatrixModel.randomModel(newN);
            fixModel(newModel);
            panel.setRoutingModel(newModel);


        });

        JButton filterUpdate = new JButton("Update Filters");
        filterUpdate.addActionListener(e -> {
            MatrixModel newFilterModel = MatrixModel.randomModel(noOfLinks);
            fixFilterModel(newFilterModel);
            panel.setFilterModel(newFilterModel);

        });


        JPanel control = new JPanel(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        control.add(modelUpdate, gc);


        gc.gridx++;
        control.add(filterUpdate, gc);


        gc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        buttonPanel.add(HOST_ONLY_BUTTON);
        buttonPanel.add(ALL_BUTTON);
        buttonPanel.add(NONE_BUTTON);
        gc.gridx++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        control.add(buttonPanel, gc);


        ALL_BUTTON.addActionListener(e -> panel.reset((i, j) -> true));

        NONE_BUTTON.addActionListener(e -> panel.reset((i, j) -> false));


        HOST_ONLY_BUTTON.addActionListener(e -> panel.reset());

        p.add(control, BorderLayout.NORTH);


        p.add(panel, BorderLayout.CENTER);

        return p;

    }

    private static void fixFilterModel(MatrixModel filterModel) {
        filterModel.getTx()[0] = false;
        filterModel.getRx()[0] = false;
    }

    private static void fixModel(MatrixModel model) {
        model.getLinks()[0] = "Ana Sistem";
        model.getTx()[0] = true;
        model.getRx()[0] = true;
    }
}
