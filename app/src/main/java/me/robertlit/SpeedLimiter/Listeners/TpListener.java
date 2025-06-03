package me.robertlit.SpeedLimiter.Listeners;

import me.robertlit.SpeedLimiter.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * 处理玩家传送相关事件的监听器
 * 用于标记玩家传送状态，防止传送后被误判为超速
 */
public class TpListener implements Listener {
    private final Main plugin;

    /**
     * 构造函数，注册事件监听器
     * @param plugin 插件主类实例
     */
    public TpListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("已注册传送事件监听器");
    }

    /**
     * 处理玩家重生事件
     * 将玩家标记为已传送状态，避免重生后被误判为超速
     */
    @EventHandler
    public void onDeath(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        this.plugin.getTped().add(player.getName());
        this.plugin.getLogger().fine("玩家 " + player.getName() + " 重生，已标记为传送状态");
    }

    /**
     * 处理玩家传送事件
     * 将玩家标记为已传送状态，避免传送后被误判为超速
     */
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        this.plugin.getTped().add(player.getName());
        this.plugin.getLogger().fine("玩家 " + player.getName() + " 传送，已标记为传送状态");
    }

    /**
     * 处理玩家退出事件
     * 清除玩家的传送状态和位置记录，释放内存
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        this.plugin.getTped().remove(playerName);
        this.plugin.getLocs().remove(playerName);
        this.plugin.getLogger().fine("玩家 " + playerName + " 退出，已清除相关数据");
    }
}
