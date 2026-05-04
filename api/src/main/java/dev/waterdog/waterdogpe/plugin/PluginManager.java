package dev.waterdog.waterdogpe.plugin;

import dev.waterdog.waterdogpe.ProxyServer;

import java.util.Collection;
import java.util.Map;

public interface PluginManager {
    ProxyServer getProxy();

    void loadAllPlugins();

    void enableAllPlugins();

    void disableAllPlugins();

    Plugin getPluginByName(String name);

    Collection<Plugin> getPlugins();

    Map<String, Plugin> getPluginMap();

    boolean enablePlugin(Plugin plugin, String parent);

    Class<?> getClassFromCache(String className);

    void cacheClass(String className, Class<?> clazz);
}
