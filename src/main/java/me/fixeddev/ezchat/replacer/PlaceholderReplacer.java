package me.fixeddev.ezchat.replacer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class PlaceholderReplacer {

    private static PlaceholderReplacer instance;
    private static Lock lock = new ReentrantLock();

    public static PlaceholderReplacer getInstance() {
        if (instance == null) {
            lock.lock();
            if (instance == null) {
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderApi")) {
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

    public abstract String replaceRelational(Player player, Player playerTwo, String toReplace);
}
