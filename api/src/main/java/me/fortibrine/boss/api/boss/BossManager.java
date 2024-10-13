package me.fortibrine.boss.api.boss;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.fortibrine.boss.api.BossApi;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class BossManager implements Listener {

    private final BossApi api;
    public BossManager(BossApi api) {
        this.api = api;
    }

    private final Map<String, Entity> entities = new HashMap<>();
    private final Table<String, Player, Double> damage = HashBasedTable.create();

    public Entity createEntity(Boss boss) {

        Entity entity = boss.getLocation().getWorld().spawnEntity(
                boss.getLocation(), boss.getEntityType()
        );

        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.setMaxHealth(boss.getMaxHealth());
        livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(boss.getDamage());

        entities.put(boss.getId(), entity);

        return entity;

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (!(damager instanceof Player player)) return;

        entities.forEach((key, value) -> {
            if (value.getUniqueId().equals(event.getEntity().getUniqueId())) {
                damage.put(
                        key,
                        player,
                        damage.contains(key, player) ?
                                damage.get(key, player) + event.getFinalDamage() :
                                event.getFinalDamage()
                );

                player.sendActionBar(
                        MiniMessage.miniMessage().deserialize(
                                api.getConfig().getString("actionbar", ""),
                                Placeholder.unparsed("remain", String.valueOf(((LivingEntity) event.getEntity()).getHealth())),
                                Placeholder.unparsed("max", String.valueOf(((LivingEntity) event.getEntity()).getMaxHealth()))
                        ).asComponent()
                );
            }
        });
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        new HashMap<>(entities).forEach((key, value) -> {
            if (value.getUniqueId().equals(event.getEntity().getUniqueId())) {
                entities.remove(key);
            }
        });
    }

}
