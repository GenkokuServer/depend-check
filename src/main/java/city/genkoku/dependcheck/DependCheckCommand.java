package city.genkoku.dependcheck;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.*;
import java.util.stream.Collectors;

public class DependCheckCommand implements CommandExecutor, TabExecutor {

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String pluginName = args[0];
            List<PluginInfo> pluginInfoList =
                    Arrays.stream(Bukkit.getPluginManager().getPlugins())
                            .map(Plugin::getDescription)
                            .filter(pluginDescriptionFile -> pluginDescriptionFile.getDepend().contains(pluginName) || pluginDescriptionFile.getSoftDepend().contains(pluginName))
                            .map(pluginDescriptionFile -> new PluginInfo(pluginDescriptionFile.getName(), pluginDescriptionFile.getDepend().contains(pluginName), pluginDescriptionFile.getSoftDepend().contains(pluginName))).collect(Collectors.toList());
            pluginInfoList.forEach(pluginInfo -> sender.sendMessage(pluginInfoList.toString()));
            return true;
        }
        return false;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 0){
            String input = args[0];
            return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .filter(plugin -> plugin.getName().startsWith(input) &&!getOtherDependingPlugins(plugin).isEmpty())
                    .map(Plugin::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<String> getOtherDependingPlugins(Plugin plugin) {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(Plugin::getDescription)
                .filter(pluginDescriptionFile -> pluginDescriptionFile.getDepend().contains(plugin.getName()) || pluginDescriptionFile.getSoftDepend().contains(plugin.getName()))
                .map(PluginDescriptionFile::getName).collect(Collectors.toList());
    }

    private static class PluginInfo {
        private final String pluginName;
        private final boolean depend;
        private final boolean softDepend;

        public PluginInfo(String pluginName, boolean depend, boolean softDepend) {
            this.pluginName = pluginName;
            this.depend = depend;
            this.softDepend = softDepend;
        }

        @Override
        public String toString() {
            return "PluginInfo{" +
                    "pluginName='" + pluginName + '\'' +
                    ", depend=" + depend +
                    ", softDepend=" + softDepend +
                    '}';
        }
    }

}
