package me.fortibrine.boss;

import me.fortibrine.boss.api.BossApi;
import me.fortibrine.boss.bosses.Robber;
import me.fortibrine.boss.bosses.Summoner;
import me.fortibrine.boss.listener.DamageListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BossPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        BossApi api = (BossApi) getServer().getPluginManager().getPlugin("BossApi");

        if (api == null) {
            getServer().shutdown();
            return;
        }

        new Summoner(this, api);
        new Robber(this, api);

        List.of(
                new DamageListener(this, api)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

    }

    @Override
    public void onDisable() {

    }

}
