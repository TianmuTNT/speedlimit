package me.robertlit.SpeedLimiter.Commands;

import me.robertlit.SpeedLimiter.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

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
        
        // 查看玩家普通飞行VL
        if (args.length == 2 && args[0].equalsIgnoreCase("vlnormal")) {
            showPlayerNormalVL(sender, args[1]);
            return true;
        }
        
        // 查看玩家鞘翅飞行VL
        if (args.length == 2 && args[0].equalsIgnoreCase("vlelytra")) {
            showPlayerElytraVL(sender, args[1]);
            return true;
        }
        
        // 重置玩家VL
        if (args.length == 2 && args[0].equalsIgnoreCase("resetvl")) {
            resetPlayerVL(sender, args[1]);
            return true;
        }
        
        // 重置玩家普通飞行VL
        if (args.length == 2 && args[0].equalsIgnoreCase("resetvlnormal")) {
            resetPlayerNormalVL(sender, args[1]);
            return true;
        }
        
        // 重置玩家鞘翅飞行VL
        if (args.length == 2 && args[0].equalsIgnoreCase("resetvlelytra")) {
            resetPlayerElytraVL(sender, args[1]);
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
        sender.sendMessage(ChatColor.WHITE + "/speedlimit vl <玩家> " + ChatColor.GRAY + "- 查看玩家所有VL");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit vlnormal <玩家> " + ChatColor.GRAY + "- 查看玩家普通飞行VL");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit vlelytra <玩家> " + ChatColor.GRAY + "- 查看玩家鞘翅飞行VL");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit resetvl <玩家> " + ChatColor.GRAY + "- 重置玩家所有VL");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit resetvlnormal <玩家> " + ChatColor.GRAY + "- 重置玩家普通飞行VL");
        sender.sendMessage(ChatColor.WHITE + "/speedlimit resetvlelytra <玩家> " + ChatColor.GRAY + "- 重置玩家鞘翅飞行VL");
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
        int normalVL = this.plugin.getNormalViolation(playerName);
        int elytraVL = this.plugin.getElytraViolation(playerName);
        int totalVL = normalVL + elytraVL;
        
        sender.sendMessage(ChatColor.YELLOW + "玩家 " + ChatColor.WHITE + playerName + ChatColor.YELLOW + " 的VL信息:");
        sender.sendMessage(ChatColor.GREEN + "普通飞行VL: " + ChatColor.WHITE + normalVL);
        sender.sendMessage(ChatColor.GREEN + "鞘翅飞行VL: " + ChatColor.WHITE + elytraVL);
        sender.sendMessage(ChatColor.GREEN + "总VL: " + ChatColor.WHITE + totalVL);
        
        // 显示阈值信息
        int normalKickThreshold = this.plugin.getConfig().getInt("vl-system.normal-flying.kick-threshold");
        int normalBanThreshold = this.plugin.getConfig().getInt("vl-system.normal-flying.ban-threshold");
        int elytraKickThreshold = this.plugin.getConfig().getInt("vl-system.elytra-flying.kick-threshold");
        int elytraBanThreshold = this.plugin.getConfig().getInt("vl-system.elytra-flying.ban-threshold");
        
        sender.sendMessage(ChatColor.YELLOW + "阈值信息:");
        if (normalKickThreshold > 0) {
            sender.sendMessage(ChatColor.GREEN + "普通飞行踢出阈值: " + ChatColor.WHITE + normalKickThreshold);
        }
        if (normalBanThreshold > 0) {
            sender.sendMessage(ChatColor.GREEN + "普通飞行封禁阈值: " + ChatColor.WHITE + normalBanThreshold);
        }
        if (elytraKickThreshold > 0) {
            sender.sendMessage(ChatColor.GREEN + "鞘翅飞行踢出阈值: " + ChatColor.WHITE + elytraKickThreshold);
        }
        if (elytraBanThreshold > 0) {
            sender.sendMessage(ChatColor.GREEN + "鞘翅飞行封禁阈值: " + ChatColor.WHITE + elytraBanThreshold);
        }
        
        // 显示状态
        if (totalVL > 0) {
            boolean normalBanned = normalBanThreshold > 0 && normalVL >= normalBanThreshold;
            boolean normalKicked = normalKickThreshold > 0 && normalVL >= normalKickThreshold;
            boolean elytraBanned = elytraBanThreshold > 0 && elytraVL >= elytraBanThreshold;
            boolean elytraKicked = elytraKickThreshold > 0 && elytraVL >= elytraKickThreshold;
            
            if (normalBanned || elytraBanned) {
                sender.sendMessage(ChatColor.RED + "状态: 已达到封禁阈值!");
            } else if (normalKicked || elytraKicked) {
                sender.sendMessage(ChatColor.GOLD + "状态: 已达到踢出阈值!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "状态: 正常");
            }
        } else {
            sender.sendMessage(ChatColor.GREEN + "状态: 无违规记录");
        }
    }

    /**
     * 显示玩家普通飞行VL信息
     */
    private void showPlayerNormalVL(CommandSender sender, String playerName) {
        int vl = this.plugin.getNormalViolation(playerName);
        int kickThreshold = this.plugin.getConfig().getInt("vl-system.normal-flying.kick-threshold");
        int banThreshold = this.plugin.getConfig().getInt("vl-system.normal-flying.ban-threshold");
        
        sender.sendMessage(ChatColor.YELLOW + "玩家 " + ChatColor.WHITE + playerName + ChatColor.YELLOW + " 的普通飞行VL信息:");
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
     * 显示玩家鞘翅飞行VL信息
     */
    private void showPlayerElytraVL(CommandSender sender, String playerName) {
        int vl = this.plugin.getElytraViolation(playerName);
        int kickThreshold = this.plugin.getConfig().getInt("vl-system.elytra-flying.kick-threshold");
        int banThreshold = this.plugin.getConfig().getInt("vl-system.elytra-flying.ban-threshold");
        
        sender.sendMessage(ChatColor.YELLOW + "玩家 " + ChatColor.WHITE + playerName + ChatColor.YELLOW + " 的鞘翅飞行VL信息:");
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
        int oldNormalVL = this.plugin.getNormalViolation(playerName);
        int oldElytraVL = this.plugin.getElytraViolation(playerName);
        this.plugin.resetAllViolations(playerName);
        
        sender.sendMessage(ChatColor.GREEN + "已重置玩家 " + ChatColor.WHITE + playerName + 
                          ChatColor.GREEN + " 的所有VL (普通飞行: " + oldNormalVL + " → 0, 鞘翅飞行: " + oldElytraVL + " → 0)");
        this.plugin.getLogger().info("玩家 " + playerName + " 的所有VL已被 " + sender.getName() + " 重置");
    }

    /**
     * 重置玩家普通飞行VL
     */
    private void resetPlayerNormalVL(CommandSender sender, String playerName) {
        int oldVL = this.plugin.getNormalViolation(playerName);
        this.plugin.resetNormalViolation(playerName);
        
        sender.sendMessage(ChatColor.GREEN + "已重置玩家 " + ChatColor.WHITE + playerName + 
                          ChatColor.GREEN + " 的普通飞行VL (从 " + oldVL + " 重置为 0)");
        this.plugin.getLogger().info("玩家 " + playerName + " 的普通飞行VL已被 " + sender.getName() + " 重置");
    }

    /**
     * 重置玩家鞘翅飞行VL
     */
    private void resetPlayerElytraVL(CommandSender sender, String playerName) {
        int oldVL = this.plugin.getElytraViolation(playerName);
        this.plugin.resetElytraViolation(playerName);
        
        sender.sendMessage(ChatColor.GREEN + "已重置玩家 " + ChatColor.WHITE + playerName + 
                          ChatColor.GREEN + " 的鞘翅飞行VL (从 " + oldVL + " 重置为 0)");
        this.plugin.getLogger().info("玩家 " + playerName + " 的鞘翅飞行VL已被 " + sender.getName() + " 重置");
    }

    /**
     * 显示所有玩家的VL信息
     */
    private void showAllPlayerVL(CommandSender sender) {
        Map<String, Integer> normalViolations = this.plugin.getPlayerNormalViolations();
        Map<String, Integer> elytraViolations = this.plugin.getPlayerElytraViolations();
        
        if (normalViolations.isEmpty() && elytraViolations.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "当前没有玩家的VL记录");
            return;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "所有玩家的VL信息:");
        
        // 合并所有玩家名称
        Set<String> allPlayers = new HashSet<>();
        allPlayers.addAll(normalViolations.keySet());
        allPlayers.addAll(elytraViolations.keySet());
        
        for (String playerName : allPlayers) {
            int normalVL = normalViolations.getOrDefault(playerName, 0);
            int elytraVL = elytraViolations.getOrDefault(playerName, 0);
            int totalVL = normalVL + elytraVL;
            
            String status = "";
            int normalKickThreshold = this.plugin.getConfig().getInt("vl-system.normal-flying.kick-threshold");
            int normalBanThreshold = this.plugin.getConfig().getInt("vl-system.normal-flying.ban-threshold");
            int elytraKickThreshold = this.plugin.getConfig().getInt("vl-system.elytra-flying.kick-threshold");
            int elytraBanThreshold = this.plugin.getConfig().getInt("vl-system.elytra-flying.ban-threshold");
            
            if ((normalBanThreshold > 0 && normalVL >= normalBanThreshold) || 
                (elytraBanThreshold > 0 && elytraVL >= elytraBanThreshold)) {
                status = ChatColor.RED + "封禁";
            } else if ((normalKickThreshold > 0 && normalVL >= normalKickThreshold) || 
                       (elytraKickThreshold > 0 && elytraVL >= elytraKickThreshold)) {
                status = ChatColor.GOLD + "踢出";
            } else {
                status = ChatColor.GREEN + "正常";
            }
            
            sender.sendMessage(ChatColor.WHITE + playerName + ": " + 
                             ChatColor.YELLOW + "普通飞行" + normalVL + "VL, " +
                             ChatColor.YELLOW + "鞘翅飞行" + elytraVL + "VL " + status);
        }
    }
}
