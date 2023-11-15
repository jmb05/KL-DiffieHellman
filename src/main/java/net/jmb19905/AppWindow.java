package net.jmb19905;

import net.jmb19905.util.Logger;
import net.jmb19905.util.events.EventListener;

public interface AppWindow {
    String getInputText();
    void appendMessage(String s);
    void addEventListener(EventListener<?> listener);

    static void handleCommand(AppWindow window, String command) {
        if (!command.startsWith("/")) {
            Logger.warn("Invalid command: No leading '/'");
            return;
        }
        switch (command.substring(1)) {
            case "aktivieren" -> {
                if (Main.encryptionEnabled) {
                    Logger.warn("Encryption already enabled");
                    window.appendMessage("Verschlüsselung schon aktiv ");
                } else {
                    Main.encryptionEnabled = true;
                    Main.sendEncryptionState(true);
                    Logger.warn("Encryption enabled");
                    window.appendMessage("Verschlüsselung nun aktiv ");
                }
            }
            case "deaktivieren" -> {
                if (!Main.encryptionEnabled) {
                    Logger.warn("Encryption already disabled");
                    window.appendMessage("Verschlüsselung schon inaktiv ");
                } else {
                    Main.encryptionEnabled = false;
                    Main.sendEncryptionState(false);
                    Logger.warn("Encryption disabled");
                    window.appendMessage("Verschlüsselung nun inaktiv ");
                }
            }
            default -> {
                Logger.warn("Invalid command: Unknown command: " + command);
                window.appendMessage("Unbekannter Befehl");
            }
        }
    }

}
