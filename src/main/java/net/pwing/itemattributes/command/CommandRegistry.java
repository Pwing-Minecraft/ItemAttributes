package net.pwing.itemattributes.command;

import net.pwing.itemattributes.ItemAttributes;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CommandRegistry {
    private static final CommandRegistry INSTANCE = new CommandRegistry();

    public void registerExecutor(String commandName, String description, BaseCommandExecutor executor, String... aliases) {
        PluginCommand command = inject(commandName, commandName.toLowerCase(Locale.ROOT), description, aliases);
        command.setExecutor(executor);

        registerPermissions(commandName.toLowerCase(Locale.ROOT), executor);
    }

    private static PluginCommand inject(String headerName, String commandName, String description, String... aliases) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCommand = constructor.newInstance(commandName, ItemAttributes.getInstance());
            pluginCommand.setAliases(List.of(aliases));
            pluginCommand.setDescription(description);
            pluginCommand.setPermission("itemattributes.command." + commandName);

            getCommandMap().register(commandName, "itemattributes", pluginCommand);
            return pluginCommand;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to construct PluginCommand " + headerName, e);
        }
    }

    private static void registerPermissions(String commandName, BaseCommandExecutor executor) {
        String rootPermissionNode = "itemattributes.command." + commandName.toLowerCase(Locale.ROOT);
        String wildcardPermissionNode = rootPermissionNode + ".*";

        PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        // Register the root permission so it shows up in things such as LuckPerms UI.
        // Set to true by default so the user can see the command, however this is intentionally
        // done so server owners can fully revoke the permission, and it will not show up in tab complete.
        Permission rootPermission = pluginManager.getPermission(rootPermissionNode);
        if (rootPermission == null) {
            pluginManager.addPermission(rootPermission = new Permission(rootPermissionNode, PermissionDefault.TRUE));
        }

        Permission wildcardPermission = pluginManager.getPermission(wildcardPermissionNode);
        if (wildcardPermission == null) {
            pluginManager.addPermission(wildcardPermission = new Permission(wildcardPermissionNode, PermissionDefault.OP));
        }

        Set<String> childPermissions = new HashSet<>();
        for (Map.Entry<String, Set<BaseCommandExecutor.CommandWrapper>> entry : executor.getCommandWrappers().entrySet()) {
            for (BaseCommandExecutor.CommandWrapper wrapper : entry.getValue()) {
                String node = wrapper.getCommand().permissionNode();
                String permissionNode = executor.getPermissionNode(node);

                // Add our permission node to the parent permission
                if (permissionNode != null) {
                    childPermissions.add(permissionNode);
                }
            }
        }

        // Add all child permissions to the parent permission
        for (String childPermissionNode : childPermissions) {
            Permission childPermission = pluginManager.getPermission(childPermissionNode);
            if (childPermission == null) {
                pluginManager.addPermission(childPermission = new Permission(childPermissionNode, PermissionDefault.OP));
            }

            // For wildcard permissions, set to true as we want all permissions granted when the wildcard is present
            childPermission.addParent(wildcardPermission, true);
        }
    }

    private static CommandMap getCommandMap() {
        // If the getCommandMap method exists (Paper), use that
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(Bukkit.getServer());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // Continue
        }

        Field field;
        try {
            field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get CommandMap", e);
        }
    }

    public static CommandRegistry get() {
        return INSTANCE;
    }
}
