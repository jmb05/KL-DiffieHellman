package net.jmb19905;

import ch.bailu.gtk.gtk.*;
import ch.bailu.gtk.lib.bridge.CSS;
import ch.bailu.gtk.lib.bridge.UiBuilder;
import ch.bailu.gtk.type.exception.AllocationError;
import net.jmb19905.util.Logger;
import net.jmb19905.util.events.Event;
import net.jmb19905.util.events.EventContext;
import net.jmb19905.util.events.EventHandler;
import net.jmb19905.util.events.EventListener;

import java.io.IOException;

import static net.jmb19905.Main.s;

public class Window implements AppWindow {

    private ApplicationWindow window;
    private TextView textView;
    private Entry inputField;
    private final EventHandler<WinEventCtx> eventHandler;
    private boolean textViewEmpty = true;

    public Window() {
        eventHandler = new EventHandler<>("window_events");
        eventHandler.setValid(true);
        try {
            var builder = UiBuilder.fromResource("/window_layout.ui");
            window = new ApplicationWindow(builder.getObject("window"));
            textView = new TextView(builder.getObject("text_view"));
            inputField = new Entry(builder.getObject("input_field"));
            inputField.onActivate(() -> {
                String text = getInputText();
                if (text.isBlank()) return;
                if (text.startsWith("/")) {
                    AppWindow.handleCommand(this, text);
                } else {
                    eventHandler.performEvent(new MessageSendEvent(new WinEventCtx(this)));
                    appendMessage("<Du> " + getInputText());
                }
                new Editable(inputField.cast()).setText("");
            });
        } catch (IOException | AllocationError e) {
            Logger.error(e, "Could not load UI Layout");
        }
    }

    public void show(Application app) {
        window.setApplication(app);
        loadCss();
        window.show();
    }

    public void appendMessage(String s) {
        var buffer = textView.getBuffer();
        TextIter start = new TextIter();
        TextIter end = new TextIter();
        buffer.getBounds(start, end);
        var str = textViewEmpty ? s : "\n" + s;
        textViewEmpty = false;
        buffer.insert(end, s(str), str.length());
        start.destroy();
        end.destroy();
    }

    public void addEventListener(EventListener<?> listener) {
        eventHandler.addEventListener(listener);
    }

    public String getInputText() {
        var text = new Editable(inputField.cast()).getText();
        return text.toString();
    }

    private void loadCss() {
        try {
            CSS.addProviderRecursive(window, "/style.css");
        } catch (IOException e) {
            Logger.error(e, "Could not load Style");
        }
    }

    public static class MessageSendEvent extends Event<WinEventCtx> {
        public static final String ID = "message_send";
        public MessageSendEvent(@org.jetbrains.annotations.NotNull WinEventCtx ctx) {
            super(ctx, ID);
        }
    }

    public static class WinEventCtx extends EventContext<AppWindow> {
        public WinEventCtx(AppWindow source) {
            super(source);
        }
    }

    public interface MessageSendListener extends EventListener<MessageSendEvent> {
        @Override
        default String getId() {
            return MessageSendEvent.ID;
        }
    }

}
