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

public class RoutingMatrixPanel extends MatrixPanel {


    private Cell<?> rxCell;
    private Cell<?> txCell;
    private final JPopupMenu popupMenu;

    private enum TxRx {NO_LABEL, TX_RX_LABEL, TX_RX_BUTTON}

    private MatrixModel filterModel;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;
    private TxRx txRx = TxRx.TX_RX_BUTTON;


    public RoutingMatrixPanel() {
        super();
        popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Go to Filters...");
        menuItem.addActionListener(e-> {

            Component comp = popupMenu.getInvoker();
            if(comp instanceof Cell) {
                goToFilterWindow(((Cell<FilterDirection>) comp).getData());
            }

        });
        popupMenu.add(menuItem);
    }

    public void goToFilterWindow(FilterDirection fd) {
        String[] links = getModel().getLinks();
        int from = -1;
        if(fd.getFrom() > 0 && fd.getFrom() < links.length) {
            from = Integer.parseInt(links[fd.getFrom()]);
        }
        int to = -1;
        if(fd.getTo() > 0 && fd.getTo() < links.length) {
            to = Integer.parseInt(links[fd.getTo()]);
        }

        System.out.println("Filter: " + fd.getFilterDirectionType() + " (" +  from + ", " + to + ")" );
    }

    public void reset() {
        if (model != null) {
            Arrays.fill(model.getTx(), true);
            Arrays.fill(model.getRx(), true);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    model.getMatrix()[i][j] = i == 0 || j == 0;
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
        future = scheduler.schedule(() -> {
            setModel(newModel);
        }, 500, TimeUnit.MILLISECONDS);

    }

    @Override
    public void refresh() {
        super.refresh();
        if (filterModel != null) {
            for (int i = 0; i < N; i++) {
                boolean filtered = false;
                if (i < filterModel.getTx().length) {
                    filtered = filterModel.getTx()[i];
                }
                txCells[i].setFiltered(filtered);
            }
            for (int i = 0; i < N; i++) {
                boolean filtered = false;
                if (i < filterModel.getRx().length) {
                    filtered = filterModel.getRx()[i];
                }
                rxCells[i].setFiltered(filtered);
            }
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    boolean filtered = false;
                    if (i < filterModel.getMatrix().length && j < filterModel.getMatrix().length) {
                        filtered = filterModel.getMatrix()[i][j];
                    }

                    matrixCells[i][j].setFiltered(filtered);
                }
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrixCells[i][j].setEnabled(rxCells[i].isSelected() && txCells[j].isSelected());
                if (last != null && last.getTo() == j && last.getFrom() == i) {
                    matrixCells[last.getFrom()][last.getTo()].highlight();
                    txCells[last.getTo()].highlight();
                    rxCells[last.getFrom()].highlight();
                }
            }
        }

    }

    @Override
    public void updatePanel() {
        super.updatePanel();
        for (int i = 1; i < N; i++) {
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
        for (int i = 1; i < N; i++) {
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
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
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
        if (N < 2) return;

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);
        gc.anchor = GridBagConstraints.CENTER;

        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 1;
        gc.gridheight = N - 1;
        gc.fill = GridBagConstraints.VERTICAL;

        Font font = new Font("TimesRoman", Font.BOLD, 16);
        VerticalLabel rxLabel = new VerticalLabel("Rx", VerticalLabel.CENTER);
        rxLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        rxLabel.setFont(font);
        rxLabel.setRotation(VerticalLabel.ROTATE_LEFT);

        add(rxLabel, gc);

        gc.gridx = 3;
        gc.gridy = 0;
        gc.gridwidth = N - 1;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel txLabel = new JLabel("Tx", JLabel.CENTER);
        txLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        txLabel.setFont(font);
        add(txLabel, gc);

    }

    private void addTxRxButton() {
        if (N < 2) return;

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);
        gc.anchor = GridBagConstraints.CENTER;

        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 1;
        gc.gridheight = N - 1;
        gc.fill = GridBagConstraints.VERTICAL;

        Font font = new Font("TimesRoman", Font.BOLD, 16);
        rxCell = new Cell<>(null, "Rx", "Rx", true, 5);
        rxCell.setPreferredSize(new Dimension(30, 10));
        rxCell.addActionListener(e -> {
            rxCell.setSelected(!rxCell.isSelected());
            Arrays.fill(model.getRx(), rxCell.isSelected());
            model.getRx()[0] = true;
            refresh();
            modelChanged();
        });
        updateCells(rxCells, rxCell);

        add(rxCell, gc);

        gc.gridx = 3;
        gc.gridy = 0;
        gc.gridwidth = N - 1;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;

        txCell = new Cell<>(null, "Tx", "Tx", false, 5);
        txCell.setPreferredSize(new Dimension(10, 30));
        txCell.addActionListener(e -> {
            txCell.setSelected(!txCell.isSelected());
            Arrays.fill(model.getTx(), txCell.isSelected());
            model.getTx()[0] = true;
            refresh();
            modelChanged();
        });
        updateCells(txCells, txCell);

        add(txCell, gc);
    }

    private void updateCells(Cell<?>[] cells, Cell<?> cell) {
        boolean allSelected = true;
        for (int i = 1; i < cells.length; i++) {
            if (!cells[i].isSelected()) {
                allSelected = false;
                break;
            }
        }
        cell.setSelected(allSelected);
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

        int N = 1 + (int) (Math.random() * 10);

        MatrixModel model = MatrixModel.random(N);
        MatrixModel filterModel = MatrixModel.random(N);

        fixModel(model);
        fixFilterModel(filterModel);

        panel.setModel(model);
        panel.setFilterModel(filterModel);


        JPanel p = new JPanel(new BorderLayout());

        JButton modelUpdate = new JButton("Update Model");
        modelUpdate.addActionListener(e -> {
            int newN = 1 + (int) (Math.random() * 10);
            MatrixModel newModel = MatrixModel.random(newN);
            fixModel(newModel);
            panel.setRoutingModel(newModel);

            // SwingUtilities.getWindowAncestor(panel).pack();
        });

        JButton filterUpdate = new JButton("Update Filters");
        filterUpdate.addActionListener(e -> {
            MatrixModel newFilterModel = MatrixModel.random(N);
            fixFilterModel(newFilterModel);
            panel.setFilterModel(newFilterModel);
        });

        JButton resetModel = new JButton("Reset");
        resetModel.addActionListener(e -> {
            panel.reset();
        });


        JPanel control = new JPanel();

        control.add(modelUpdate);

        control.add(filterUpdate);

        control.add(resetModel);

        p.add(control, BorderLayout.NORTH);

        JScrollPane pane = new JScrollPane(panel);
        pane.setPreferredSize(new Dimension(600, 600));
        pane.setMinimumSize(pane.getPreferredSize());

        p.add(pane, BorderLayout.CENTER);

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
