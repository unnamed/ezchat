package me.fixeddev.ezchat.commands;

import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.fixeddev.commandflow.command.Command;
import org.bukkit.plugin.Plugin;

public class CommandRegistry {
    private final CommandManager commandManager;
    private final AnnotatedCommandTreeBuilder treeBuilder;

    public CommandRegistry(Plugin plugin) {
        PartInjector injector = PartInjector.create();
        injector.install(new DefaultsModule());
        injector.install(new BukkitModule());

        commandManager = new BukkitCommandManager(plugin.getName());
        treeBuilder = AnnotatedCommandTreeBuilder.create(injector);
    }

    public void registerCommand(CommandClass commandClass){
        commandManager.registerCommands(treeBuilder.fromClass(commandClass));
    }

    public void registerCommand(Command command){
        commandManager.registerCommand(command);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AnnotatedCommandTreeBuilder getTreeBuilder() {
        return treeBuilder;
    }
}
