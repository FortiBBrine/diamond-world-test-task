package me.fortibrine.boss.api;

import me.fortibrine.boss.api.boss.BossManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BossApi extends JavaPlugin {

    private final BossManager bossManager = new BossManager(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();

    }

}
