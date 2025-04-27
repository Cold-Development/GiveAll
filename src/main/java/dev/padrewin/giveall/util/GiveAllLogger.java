package dev.padrewin.giveall.util;

import dev.padrewin.giveall.GiveAll;
import dev.padrewin.giveall.setting.SettingKey;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPOutputStream;

public class GiveAllLogger {

    private final GiveAll plugin;
    private final File logFolder;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public GiveAllLogger(GiveAll plugin) {
        this.plugin = plugin;
        this.logFolder = new File(plugin.getDataFolder(), "logs");
        if (!logFolder.exists()) {
            logFolder.mkdirs();
        }
    }

    public void log(Map<String, List<String>> giveLog) {
        CompletableFuture.runAsync(() -> {
            try {
                String today = dateFormat.format(new Date());
                File logFile = new File(logFolder, "giveall-" + today + ".log");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                    writer.write("=== GiveAll Executed - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ===");
                    writer.newLine();

                    for (Map.Entry<String, List<String>> entry : giveLog.entrySet()) {
                        String mainReceiver = entry.getKey();
                        List<String> blockedPlayers = entry.getValue();

                        if (blockedPlayers.isEmpty()) {
                            writer.write("Received: " + mainReceiver);
                        } else {
                            writer.write("Received: " + mainReceiver + " (Blocked: " + String.join(", ", blockedPlayers) + ")");
                        }
                        writer.newLine();
                    }

                    writer.write("=== End of GiveAll ===");
                    writer.newLine();
                    writer.newLine();
                }
            } catch (IOException e) {
                if (SettingKey.DEBUG_MODE.get()) {
                    plugin.getLogger().warning("Failed to write GiveAll log: " + e.getMessage());
                }
            }
        });
    }

    public void archiveLogs() {
        CompletableFuture.runAsync(() -> {
            try {
                String today = dateFormat.format(new Date());
                File currentLog = new File(logFolder, "giveall-" + today + ".log");

                if (!currentLog.exists()) return;

                int index = 1;
                File archiveFile;
                do {
                    archiveFile = new File(logFolder, "giveall-" + today + "-" + index + ".log.gz");
                    index++;
                } while (archiveFile.exists());

                try (FileInputStream fis = new FileInputStream(currentLog);
                     FileOutputStream fos = new FileOutputStream(archiveFile);
                     GZIPOutputStream gos = new GZIPOutputStream(fos)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        gos.write(buffer, 0, len);
                    }
                }

                Files.delete(currentLog.toPath());

                if (SettingKey.DEBUG_MODE.get()) {
                    plugin.getLogger().info("Archived GiveAll log to: " + archiveFile.getName());
                }
            } catch (IOException e) {
                if (SettingKey.DEBUG_MODE.get()) {
                    plugin.getLogger().warning("Failed to archive GiveAll log: " + e.getMessage());
                }
            }
        });
    }
}
