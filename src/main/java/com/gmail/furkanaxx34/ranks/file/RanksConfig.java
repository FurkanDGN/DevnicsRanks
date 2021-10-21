package com.gmail.furkanaxx34.ranks.file;

import com.gmail.furkanaxx34.ranks.Ranks;
import com.gmail.furkanaxx34.ranks.storage.Sql;
import com.gmail.furkanaxx34.ranks.storage.impl.MySQL;
import com.gmail.furkanaxx34.ranks.storage.impl.SQLite;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.infumialib.paper.transformer.resolvers.BukkitSnakeyaml;
import tr.com.infumia.infumialib.paper.transformer.serializers.SentTitle;
import tr.com.infumia.infumialib.replaceable.RpString;
import tr.com.infumia.infumialib.transformer.TransformedObject;
import tr.com.infumia.infumialib.transformer.TransformerPool;
import tr.com.infumia.infumialib.transformer.annotations.Comment;
import tr.com.infumia.infumialib.transformer.annotations.Exclude;
import tr.com.infumia.infumialib.transformer.annotations.Names;

import java.io.File;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Names(modifier = Names.Modifier.TO_LOWER_CASE, strategy = Names.Strategy.HYPHEN_CASE)
public class RanksConfig extends TransformedObject {

    @Comment("The section contains database settings.")
    public static MySQLSettings mysqlSettings = new MySQLSettings();

    @Comment("How many percent should it increases when player complete all the levels?")
    public static int prestigePercent = 5;

    @Comment("Ranks format should be \"<rank name>: <price>\" Example: \"rank-1: 500\"")
    public static List<String> ranks = List.of(
            "First Rank: 100",
            "Second Rank: 200",
            "Third Rank: 500",
            "Fourth Rank: 1500",
            "Fifth Rank: 5000",
            "rank-6: 10000",
            "rank-7: 15000",
            "rank-8: 25000",
            "rank-9: 50000"
    );

    @Nullable
    @Exclude
    private static TransformedObject instance;

    @Nullable
    @Exclude
    private static Sql sql;

    public static void loadConfig(Ranks plugin) {
        if (RanksConfig.instance == null) {
            RanksConfig.instance = TransformerPool.create(new RanksConfig())
                    .withFile(new File(plugin.getDataFolder(), "config.yml"))
                    .withResolver(new BukkitSnakeyaml())
                    .withTransformPack(registry -> registry.withSerializers(new SentTitle.Serializer()));
        }
        RanksConfig.instance.initiate();
        if (RanksConfig.sql != null) {
            RanksConfig.sql = null;
        }
        RanksConfig.prepareSql(plugin);
    }

    private static void prepareSql(final Ranks plugin) {
        if (RanksConfig.sql == null) {
            RanksConfig.sql = RanksConfig.getPreparedSql(plugin);
        }
    }

    @NotNull
    private static Sql getPreparedSql(final Ranks plugin) {
        if (MySQLSettings.enabled) {
            return new MySQL(
                    MySQLSettings.host.build(),
                    MySQLSettings.port,
                    MySQLSettings.database.build(),
                    MySQLSettings.username.build(),
                    MySQLSettings.password.build(),
                    MySQLSettings.tableName.build()
            );
        } else {
            return new SQLite(
                    new File(plugin.getDataFolder(), "data.db")
            );
        }
    }

    @NotNull
    public static Sql getSql() {
        Preconditions.checkState(RanksConfig.sql != null, "Cannot connected to database!");
        return RanksConfig.sql;
    }

    public static final class MySQLSettings extends TransformedObject {

        @Comment("'Database' or 'Schema' name in your database.")
        public static RpString database = RpString.from("database");

        @Comment({
                "true -> It will use MySQL.",
                "false -> It will use SQLite."
        })
        public static boolean enabled = false;

        @Comment({
                "IP address or domain name of database.",
                "If you using a local database -> 'localhost'"
        })
        public static RpString host = RpString.from("host");

        @Comment("The password of your MySQL server.")
        public static RpString password = RpString.from("password");

        @Comment({
                "Which port is your database streaming on?",
                "Most MySQL databases use 3306 by default."
        })
        public static int port = 3306;

        @Comment("Under which table name do you want to see the player ranks?")
        public static RpString tableName = RpString.from("ranks");

        @Comment("The username of your MySQL server.")
        public static RpString username = RpString.from("admin");
    }
}
