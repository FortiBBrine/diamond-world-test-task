package me.fortibrine.boss.api.statistics.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

@DatabaseTable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BossStatistics {

    @DatabaseField(canBeNull = false)
    private String id;

    @DatabaseField(canBeNull = false)
    private long time;

    @DatabaseField(canBeNull = false)
    private String bestPlayers;

}
