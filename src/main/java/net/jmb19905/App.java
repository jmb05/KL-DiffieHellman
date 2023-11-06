package net.jmb19905;

import net.jmb19905.util.ShutdownManager;

public abstract class App {

    public App() {
        ShutdownManager.addCleanupLast(this::shutdown);
    }

    public abstract void start(String[] args);

    public abstract void appendMessage(String message);

    protected abstract void shutdown();

}
