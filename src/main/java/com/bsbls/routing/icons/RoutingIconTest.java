package com.bsbls.routing.icons;

import com.bsbls.test.GUITester;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoutingIconTest
{

    public static final JButton HOST_ONLY_BUTTON = createButton("/routing_host_only_32x32-0.png", "Forward only Host data.");
    public static final JButton ALL_BUTTON = createButton("/routing_all_32x32-0.png", "Enable routing for all links.");
    public static final JButton NONE_BUTTON = createButton("/routing_none_32x32-0.png", "Enable routing for all links.");



    public static void main(String[] args) {
        GUITester.test(RoutingIconTest::test);
    }

    private static JComponent test() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));


        panel.add(HOST_ONLY_BUTTON);
        panel.add(ALL_BUTTON);
        panel.add(NONE_BUTTON);




        return panel;
    }

    public static JButton createButton(String name) {
        return createButton(name, null);
    }

    public static JButton createButton(String name, String tooltip) {

        ImageIcon icon = new ImageIcon(RoutingIconTest.class.getResource(name));
        JButton button = new JButton(icon);
        button.setMargin(new Insets(3, 3, 2, 2));
        button.setFocusPainted(false);
        buttonHover(button, false);
        if(tooltip != null) {
            button.setToolTipText(tooltip);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonHover(button, true);

                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonHover(button, false);
                super.mouseExited(e);
            }
        });


        UIDefaults def = new UIDefaults();
        def.put("Button.contentMargins", new Insets(4,4,3,3));
        button.putClientProperty("Nimbus.Overrides", def);

        return button;
    }

    public static void buttonHover(JButton button, boolean hover) {
        button.setBorderPainted(hover);
        button.setContentAreaFilled(hover);
        button.setOpaque(hover);

    }
}
