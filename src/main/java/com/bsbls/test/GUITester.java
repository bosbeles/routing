package com.bsbls.test;

import javax.swing.*;
import java.util.function.Supplier;

public class GUITester {


    public static void test(JPanel panel) {
        test(()-> panel, null);
    }

    public static void test(Supplier<JComponent> panelSupplier) {
        test(panelSupplier, "Nimbus");
    }

    public static void test(Supplier<JComponent> panelSupplier, String lookAndFeel) {
        setLookAndFeel(lookAndFeel);

        JFrame frame = new JFrame();


        JMenu menu = new JMenu("Look and Feel");
        // Get all the available look and feel that we are going to use for
        // creating the JMenuItem and assign the action listener to handle
        // the selection of menu item to change the look and feel.
        UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : lookAndFeels) {
            JMenuItem item = new JMenuItem(lookAndFeelInfo.getName());
            item.addActionListener(event -> {
                try {
                    // Set the look and feel for the frame and update the UI
                    // to use a new selected look and feel.
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            menu.add(item);
        }

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);


        frame.setJMenuBar(menuBar);
        frame.setContentPane(panelSupplier.get());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void setLookAndFeel(String lookAndFeel) {
        try {
            UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
            for (UIManager.LookAndFeelInfo lookAndFeelInfo : lookAndFeelInfos) {
                if(lookAndFeelInfo.getName().contains(lookAndFeel)) {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName().toString());
                }

            }
        }
        catch (Exception ex) {

        }
    }
}
