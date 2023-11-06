package net.jmb19905;

import net.jmb19905.util.Logger;

public class SwingApp extends App {

    private final WindowSwing window;

    public SwingApp() {
        window = new WindowSwing();
    }

    @Override
    public void start(String[] args) {
        window.showWindow();
        window.addEventListener((Window.MessageSendListener) e -> {
            if (Main.isDisconnected()) {
                Logger.warn("Can't send message; Not connected");
                return;
            }
            var text = e.getContext().getSource().getInputText();
            Main.sendMessage(text);
        });
    }

    @Override
    public void appendMessage(String message) {
        window.appendMessage(message);
    }

    @Override
    protected void shutdown() {
        window.dispose();
    }
}
