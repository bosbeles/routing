package com.bsbls.routing;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cell<T extends Serializable> extends JPanel {

    public static final Color DISABLED_COLOR = new Color(214, 217, 223);
    public static final Color PRIMARY_COLOR = new Color(0, 122, 204);
    public static final Color SECONDARY_COLOR = new Color(238, 238, 242);
    public static final Color BORDER_COLOR = new Color(147, 147, 147);
    public static final String TICK = "\u2714";

    public static final Font FONT = new Font("TimesRoman", Font.PLAIN, 12);
    public static final int FILTER_ICON_SIZE = 8;


    private static ImageIcon whiteFilterIcon;
    private static ImageIcon blackFilterIcon;
    private final int padx;
    private final int pady;
    private float fontSize;


    protected T data;
    protected String text;
    protected String deselectedText;
    protected boolean vertical;
    protected transient List<ActionListener> listeners;
    protected boolean selected;
    protected boolean hoverEnabled = true;

    protected VerticalLabel centerLabel;

    protected JLabel upperLabel;
    protected boolean filtered;


    protected boolean highlighted;
    protected Color primaryColor;
    protected Color secondaryColor;
    protected Color disabledColor;
    protected Color borderColor;


    public Cell() {
        this(null);
    }

    public Cell(T data) {
        this(data, "", false);
    }

    public Cell(T data, String selectedText, boolean vertical) {
        this(data, selectedText, selectedText, vertical, 0f);
    }

    public Cell(T data, String selectedText, boolean vertical, float fontSize) {
        this(data, selectedText, selectedText, vertical, fontSize);
    }

    public Cell(T data, String selectedText, boolean vertical, int padx, float fontSize) {
        this(data, selectedText, selectedText, vertical, padx, FILTER_ICON_SIZE + 2, 0);
    }

    public Cell(T data, String selectedText, String deselectedText, boolean vertical, float fontSize) {
        this(data, selectedText, deselectedText, vertical, 0, FILTER_ICON_SIZE + 2, 0);
    }

    public Cell(T data, String selectedText, String deselectedText, boolean vertical, int padx, int pady, float fontSize) {
        super();
        this.padx = padx;
        this.pady = pady;
        this.data = data;
        this.fontSize = fontSize;
        this.vertical = vertical;
        this.text = selectedText == null ? "" : selectedText;
        this.deselectedText = deselectedText == null ? "" : deselectedText;

        createPanel();
        createMouseListener();
    }


    private void createPanel() {
        upperLabel = new JLabel();

        this.primaryColor = PRIMARY_COLOR;
        this.secondaryColor = SECONDARY_COLOR;
        this.disabledColor = DISABLED_COLOR;
        this.borderColor = BORDER_COLOR;

        this.setBorder(new MatteBorder(1, 1, 1, 1, borderColor));

        VerticalLabel verticalLabel = new VerticalLabel(text);
        this.centerLabel = verticalLabel;
        if (vertical) {
            centerLabel.setRotation(VerticalLabel.ROTATE_LEFT);
        } else {
            centerLabel.setRotation(VerticalLabel.DONT_ROTATE);
        }
        this.centerLabel.setFont(fontSize > 0 ? FONT.deriveFont(fontSize) : FONT);

        setLayout(new BorderLayout());


        JPanel panel = emptyPanel(getWidth(), pady);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 1));
        panel.add(upperLabel);
        this.add(panel, BorderLayout.NORTH);

        JPanel p2 = new JPanel(new GridBagLayout());
        p2.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 3 + padx, 0, 3);
        gc.ipadx = this.padx;
        p2.add(this.centerLabel, gc);

        this.add(p2, BorderLayout.CENTER);

        JPanel empty = emptyPanel(getWidth(), pady);
        this.add(empty, BorderLayout.SOUTH);
    }

    private void createMouseListener() {

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (Cell.this.contains(e.getPoint())) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        ActionEvent ae = new ActionEvent(Cell.this, e.getID(), "", e.getWhen(), e.getModifiers());
                        notifyListeners(ae);
                    }
                }
                super.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverEnabled) {
                    highlight();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverEnabled) {
                    dehighlight();
                }
            }
        });
    }

    public void dehighlight() {
        highlighted = false;
        if (isEnabled()) {
            if (isSelected()) {
                setBackground(primaryColor);
            } else {
                setBackground(secondaryColor);
            }
        } else {
            setBackground(disabledColor);
            centerLabel.setEnabled(isEnabled());
            upperLabel.setEnabled(isEnabled());
        }
    }

    public void highlight() {
        highlighted = true;
        if (isEnabled()) {
            if (isSelected()) {
                setBackground(primaryColor.darker());
            } else {
                setBackground(secondaryColor.darker());
            }
        } else {
            setBackground(secondaryColor.darker());
            centerLabel.setEnabled(true);
            upperLabel.setEnabled(true);
        }
    }

    public void updateCell() {
        centerLabel.setText(isSelected() ? text : deselectedText);

        if (isEnabled()) {
            if (isSelected()) {
                centerLabel.setForeground(Color.WHITE);
                setBackground(primaryColor);
            } else {
                centerLabel.setForeground(Color.BLACK);
                setBackground(secondaryColor);
            }

            if (filtered) {
                upperLabel.setIcon(selected ? getWhiteFilterIcon() : getBlackFilterIcon());
            } else {
                upperLabel.setIcon(null);
            }

        } else {
            if (filtered) {
                upperLabel.setIcon(getBlackFilterIcon());
            } else {
                upperLabel.setIcon(null);
            }

            setBackground(disabledColor);
            centerLabel.setForeground(Color.BLACK);
        }

        if (highlighted) {
            highlight();
        } else {
            dehighlight();
        }
    }


    public void addActionListener(ActionListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>(2);
        }
        listeners.add(listener);
    }

    public boolean removeActionListener(ActionListener listener) {
        if (listeners != null) {
            return listeners.remove(listener);
        }
        return false;
    }

    private void notifyListeners(ActionEvent ae) {
        if (listeners != null) {
            for (ActionListener listener : listeners) {
                listener.actionPerformed(ae);
            }
        }
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        centerLabel.setEnabled(enabled);
        upperLabel.setEnabled(enabled);
        updateCell();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateCell();
    }

    public boolean isHoverEnabled() {
        return hoverEnabled;
    }

    public void setHoverEnabled(boolean hoverEnabled) {
        this.hoverEnabled = hoverEnabled;
        updateCell();
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
        updateCell();
    }

    public boolean isFiltered() {
        return filtered;
    }

    public static JPanel emptyPanel(int width, int height) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(width, height));
        panel.setMinimumSize(panel.getPreferredSize());
        return panel;
    }


    public static ImageIcon getWhiteFilterIcon() {
        if (whiteFilterIcon == null) {
            whiteFilterIcon = new ImageIcon(new ImageIcon(Cell.class.getResource("/filled-filter-16-white.png")).getImage().getScaledInstance(FILTER_ICON_SIZE, FILTER_ICON_SIZE, Image.SCALE_SMOOTH));
        }
        return whiteFilterIcon;
    }


    public static ImageIcon getBlackFilterIcon() {
        if (blackFilterIcon == null) {
            blackFilterIcon = new ImageIcon(new ImageIcon(Cell.class.getResource("/filled-filter-16-black.png")).getImage().getScaledInstance(FILTER_ICON_SIZE, FILTER_ICON_SIZE, Image.SCALE_SMOOTH));
        }
        return blackFilterIcon;
    }

}
