package me.fortibrine.boss.api.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PlayerDamage {
    private final String name;
    private final double damage;
}
