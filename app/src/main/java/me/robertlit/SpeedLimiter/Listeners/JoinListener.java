package me.robertlit.SpeedLimiter.Listeners;

import me.robertlit.SpeedLimiter.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 处理玩家加入服务器事件的监听器
 * 主要用于初始化玩家位置记录
 */
public class JoinListener implements Listener {
    private final Main plugin;

    /**
     * 构造函数，注册事件监听器
     * @param plugin 插件主类实例
     */
    public JoinListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("已注册玩家加入事件监听器");
    }

    /**
     * 处理玩家加入事件
     * 初始化玩家位置记录
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 初始化玩家位置记录
        this.plugin.getLocs().put(player.getName(), player.getLocation().clone());
    }
}
