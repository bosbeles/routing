package com.bsbls.test;

import javax.swing.*;
import java.util.function.Supplier;

public class GUITester {


    public static void test(JPanel panel) {
        test(() -> panel, null);
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


        String[] lafs = {
                "org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceEmeraldDuskLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel"
        };

        UIManager.installLookAndFeel("Darcula", "com.bulenkov.darcula.DarculaLaf");

        for (String lafClass : lafs) {
            String simpleName = lafClass.substring(lafClass.lastIndexOf('.') + 1);
            int from = "Substance".length();
            int to = simpleName.indexOf("LookAndFeel");
            String name = simpleName.substring(from, to);
            UIManager.installLookAndFeel(name, lafClass);
        }



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
                if (lookAndFeelInfo.getName().contains(lookAndFeel)) {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName().toString());
                }

            }
        } catch (Exception ex) {

        }
    }
}
