package me.fortibrine.boss.api.boss;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import me.fortibrine.boss.api.BossApi;
import me.fortibrine.boss.api.statistics.PlayerDamage;
import me.fortibrine.boss.api.statistics.table.BossStatistics;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.SimpleDateFormat;
import java.util.*;

public class BossManager implements Listener {

    private final BossApi api;
    public BossManager(BossApi api) {
        this.api = api;

        api.getServer().getScheduler().scheduleSyncRepeatingTask(api, this::setTextDisplayEntities, 20L, 20L);
    }

    private final Map<String, Boss> bosses = new HashMap<>();
    private final Map<String, Entity> entities = new HashMap<>();
    private final Table<String, Player, Double> damage = HashBasedTable.create();

    private final Map<String, TextDisplay> holograms = new HashMap<>();
    private final Map<String, Long> time = new HashMap<>();
    private final Map<String, Long> spawnTime = new HashMap<>();

    public Entity createEntity(Boss boss) {

        if (entities.containsKey(boss.getId())) {
            entities.get(boss.getId()).remove();
            entities.remove(boss.getId());
        }

        if (holograms.containsKey(boss.getId())) {
            holograms.get(boss.getId()).remove();
            holograms.remove(boss.getId());
        }

        time.remove(boss.getId());

        Entity entity = boss.getLocation().getWorld().spawnEntity(
                boss.getLocation(), boss.getEntityType()
        );

        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.setCustomNameVisible(true);
        livingEntity.customName(MiniMessage.miniMessage().deserialize(boss.getDisplayName()).asComponent());
        livingEntity.setMaxHealth(boss.getMaxHealth());
        livingEntity.setHealth(boss.getMaxHealth());
        livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(boss.getDamage());

        bosses.put(boss.getId(), boss);
        entities.put(boss.getId(), entity);
        spawnTime.put(boss.getId(), System.currentTimeMillis());

        boss.getConsumer().accept(entity);

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
                                Placeholder.unparsed("remain", String.format("%.0f", ((LivingEntity) event.getEntity()).getHealth())),
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
                createHologram(bosses.get(key));
                time.put(
                        bosses.get(key).getId(),
                        System.currentTimeMillis() + bosses.get(key).getSpawnTime() * 1000L
                );

                Map<Player, Double> playerDamage = damage.row(key);

                List<TagResolver> resolvers = new ArrayList<>() {{
                    add(
                            Placeholder.unparsed("display-name", bosses.get(key).getDisplayName())
                    );
                }};

                List<Map.Entry<Player, Double>> bestDamage = playerDamage.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(3)
                        .toList();

                BossManager.this.api.getStatisticsManager().putStatistics(
                        key,
                        new BossStatistics(
                                key,
                                System.currentTimeMillis() - spawnTime.get(key),
                                new Gson().toJson(
                                        bestDamage.stream()
                                                .map((entry) -> new PlayerDamage(entry.getKey().getName(), entry.getValue()))
                                                .toList()
                                )
                        )
                );

                for (int i = 1; i <= bestDamage.size(); i++) {
                    resolvers.add(
                            Placeholder.unparsed("player-" + i, bestDamage.get(i - 1).getKey().getName())
                    );

                    resolvers.add(
                            Placeholder.unparsed("player-damage-" + i, String.format("%.0f", bestDamage.get(i - 1).getValue()))
                    );
                }

                Bukkit.broadcast(
                        MiniMessage.miniMessage().deserialize(
                                String.join(
                                        "<newline>",

                                        api.getConfig().getStringList("messages.boss-killed")
                                ),
                                resolvers.toArray(new TagResolver[0])
                        )
                );

                damage.row(bosses.get(key).getId()).forEach((player, damage) -> {
                    BossManager.this.damage.remove(bosses.get(key).getId(), player);
                });
            }
        });

    }

    public void disable() {
        entities.forEach((key, value) -> {
            value.remove();
        });

        holograms.forEach((key, value) -> {
            value.remove();
        });
    }

    public void setTextDisplayEntities() {

        new HashMap<>(holograms).forEach((key, entity) -> {

            if (time.get(key) == null) {
                entity.remove();
                holograms.remove(key);
                return;
            }

            if (System.currentTimeMillis() > time.get(key)) {
                createEntity(bosses.get(key));
                return;
            }

            Boss boss = bosses.get(key);

            entity.text(MiniMessage.miniMessage().deserialize(
                    String.join(
                            "<newline>",
                            api.getConfig().getStringList("hologram")
                    ),
                    Placeholder.unparsed("display-name", boss.getDisplayName()),
                    Placeholder.unparsed("time",
                            new SimpleDateFormat("mm:ss")
                                    .format(new Date(time.get(key) - System.currentTimeMillis()))
//                            String.valueOf((time.get(key) - System.currentTimeMillis()) / 1000)
                    )
            ).asComponent());
        });
    }

    public void createHologram(Boss boss) {
        Location location = boss.getLocation();
        World world = location.getWorld();

        TextDisplay display = world.spawn(location, TextDisplay.class, entity -> {
            entity.setBillboard(Display.Billboard.VERTICAL);
            entity.setBackgroundColor(Color.RED);
        });
        holograms.put(boss.getId(), display);
    }

    public String getBossId(Entity entity) {
        for (Map.Entry<String, Entity> entry : entities.entrySet()) {
            String id = entry.getKey();
            Entity value = entry.getValue();

            if (value.getUniqueId().equals(entity.getUniqueId())) {
                return id;
            }
        }

        return null;
    }

    public Boss getBoss(Entity entity) {
        String bossId = getBossId(entity);

        if (bossId == null) return null;

        return bosses.getOrDefault(bossId, null);
    }

    public Entity getBoss(String bossId) {
        return entities.getOrDefault(bossId, null);
    }

}
