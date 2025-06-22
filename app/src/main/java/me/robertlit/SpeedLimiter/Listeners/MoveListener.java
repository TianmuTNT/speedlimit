package me.robertlit.SpeedLimiter.Listeners;

import me.robertlit.SpeedLimiter.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * 监听玩家移动并限制速度
 */
public class MoveListener {
    private final Main plugin;

    public MoveListener(final Main plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::checkPlayerSpeeds, 0L, 20L);
    }

    /**
     * 检查所有玩家的速度并限制超速玩家
     */
    private void checkPlayerSpeeds() {
        double allowedNormal = plugin.getConfig().getDouble("max-meters-per-second");
        double allowedElytra = plugin.getConfig().getDouble("elytra-max-meters-per-second");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            // 跳过有权限绕过限制的玩家
            if (player.hasPermission("speedlimit.bypass")) {
                continue;
            }

            // 检查玩家是否有上一个位置记录且不是刚传送过来的
            if (plugin.getLocs().get(player.getName()) != null && !plugin.getTped().contains(player.getName())) {
                Location prevLocation = plugin.getLocs().get(player.getName()).clone();
                Location newLocation = player.getLocation().clone();

                // 检查是否需要根据飞行状态限制速度
                boolean shouldCheckFlying = !plugin.getConfig().getBoolean("only-flying") || player.isFlying();
                boolean shouldCheckGround = !plugin.getConfig().getBoolean("only-on-ground") || !player.isFlying();
                
                if (shouldCheckFlying && shouldCheckGround) {
                    // 检查世界是否在配置的限速世界列表中
                    if (isWorldMonitored(prevLocation, newLocation)) {
                        Vector movementVector = newLocation.subtract(prevLocation).toVector();
                        
                        // 检查是否是允许的下落速度
                        if (plugin.getConfig().getBoolean("allow-falling-bypass") && 
                            movementVector.clone().normalize().getY() < -0.95d) {
                            plugin.getLocs().remove(player.getName());
                        } else {
                            // 检查速度是否超过限制
                            double distance = movementVector.length();
                            
                            // 检查玩家是否在使用鞘翅飞行
                            boolean isUsingElytra = player.isGliding();
                            double allowedSpeed = isUsingElytra ? allowedElytra : allowedNormal;
                            
                            // 如果鞘翅速度限制设为-1，则不限制鞘翅飞行速度
                            if (isUsingElytra && allowedSpeed < 0) {
                                // 不限制鞘翅飞行速度
                            } else if (distance > allowedSpeed) {
                                handleSpeedLimitViolation(player, prevLocation, distance, allowedSpeed, isUsingElytra);
                            }
                        }
                    }
                }
            }
            
            // 更新玩家位置记录并清除传送标记
            plugin.getLocs().put(player.getName(), player.getLocation().clone());
            plugin.getTped().remove(player.getName());
        }
    }

    /**
     * 检查世界是否在监控列表中
     */
    private boolean isWorldMonitored(Location prevLocation, Location newLocation) {
        return plugin.getConfig().getList("worlds").contains(prevLocation.getWorld().getName()) || 
               plugin.getConfig().getList("worlds").contains(newLocation.getWorld().getName());
    }

    /**
     * 处理超速违规
     */
    private void handleSpeedLimitViolation(Player player, Location prevLocation, double actualSpeed, double allowedSpeed, boolean isUsingElytra) {
        // 记录到控制台
        String flyingType = isUsingElytra ? "鞘翅飞行" : "普通飞行";
        plugin.getLogger().info(
            String.format("玩家 %s 触发了%s速度限制! 实际速度: %.2f 方块/秒, 允许速度: %.2f 方块/秒", 
            player.getName(), flyingType, actualSpeed, allowedSpeed)
        );
        
        // 处理玩家在载具中的情况
        if (player.isInsideVehicle()) {
            Entity vehicle = player.getVehicle();
            player.leaveVehicle();
            Location entityLoc = prevLocation.clone().add(0.0d, 0.5d, 0.0d);
            vehicle.teleport(entityLoc);
            
            if (plugin.getConfig().getBoolean("put-back-on-vehicle")) {
                vehicle.addPassenger(player);
            } else {
                player.teleport(prevLocation);
            }
        } else {
            player.teleport(prevLocation);
        }
        
        // 发送消息给玩家
        String configKey = isUsingElytra ? "elytra-too-fast-message" : "too-fast-message";
        String message = plugin.getConfig().getString(configKey);
        if (message != null && !message.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            
            // 向所有拥有 speedlimit.verbose 权限的玩家发送消息
            String opMessage = ChatColor.translateAlternateColorCodes('&', 
                String.format("&e[SpeedLimit] &c玩家 &6%s &c触发了%s速度限制! 实际速度: %.2f 方块/秒, 允许速度: %.2f 方块/秒", 
                player.getName(), flyingType, actualSpeed, allowedSpeed));
                
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("speedlimit.verbose") && !staff.equals(player)) {
                    staff.sendMessage(opMessage);
                }
            }
        }
    }
}
