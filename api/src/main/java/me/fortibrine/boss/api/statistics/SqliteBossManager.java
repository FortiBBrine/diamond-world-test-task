package me.fortibrine.boss.api.statistics;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.SneakyThrows;
import me.fortibrine.boss.api.statistics.table.BossStatistics;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SqliteBossManager implements BossManager {

    private final ConnectionSource source;
    private final Dao<BossStatistics, String> dao;

    @SneakyThrows
    public SqliteBossManager(@NotNull String jdbcUrl) {
        this.source = new JdbcPooledConnectionSource(jdbcUrl);
        TableUtils.createTableIfNotExists(source, BossStatistics.class);
        this.dao = DaoManager.createDao(source, BossStatistics.class);
    }

    @Override
    @SneakyThrows
    public @NotNull List<BossStatistics> getStatistics(@NotNull String id) {
        return this.dao.queryBuilder()
                .where()
                .eq("id", id)
                .query();
    }

    @Override
    @SneakyThrows
    public void putStatistics(@NotNull String id, @NotNull BossStatistics statistics) {
        this.dao.create(statistics);
    }
}
