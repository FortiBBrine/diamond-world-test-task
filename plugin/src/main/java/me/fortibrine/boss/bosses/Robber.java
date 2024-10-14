package me.fortibrine.boss.bosses;

import me.fortibrine.boss.BossPlugin;
import me.fortibrine.boss.api.BossApi;
import me.fortibrine.boss.api.boss.Boss;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Robber {
    private final BossPlugin plugin;
    private final BossApi api;

    public Robber(BossPlugin plugin, BossApi api) {
        this.plugin = plugin;
        this.api = api;

        FileConfiguration config = plugin.getConfig();

        Boss boss = new Boss(
                "robber",
                EntityType.valueOf(config.getString("robber.type")),
                config.getLocation("robber.location"),
                config.getInt("robber.spawn-time"),
                config.getString("robber.display-name"),
                config.getInt("robber.max-health"),
                config.getInt("robber.damage"),
                (consumedEntity) -> {
                    Pillager pillager = (Pillager) consumedEntity;

                    pillager.getEquipment().setItemInMainHand(new ItemStack(Material.CROSSBOW) {{
                        addUnsafeEnchantment(Enchantment.PIERCING, 1);
                        addUnsafeEnchantment(Enchantment.MULTISHOT, 1);
                    }});
                }
        );

        api.getBossManager().createEntity(
                boss
        );

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Entity robber = api.getBossManager().getBoss("robber");
            if (robber == null) return;

            Pillager pillager = (Pillager) robber;

            if (pillager.getHealth() < pillager.getMaxHealth() / 2) {
                pillager.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
            }
        }, 20L, 20L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Entity robber = api.getBossManager().getBoss("robber");
            if (robber == null) return;

            Pillager pillager = (Pillager) robber;

            pillager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10, 0));

            if (pillager.getEquipment().getItemInMainHand().getType().equals(Material.IRON_AXE)) {
                // хз как рывок сделать
                // pillager.setVelocity по идеи
            }
        }, 60 * 20L, 60 * 20L);

    }
}
