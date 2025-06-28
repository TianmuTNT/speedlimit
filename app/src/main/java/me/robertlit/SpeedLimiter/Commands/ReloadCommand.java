package me.robertlit.SpeedLimiter.Commands;

import me.robertlit.SpeedLimiter.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

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
        
        // 查看玩家VL
        if (args.length == 2 && args[0].equalsIgnoreCase("vl")) {
            showPlayerVL(sender, args[1]);
            return true;
        }
        
        // 重置玩家VL
        if (args.length == 2 && args[0].equalsIgnoreCase("resetvl")) {
            resetPlayerVL(sender, args[1]);
            return true;
        }
        
        // 查看所有玩家VL
        if (args.length == 1 && args[0].equalsIgnoreCase("vlall")) {
            showAllPlayerVL(sender);
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
        sender.sendMessage(ChatColor.YELLOW + "命令列表:");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit help " + ChatColor.GRAY + "- 显示此帮助信息");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit reload " + ChatColor.GRAY + "- 重载配置");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit vl <玩家> " + ChatColor.GRAY + "- 查看玩家VL");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit resetvl <玩家> " + ChatColor.GRAY + "- 重置玩家VL");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit vlall " + ChatColor.GRAY + "- 查看所有玩家VL");
    }
    
    /**
     * 重载插件配置
     */
    private void reloadConfig(CommandSender sender) {
        try {
            this.plugin.reloadConfig();
            double normalSpeedLimit = this.plugin.getConfig().getDouble("max-meters-per-second");
            double elytraSpeedLimit = this.plugin.getConfig().getDouble("elytra-max-meters-per-second");
            
            sender.sendMessage(ChatColor.GREEN + "配置重载成功!");
            sender.sendMessage(ChatColor.GREEN + "普通飞行速度限制: " + normalSpeedLimit + " 方块/秒");
            
            if (elytraSpeedLimit < 0) {
                sender.sendMessage(ChatColor.GREEN + "鞘翅飞行速度: 不限制");
            } else {
                sender.sendMessage(ChatColor.GREEN + "鞘翅飞行速度限制: " + elytraSpeedLimit + " 方块/秒");
            }
            
            this.plugin.getLogger().info("配置已被 " + sender.getName() + " 重载");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "配置重载失败: " + e.getMessage());
            this.plugin.getLogger().warning("配置重载失败: " + e.getMessage());
        }
    }

    /**
     * 显示玩家VL信息
     */
    private void showPlayerVL(CommandSender sender, String playerName) {
        int vl = this.plugin.getViolation(playerName);
        int kickThreshold = this.plugin.getConfig().getInt("vl-system.kick-threshold");
        int banThreshold = this.plugin.getConfig().getInt("vl-system.ban-threshold");
        
        sender.sendMessage(ChatColor.YELLOW + "玩家 " + ChatColor.WHITE + playerName + ChatColor.YELLOW + " 的VL信息:");
        sender.sendMessage(ChatColor.GREEN + "当前VL: " + ChatColor.WHITE + vl);
        
        if (kickThreshold > 0) {
            sender.sendMessage(ChatColor.GREEN + "踢出阈值: " + ChatColor.WHITE + kickThreshold);
        }
        if (banThreshold > 0) {
            sender.sendMessage(ChatColor.GREEN + "封禁阈值: " + ChatColor.WHITE + banThreshold);
        }
        
        if (vl > 0) {
            if (banThreshold > 0 && vl >= banThreshold) {
                sender.sendMessage(ChatColor.RED + "状态: 已达到封禁阈值!");
            } else if (kickThreshold > 0 && vl >= kickThreshold) {
                sender.sendMessage(ChatColor.GOLD + "状态: 已达到踢出阈值!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "状态: 正常");
            }
        } else {
            sender.sendMessage(ChatColor.GREEN + "状态: 无违规记录");
        }
    }

    /**
     * 重置玩家VL
     */
    private void resetPlayerVL(CommandSender sender, String playerName) {
        int oldVL = this.plugin.getViolation(playerName);
        this.plugin.resetViolation(playerName);
        
        sender.sendMessage(ChatColor.GREEN + "已重置玩家 " + ChatColor.WHITE + playerName + 
                          ChatColor.GREEN + " 的VL (从 " + oldVL + " 重置为 0)");
        this.plugin.getLogger().info("玩家 " + playerName + " 的VL已被 " + sender.getName() + " 重置");
    }

    /**
     * 显示所有玩家的VL信息
     */
    private void showAllPlayerVL(CommandSender sender) {
        Map<String, Integer> violations = this.plugin.getPlayerViolations();
        
        if (violations.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "当前没有玩家的VL记录");
            return;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "所有玩家的VL信息:");
        violations.forEach((playerName, vl) -> {
            String status;
            int kickThreshold = this.plugin.getConfig().getInt("vl-system.kick-threshold");
            int banThreshold = this.plugin.getConfig().getInt("vl-system.ban-threshold");
            
            if (banThreshold > 0 && vl >= banThreshold) {
                status = ChatColor.RED + "封禁";
            } else if (kickThreshold > 0 && vl >= kickThreshold) {
                status = ChatColor.GOLD + "踢出";
            } else {
                status = ChatColor.GREEN + "正常";
            }
            
            sender.sendMessage(ChatColor.WHITE + playerName + ": " + ChatColor.YELLOW + vl + " VL " + status);
        });
    }
}
