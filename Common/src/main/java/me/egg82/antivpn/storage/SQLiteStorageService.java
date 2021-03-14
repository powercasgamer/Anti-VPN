package me.egg82.antivpn.storage;

import com.zaxxer.hikari.HikariConfig;
import io.ebean.config.dbplatform.sqlite.SQLitePlatform;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.jetbrains.annotations.NotNull;

public class SQLiteStorageService extends AbstractJDBCStorageService {
    private SQLiteStorageService(@NotNull String name) {
        super(name);
    }

    public static @NotNull SQLiteStorageService.Builder builder(@NotNull String name) {
        return new SQLiteStorageService.Builder(name);
    }

    public static class Builder {
        private final SQLiteStorageService service;
        private final HikariConfig config = new HikariConfig();

        private Builder(@NotNull String name) {
            service = new SQLiteStorageService(name);

            // Baseline
            config.setPoolName("Anti-VPN_SQLite");
            config.setDriverClassName("org.sqlite.JDBC");
            config.setConnectionTestQuery("SELECT 1;");
            config.addDataSourceProperty("useLegacyDatetimeCode", "false");
            config.addDataSourceProperty("serverTimezone", "UTC");
        }

        public @NotNull SQLiteStorageService.Builder file(@NotNull File file) {
            config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
            return this;
        }

        public @NotNull SQLiteStorageService.Builder options(@NotNull String options) throws IOException {
            options = !options.isEmpty() && options.charAt(0) == '?' ? options.substring(1) : options;
            Properties p = new Properties();
            p.load(new StringReader(options.replace("&", "\n")));
            config.setDataSourceProperties(p);
            return this;
        }

        public @NotNull SQLiteStorageService.Builder poolSize(int min, int max) {
            config.setMaximumPoolSize(max);
            config.setMinimumIdle(min);
            return this;
        }

        public @NotNull SQLiteStorageService.Builder life(long lifetime, long timeout) {
            config.setMaxLifetime(lifetime);
            config.setConnectionTimeout(timeout);
            return this;
        }

        public @NotNull SQLiteStorageService build() {
            service.createSource(config, new SQLitePlatform(), true, "sqlite");
            return service;
        }
    }
}
