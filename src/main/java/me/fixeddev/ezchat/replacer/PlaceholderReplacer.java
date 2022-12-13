package me.fixeddev.ezchat.replacer;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public abstract class PlaceholderReplacer {

    private static PlaceholderReplacer instance;
    private static final Lock lock = new ReentrantLock();

    public static PlaceholderReplacer getInstance() {
        if (instance == null) {
            lock.lock();
            if (instance == null) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    Bukkit.getLogger().log(Level.INFO, "[EzChat] Successfully hooked with PlaceholderAPI");

                    instance = new PlaceholderApiReplacer();
                } else {
                    instance = new DummyReplacer();
                }
            }
            lock.unlock();
        }

        return instance;
    }

    public abstract String replacePlaceholders(Player player, String toReplace);

    public abstract String replaceRelational(Player player, Audience viewer, String toReplace);
}
