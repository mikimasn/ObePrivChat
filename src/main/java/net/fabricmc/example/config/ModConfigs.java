package net.fabricmc.example.config;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.config.*;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static String TOKEN;
    public static int SOME_INT;
    public static double SOME_DOUBLE;
    public static int MAX_DAMAGE_DOWSING_ROD;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(ExampleMod.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("key.security.token", "Token"), "Token to ask api");
    }

    private static void assignConfigs() {
        TOKEN = CONFIG.getOrDefault("key.security.token", "Token");

        ExampleMod.LOGGER.info("All " + configs.getConfigsList().size() + " have been set properly");
    }
}
