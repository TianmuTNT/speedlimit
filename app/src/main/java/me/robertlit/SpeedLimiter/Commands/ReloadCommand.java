package me.robertlit.SpeedLimiter.Commands;

import me.robertlit.SpeedLimiter.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * 处理插件命令的执行器
 * 提供插件信息显示和配置重载功能
 */
public class ReloadCommand implements CommandExecutor {
    private final Main plugin;

    /**
     * 构造函数，注册命令执行器
     * @param plugin 插件主类实例
     */
    public ReloadCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("speedlimit").setExecutor(this);
        plugin.getLogger().info("已注册命令执行器");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 检查权限
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "您没有权限执行此命令!");
            return true;
        }
        
        // 显示插件信息
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            showPluginInfo(sender);
            return true;
        }
        
        // 重载配置
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig(sender);
            return true;
        }
        
        // 未知命令，显示帮助
        sender.sendMessage(ChatColor.RED + "未知命令! 使用 /speedlimit help 查看帮助");
        return true;
    }
    
    /**
     * 显示插件信息
     */
    private void showPluginInfo(CommandSender sender) {
        String version = this.plugin.getDescription().getVersion();
        sender.sendMessage(ChatColor.GREEN + "您的服务器正在运行 SpeedLimit v" + version + " 由 robertlit 开发");
        sender.sendMessage(ChatColor.GREEN + "插件页面: https://www.spigotmc.org/resources/speedlimit.72343/");
        sender.sendMessage(ChatColor.GREEN + "捐赠链接: https://www.paypal.me/robertlitmc");
        sender.sendMessage(ChatColor.YELLOW + "使用 /speedlimit reload 重载配置");
    }
    
    /**
     * 重载插件配置
     */
    private void reloadConfig(CommandSender sender) {
        try {
            this.plugin.reloadConfig();
            double speedLimit = this.plugin.getConfig().getDouble("max-meters-per-second");
            sender.sendMessage(ChatColor.GREEN + "配置重载成功! 当前速度限制: " + speedLimit + " 方块/秒");
            this.plugin.getLogger().info("配置已被 " + sender.getName() + " 重载");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "配置重载失败: " + e.getMessage());
            this.plugin.getLogger().warning("配置重载失败: " + e.getMessage());
        }
    }
}
