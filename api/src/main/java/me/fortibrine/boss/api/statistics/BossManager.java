package me.fortibrine.boss.api.statistics;

import me.fortibrine.boss.api.statistics.table.BossStatistics;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BossManager {
    public @NotNull List<BossStatistics> getStatistics(@NotNull String id);
    public void putStatistics(@NotNull String id, @NotNull BossStatistics statistics);
}
