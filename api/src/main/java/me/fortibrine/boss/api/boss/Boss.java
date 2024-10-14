package me.fortibrine.boss.api.boss;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.function.Consumer;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class Boss {
    private final String id;
    private final EntityType entityType;
    private final Location location;
    private final int spawnTime;
    private final String displayName;
    private final int maxHealth;
    private final int damage;
    private final Consumer<Entity> consumer;
}
