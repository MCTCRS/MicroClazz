package me.tcrs.microClazz;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Arrays;

public class commandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("microclazz")) {

            if (!sender.hasPermission("microclazz.use")) {
                sender.sendMessage("§8[§7MicroClazz§8] §fMissing permission: §cmicroclazz.use");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("§8[§7MicroClazz§8] §fUsage: /microclazz [load/unload/invoke/list/help]");
                return true;
            }

            String opType = args[0];
            switch (opType) {
                case "help": {
                    if (args.length > 1) {
                        sender.sendMessage("§8[§7MicroClazz§8] §fUsage: /microclazz help");
                        return true;
                    }

                    sender.sendMessage("§8[§7MicroClazz§8] §fMicroClazz Commands:");
                    sender.sendMessage("§8 - §f/microclazz load <filePath> §7- Load a class from a .class or .java file.");
                    sender.sendMessage("§8 - §f/microclazz unload <filePath> §7- Unload a previously loaded class.");
                    sender.sendMessage("§8 - §f/microclazz invoke <filePath> <method> §7- Invoke a method from a loaded class.");
                    sender.sendMessage("§8 - §f/microclazz list §7- List all currently loaded classes.");
                    sender.sendMessage("");
                    sender.sendMessage("§8[§7MicroClazz§8] §fScript Lifecycle:");
                    sender.sendMessage("§8   - §fonScriptLoad() §7- Called when a script is successfully loaded.");
                    sender.sendMessage("§8   - §fonScriptUnLoad() §7- Called when a script is unloaded.");
                    sender.sendMessage("");
                    sender.sendMessage("§8[§7MicroClazz§8] §fConfiguration:");
                    sender.sendMessage("§8   - §frun_on_enable: §7Commands to run when the plugin is enabled (e.g., 'say enable').");

                    break;
                }



                case "load": {

                    Bukkit.getScheduler().runTaskAsynchronously(MicroClazz.instance, () -> {

                        if (args.length != 2) {
                            sender.sendMessage("§8[§7MicroClazz§8] §fUsage: /microclazz load <filePath>");
                            return;
                        }

                        String path = args[1];
                        if (path.endsWith(".class")) {
                            try {
                                //sender.sendMessage("task: load from .class");

                                sender.sendMessage("§8[§7MicroClazz§8] §fLoading " + path + "...");
                                String loadWarning = ScriptManager.loadClass(new File(MicroClazz.mainDataFolder, "scripts/" + path), path);
                                if (!loadWarning.isEmpty())
                                    sender.sendMessage("§8[§7MicroClazz§8] §e" + loadWarning);

                                sender.sendMessage("§8[§7MicroClazz§8] §fLoaded '" + path + "' Successfully.");
                            } catch (Exception e) {
                                String errorType = e.getClass().getSimpleName();
                                String message = e.getMessage();
                                sender.sendMessage("§8[§7MicroClazz§8] §c" + errorType + ": " + (message != null ? message : "Something went wrong"));
                                e.printStackTrace();
                            }
                        } else if (path.endsWith(".java")) {
                            try {
                                //sender.sendMessage("task: load from .java");

                                sender.sendMessage("§8[§7MicroClazz§8] §fCompiling " + path + "...");
                                if (ScriptManager.compileJava(new File(MicroClazz.mainDataFolder, "scripts/" + path)) == 1)
                                    sender.sendMessage("§8[§7MicroClazz§8] §e" + path + " has not change, skip compiling.");

                                sender.sendMessage("§8[§7MicroClazz§8] §fLoading " + path.replaceFirst("\\.java$", ".class") + "...");
                                String loadWarning = ScriptManager.loadClass(new File(MicroClazz.mainDataFolder, "scripts/" + path.replaceFirst("\\.java$", ".class")), path.replaceFirst("\\.java$", ".class"));
                                if (!loadWarning.isEmpty())
                                    sender.sendMessage("§8[§7MicroClazz§8] §e" + loadWarning);

                                sender.sendMessage("§8[§7MicroClazz§8] §fLoaded '" + path + "' Successfully.");

                            } catch (Exception e) {
                                String errorType = e.getClass().getSimpleName();
                                String message = e.getMessage();
                                sender.sendMessage("§8[§7MicroClazz§8] §c" + errorType + ": " + (message != null ? message : "Something went wrong"));
                                e.printStackTrace();
                            }
                        } else {
                            sender.sendMessage("§8[§7MicroClazz§8] §fOnly accept .java or .class files");
                            return;
                        }


                        return;
                    });
                    break;
                }



                case "unload": {
                    if (args.length != 2) {
                        sender.sendMessage("§8[§7MicroClazz§8] §fUsage: /microclazz unload <filePath>");
                        return true;
                    }
                    String unload_path = args[1];
                    try {
                        ScriptManager.unloadClass(unload_path);
                        sender.sendMessage("§8[§7MicroClazz§8] §fSuccessfully unload '" + unload_path + "'.");
                    } catch (Exception e) {
                        String errorType = e.getClass().getSimpleName();
                        String message = e.getMessage();
                        sender.sendMessage("§8[§7MicroClazz§8] §c" + errorType + ": " + (message != null ? message : "Something went wrong"));
                        e.printStackTrace();
                    }
                    break;
                }

                    case "invoke":{
                        if (args.length <= 2) {
                            sender.sendMessage("§8[§7MicroClazz§8] §fUsage: /microclazz invoke <filePath> <method>");
                            return true;
                        }

                        try {
                            ScriptManager.invokeClass(args[1], args[2]);
                        } catch (Exception e) {
                            String errorType = e.getClass().getSimpleName();
                            String message = e.getMessage();
                            sender.sendMessage("§8[§7MicroClazz§8] §c" + errorType + ": " + (message != null ? message : "Something went wrong"));
                            e.printStackTrace();
                        }

                        break;
                }

                case "list": {
                    if (args.length > 1) {
                        sender.sendMessage("§8[§7MicroClazz§8] §fUsage: /microclazz list");
                        return true;
                    }
                    if (MicroClazz.loadedClassData.keySet().isEmpty()) {
                        sender.sendMessage("§8[§7MicroClazz§8] §fNo Class is loaded.");
                        return true;
                    }

                    sender.sendMessage("§8[§7MicroClazz§8] §fLoaded Classes:");
                    for (String cls : MicroClazz.loadedClassData.keySet()) {
                        sender.sendMessage("§8 - §f" + cls + (MicroClazz.loadedClassData.get(cls).containsKey(Keys.INSTANCE) ? " §8(Listener)" : ""));
                    }
                    break;
                }

                default:
                    sender.sendMessage("§8[§7MicroClazz§8] §fUsage: /microclazz [load/unload/invoke/list]");
            }
            return true; // return true if command was handled
        }
        return false; // false if not handled, Bukkit will show usage
    }
}
