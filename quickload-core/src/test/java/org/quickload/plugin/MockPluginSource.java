package org.quickload.plugin;

import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.fasterxml.jackson.databind.JsonNode;

public class MockPluginSource
        implements PluginSource
{
    private final Class<?> expectedIface;
    private final Object plugin;
    private JsonNode typeConfig;

    public <T> MockPluginSource(Class<T> expectedIface, T plugin)
    {
        this.expectedIface = expectedIface;
        this.plugin = plugin;
    }

    public JsonNode getTypeConfig()
    {
        return typeConfig;
    }

    public <T> T newPlugin(Class<T> iface, JsonNode typeConfig) throws PluginSourceNotMatchException
    {
        if (expectedIface.equals(iface)) {
            this.typeConfig = typeConfig;
            return (T) plugin;
        } else {
            throw new PluginSourceNotMatchException();
        }
    }

    public static <T> Module newInjectModule(final Class<T> expectedIface, final T plugin)
    {
        return new InjectModule(expectedIface, plugin);
    }

    public static class InjectModule
            implements Module
    {
        private final PluginSource pluginSource;

        public <T> InjectModule(Class<T> expectedIface, T plugin)
        {
            this.pluginSource = new MockPluginSource(expectedIface, plugin);
        }

        @Override
        public void configure(Binder binder)
        {
            Multibinder<PluginSource> multibinder = Multibinder.newSetBinder(binder, PluginSource.class);
            multibinder.addBinding().toInstance(pluginSource);
        }
    }
}