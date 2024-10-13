package me.fortibrine.boss.api.statistics.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable
@AllArgsConstructor
@Getter
@Setter
public class BossStatistics {

    @DatabaseField(canBeNull = false)
    private final String id;

    @DatabaseField(canBeNull = false)
    private final short time;

    @DatabaseField(canBeNull = false)
    private final String bestPlayers;

}
