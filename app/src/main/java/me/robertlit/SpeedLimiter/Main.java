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
        
        // 输出启动信息
        double speedLimit = getConfig().getDouble("max-meters-per-second");
        getLogger().info("SpeedLimit插件已启动! 当前速度限制: " + speedLimit + " 方块/秒");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SpeedLimit插件已关闭!");
    }
}
