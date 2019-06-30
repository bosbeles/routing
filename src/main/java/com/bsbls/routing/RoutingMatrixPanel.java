package com.bsbls.routing;

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

    private MatrixModel filterModel;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;


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
        if(future != null) {
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
                refresh();
                modelChanged();
            });
        }
        for (int i = 1; i < N; i++) {
            Cell<?> cell = rxCells[i];
            cell.setHoverEnabled(true);
            final int index = i;
            cell.addActionListener(e -> {
                cell.setSelected(!cell.isSelected());
                getModel().getRx()[index] = cell.isSelected();
                refresh();
                modelChanged();
            });
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Cell<?> cell = matrixCells[i][j];
                final int y = i;
                final int x = j;
                cell.addActionListener(e -> {
                    cell.setSelected(!cell.isSelected());
                    getModel().getMatrix()[y][x] = cell.isSelected();
                    modelChanged();
                });
            }
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
        model.getLinks()[0] = "HOST";
        model.getTx()[0] = true;
        model.getRx()[0] = true;
    }
}
