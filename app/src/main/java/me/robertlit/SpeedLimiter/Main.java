package me.robertlit.SpeedLimiter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.robertlit.SpeedLimiter.Commands.ReloadCommand;
import me.robertlit.SpeedLimiter.Listeners.JoinListener;
import me.robertlit.SpeedLimiter.Listeners.MoveListener;
import me.robertlit.SpeedLimiter.Listeners.TpListener;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SpeedLimit插件主类
 * 限制玩家移动速度，防止玩家使用作弊客户端高速移动
 */
public class Main extends JavaPlugin {
    // 存储玩家上一次位置的映射
    private final Map<String, Location> playerLocations = new HashMap<>();
    // 存储最近被传送的玩家，避免传送后被误判为超速
    private final Set<String> teleportedPlayers = new HashSet<>();
    // 存储玩家普通飞行VL（违规次数）的映射
    private final Map<String, Integer> playerNormalViolations = new HashMap<>();
    // 存储玩家鞘翅飞行VL（违规次数）的映射
    private final Map<String, Integer> playerElytraViolations = new HashMap<>();
    // 存储玩家普通飞行VL时间戳的映射（用于重置VL）
    private final Map<String, Long> playerNormalViolationTimestamps = new HashMap<>();
    // 存储玩家鞘翅飞行VL时间戳的映射（用于重置VL）
    private final Map<String, Long> playerElytraViolationTimestamps = new HashMap<>();

    /**
     * 获取玩家位置映射
     */
    public Map<String, Location> getLocs() {
        return this.playerLocations;
    }

    /**
     * 获取最近传送的玩家集合
     */
    public Set<String> getTped() {
        return this.teleportedPlayers;
    }

    /**
     * 获取玩家普通飞行VL映射
     */
    public Map<String, Integer> getPlayerNormalViolations() {
        return this.playerNormalViolations;
    }

    /**
     * 获取玩家鞘翅飞行VL映射
     */
    public Map<String, Integer> getPlayerElytraViolations() {
        return this.playerElytraViolations;
    }

    /**
     * 获取玩家普通飞行VL时间戳映射
     */
    public Map<String, Long> getPlayerNormalViolationTimestamps() {
        return this.playerNormalViolationTimestamps;
    }

    /**
     * 获取玩家鞘翅飞行VL时间戳映射
     */
    public Map<String, Long> getPlayerElytraViolationTimestamps() {
        return this.playerElytraViolationTimestamps;
    }

    /**
     * 增加玩家普通飞行VL
     */
    public void addNormalViolation(String playerName) {
        int currentVL = playerNormalViolations.getOrDefault(playerName, 0);
        playerNormalViolations.put(playerName, currentVL + 1);
        playerNormalViolationTimestamps.put(playerName, System.currentTimeMillis());
    }

    /**
     * 增加玩家鞘翅飞行VL
     */
    public void addElytraViolation(String playerName) {
        int currentVL = playerElytraViolations.getOrDefault(playerName, 0);
        playerElytraViolations.put(playerName, currentVL + 1);
        playerElytraViolationTimestamps.put(playerName, System.currentTimeMillis());
    }

    /**
     * 获取玩家普通飞行VL
     */
    public int getNormalViolation(String playerName) {
        return playerNormalViolations.getOrDefault(playerName, 0);
    }

    /**
     * 获取玩家鞘翅飞行VL
     */
    public int getElytraViolation(String playerName) {
        return playerElytraViolations.getOrDefault(playerName, 0);
    }

    /**
     * 重置玩家普通飞行VL
     */
    public void resetNormalViolation(String playerName) {
        playerNormalViolations.remove(playerName);
        playerNormalViolationTimestamps.remove(playerName);
    }

    /**
     * 重置玩家鞘翅飞行VL
     */
    public void resetElytraViolation(String playerName) {
        playerElytraViolations.remove(playerName);
        playerElytraViolationTimestamps.remove(playerName);
    }

    /**
     * 重置玩家所有VL
     */
    public void resetAllViolations(String playerName) {
        resetNormalViolation(playerName);
        resetElytraViolation(playerName);
    }

    @Override
    public void onEnable() {
        // 保存默认配置
        saveDefaultConfig();
        
        // 注册监听器和命令
        getLogger().info("正在注册监听器和命令...");
        new MoveListener(this);
        new TpListener(this);
        new ReloadCommand(this);
        new JoinListener(this);
        
        // 启动VL重置任务
        startViolationResetTask();
        
        // 输出启动信息
        double normalSpeedLimit = getConfig().getDouble("max-meters-per-second");
        double elytraSpeedLimit = getConfig().getDouble("elytra-max-meters-per-second");
        
        getLogger().info("SpeedLimit插件已启动!");
        getLogger().info("普通飞行速度限制: " + normalSpeedLimit + " 方块/秒");
        
        if (elytraSpeedLimit < 0) {
            getLogger().info("鞘翅飞行速度: 不限制");
        } else {
            getLogger().info("鞘翅飞行速度限制: " + elytraSpeedLimit + " 方块/秒");
        }
        
        // 输出VL系统信息
        int normalKickThreshold = getConfig().getInt("vl-system.normal-flying.kick-threshold");
        int normalBanThreshold = getConfig().getInt("vl-system.normal-flying.ban-threshold");
        int elytraKickThreshold = getConfig().getInt("vl-system.elytra-flying.kick-threshold");
        int elytraBanThreshold = getConfig().getInt("vl-system.elytra-flying.ban-threshold");
        int resetMinutes = getConfig().getInt("vl-system.vl-reset-minutes");
        
        if (normalKickThreshold > 0) {
            getLogger().info("普通飞行VL踢出阈值: " + normalKickThreshold);
        }
        if (normalBanThreshold > 0) {
            getLogger().info("普通飞行VL封禁阈值: " + normalBanThreshold);
        }
        if (elytraKickThreshold > 0) {
            getLogger().info("鞘翅飞行VL踢出阈值: " + elytraKickThreshold);
        }
        if (elytraBanThreshold > 0) {
            getLogger().info("鞘翅飞行VL封禁阈值: " + elytraBanThreshold);
        }
        if (resetMinutes > 0) {
            getLogger().info("VL重置时间: " + resetMinutes + " 分钟");
        }
    }

    /**
     * 启动VL重置任务
     */
    private void startViolationResetTask() {
        int resetMinutes = getConfig().getInt("vl-system.vl-reset-minutes");
        if (resetMinutes <= 0) {
            return; // 不重置
        }
        
        long resetInterval = resetMinutes * 60 * 20L; // 转换为tick
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            long currentTime = System.currentTimeMillis();
            long resetTime = resetMinutes * 60 * 1000L; // 转换为毫秒
            
            playerNormalViolationTimestamps.entrySet().removeIf(entry -> {
                if (currentTime - entry.getValue() >= resetTime) {
                    String playerName = entry.getKey();
                    playerNormalViolations.remove(playerName);
                    getLogger().info("玩家 " + playerName + " 的普通飞行VL已重置");
                    return true;
                }
                return false;
            });

            playerElytraViolationTimestamps.entrySet().removeIf(entry -> {
                if (currentTime - entry.getValue() >= resetTime) {
                    String playerName = entry.getKey();
                    playerElytraViolations.remove(playerName);
                    getLogger().info("玩家 " + playerName + " 的鞘翅飞行VL已重置");
                    return true;
                }
                return false;
            });
        }, 20L * 60, resetInterval); // 每分钟检查一次
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SpeedLimit插件已关闭!");
    }
}
