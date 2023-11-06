package net.jmb19905;

import ch.bailu.gtk.gio.ApplicationFlags;
import ch.bailu.gtk.gtk.Application;
import ch.bailu.gtk.type.Str;
import ch.bailu.gtk.type.Strs;
import net.jmb19905.util.Logger;
import net.jmb19905.util.ShutdownManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GtkApp extends App {

    private final Application app;
    public static Window window;

    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

    public GtkApp(String type) {
        app = new Application(new Str("net.jmb19905.kldiffiehellman." + type), ApplicationFlags.FLAGS_NONE);
        app.onActivate(() -> {
            window = new Window();
            window.addEventListener((Window.MessageSendListener) e -> {
                if (Main.isDisconnected()) {
                    Logger.warn("Can't send message; Not connected");
                    return;
                }
                var text = e.getContext().getSource().getInputText();
                Main.sendMessage(text);
            });
            window.show(app);
            while (!messageQueue.isEmpty()) {
                window.appendMessage(messageQueue.poll());
            }
        });
    }

    @Override
    public void start(String[] args) {
        ShutdownManager.shutdown(app.run(args.length, new Strs(args)));
    }

    @Override
    public void appendMessage(String message) {
        if (window == null) {
            messageQueue.offer(message);
        } else {
            window.appendMessage(message);
        }
    }

    @Override
    protected void shutdown() {
        app.quit();
    }
}
