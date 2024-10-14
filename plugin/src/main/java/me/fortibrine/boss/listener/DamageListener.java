package me.fortibrine.boss.listener;

import lombok.AllArgsConstructor;
import me.fortibrine.boss.BossPlugin;
import me.fortibrine.boss.api.BossApi;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class DamageListener implements Listener {

    private final BossPlugin plugin;
    private final BossApi api;

    @EventHandler
    public void damage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        String bossId = api.getBossManager().getBossId(entity);

        if (bossId == null) return;

        if (bossId.equals("summoner")) {
            if (event.getCause() == EntityDamageEvent.DamageCause.POISON || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        String bossId = api.getBossManager().getBossId(entity);

        if (bossId == null) return;

        if (bossId.equals("robber")) {
            Vector vector = new Vector();
            entity.setVelocity(vector);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> entity.setVelocity(vector), 1L);
        }
    }

}
