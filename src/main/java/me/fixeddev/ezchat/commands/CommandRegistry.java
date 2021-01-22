package me.fixeddev.ezchat.commands;

import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.fixeddev.commandflow.command.Command;
import me.fixeddev.ezchat.ChatPlugin;
import me.fixeddev.ezchat.format.ChatFormatManager;

public class CommandRegistry {
    private final CommandManager commandManager;
    private final AnnotatedCommandTreeBuilder treeBuilder;

    private final ChatPlugin plugin;
    private final ChatFormatManager manager;

    public CommandRegistry(ChatPlugin plugin, ChatFormatManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        PartInjector injector = PartInjector.create();
        injector.install(new DefaultsModule());
        injector.install(new BukkitModule());

        commandManager = new BukkitCommandManager(plugin.getName());
        treeBuilder = AnnotatedCommandTreeBuilder.create(injector);
    }

    public void registerCommands() {
        commandManager.registerCommands(treeBuilder.fromClass(new me.fixeddev.ezchat.commands.EzChatCommands(plugin, manager)));
    }

    public void unregisterCommands() {
        commandManager.unregisterAll();
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AnnotatedCommandTreeBuilder getTreeBuilder() {
        return treeBuilder;
    }
}
