package net.jmb19905;

import net.jmb19905.util.ShutdownManager;
import net.jmb19905.util.events.EventHandler;
import net.jmb19905.util.events.EventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowSwing extends JFrame implements AppWindow {

    private final EventHandler<Window.WinEventCtx> eventHandler;
    private final JTextArea area;
    private final JTextField field;

    public WindowSwing() {
        eventHandler = new EventHandler<>("window_events");
        eventHandler.setValid(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        ShutdownManager.addCleanupFirst(this::dispose);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ShutdownManager.shutdown(0);
            }
        });

        setLayout(new BorderLayout());

        area = new JTextArea();
        area.setEditable(false);
        add(area, BorderLayout.CENTER);

        field = new JTextField();
        field.addActionListener(e -> {
            String text = getInputText();
            if (text.isBlank()) return;
            if (text.startsWith("/")) {
                AppWindow.handleCommand(this, text);
            } else {
                eventHandler.performEvent(new Window.MessageSendEvent(new Window.WinEventCtx(this)));
                appendMessage("<Du> " + field.getText());
            }
            field.setText("");
        });
        add(field, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(500, 400));
    }

    @Override
    public String getInputText() {
        return field.getText();
    }

    public void addEventListener(EventListener<?> listener) {
        eventHandler.addEventListener(listener);
    }

    public void showWindow() {
        setVisible(true);
        pack();
    }

    public void appendMessage(String text) {
        area.append(text + "\n");
    }

}
