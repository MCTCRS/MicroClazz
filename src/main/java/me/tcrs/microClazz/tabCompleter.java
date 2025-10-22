package me.tcrs.microClazz;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



public class tabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            return List.of("load","unload","invoke","list", "help");
        }

        else if (args[0].equalsIgnoreCase("load")) {

            if (args.length == 2) return filePathList.listAllFiles(new File(MicroClazz.mainDataFolder, "scripts"));

            return List.of("");

        }

        else if (args[0].equalsIgnoreCase("unload")) {

            if (args.length == 2) return new ArrayList<>(MicroClazz.loadedClassData.keySet());
            return List.of("");

        }


        else if (args[0].equalsIgnoreCase("invoke")) {

            if (args.length == 2) return new ArrayList<>(MicroClazz.loadedClassData.keySet());


            if (args.length == 3 && MicroClazz.loadedClassData.containsKey(args[1])) {
                Map<Byte, Object> classobj = MicroClazz.loadedClassData.get(args[1]);
                Class<?> cls = (Class<?>) classobj.get(Keys.CLS);
                return Arrays.stream(cls.getDeclaredMethods())
                        .filter(method -> method.getParameterCount() == 0) // Filter for no-arg methods
                        .map(Method::getName) // Get the method names
                        .toList();


            }
        }

        return completions;//filePathList.listAllFiles(new File(MicroClazz.mainDataFolder, "scripts"));
    }
}
