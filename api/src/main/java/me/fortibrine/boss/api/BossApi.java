package me.fortibrine.boss.api;

import lombok.Getter;
import me.fortibrine.boss.api.boss.BossManager;
import me.fortibrine.boss.api.statistics.SqliteBossManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BossApi extends JavaPlugin {

    private BossManager bossManager;
    private me.fortibrine.boss.api.statistics.BossManager statisticsManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        bossManager = new BossManager(this);
        statisticsManager = new SqliteBossManager(getConfig().getString("jdbc-url"));
        getServer().getPluginManager().registerEvents(bossManager, this);
    }

    @Override
    public void onDisable() {
        bossManager.disable();
        getServer().getScheduler().cancelTasks(this);
        bossManager = null;
    }

}
