package me.fortibrine.boss.bosses;

import me.fortibrine.boss.BossPlugin;
import me.fortibrine.boss.api.BossApi;
import me.fortibrine.boss.api.boss.Boss;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftZombie;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class Summoner {

    private final BossPlugin plugin;
    private final BossApi api;

    public Summoner(BossPlugin plugin, BossApi api) {
        this.plugin = plugin;
        this.api = api;

        FileConfiguration config = plugin.getConfig();

        Boss boss = new Boss(
                "summoner",
                EntityType.valueOf(config.getString("summoner.type")),
                config.getLocation("summoner.location"),
                config.getInt("summoner.spawn-time"),
                config.getString("summoner.display-name"),
                config.getInt("summoner.max-health"),
                config.getInt("summoner.damage"),
                (consumedEntity) -> {
                    LivingEntity livingEntity = (LivingEntity) consumedEntity;

                    Zombie zombie = (Zombie) livingEntity;
                    zombie.setAdult();
                    zombie.setShouldBurnInDay(false);
                    zombie.getEquipment().setItemInMainHand(
                            new ItemStack(Material.BONE)
                    );

                    applyGoals(zombie);
                }
        );

        api.getBossManager().createEntity(
                boss
        );

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int countZombies = ThreadLocalRandom.current().nextInt(2) + 1;
            for (int i = 0; i < countZombies; i++) {
                Zombie zombie = (Zombie) boss.getLocation().getWorld().spawnEntity(boss.getLocation(), EntityType.ZOMBIE);
                zombie.setBaby();
                zombie.setShouldBurnInDay(false);
                applyGoals(zombie);
            }

        }, 60 * 20L, 60 * 20L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Entity summoner = api.getBossManager().getBoss("summoner");
            if (summoner == null) return;

            LivingEntity livingEntity = (LivingEntity) summoner;
            EntityEquipment equipment = livingEntity.getEquipment();

            equipment.setHelmet(new ItemStack(Material.LEATHER_HELMET) {{
                this.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }});
            equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE) {{
                this.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }});
            equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS) {{
                this.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }});
            equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS) {{
                this.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }});
            equipment.setItemInMainHand(new ItemStack(Material.STONE_SWORD) {{
                this.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            }});

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                equipment.setHelmet(null);
                equipment.setChestplate(null);
                equipment.setLeggings(null);
                equipment.setBoots(null);
                equipment.setItemInMainHand(new ItemStack(Material.BONE));
            }, 5 * 60 * 20L);
        }, 0L, 600 * 20L);
    }

    private void applyGoals(Zombie zombie) {

        net.minecraft.world.entity.monster.Zombie nmsZombie = ((CraftZombie) zombie).getHandle();

        nmsZombie.targetSelector.removeAllGoals((goal -> true));
        nmsZombie.goalSelector.removeAllGoals((goal -> true));

        nmsZombie.goalSelector.addGoal(2, new ZombieAttackGoal(nmsZombie, 1.0D, false));
        nmsZombie.goalSelector.addGoal(8, new LookAtPlayerGoal(nmsZombie, Player.class, 8.0F));
        nmsZombie.targetSelector.addGoal(1, new HurtByTargetGoal(nmsZombie));
        nmsZombie.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(nmsZombie, Player.class, true));
        nmsZombie.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(nmsZombie, Wolf.class, true, (wolf) -> {
            return ((Wolf) wolf).getOwner() != null;
        }));
        nmsZombie.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(nmsZombie, Ocelot.class, true));
        nmsZombie.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(nmsZombie, Parrot.class, true, (parrot) -> {
            return ((Parrot) parrot).getOwner() != null;
        }));
    }

}
