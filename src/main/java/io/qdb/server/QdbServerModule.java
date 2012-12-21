package io.qdb.server;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import io.qdb.server.controller.Router;
import io.qdb.server.model.Repository;
import io.qdb.server.zk.ZkRepository;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;

import java.util.Map;

/**
 * Standard server configuration.
 */
public class QdbServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bindProperties();
        bind(Container.class).to(Router.class);
        bind(Repository.class).to(ZkRepository.class);
        bind(EventBus.class).toInstance(new EventBus());
        bind(Connection.class).toProvider(ConnectionProvider.class);
    }

    /**
     * Create named bindings for all our configuration properties.
     */
    protected void bindProperties() {
        Config cfg = ConfigFactory.load();
        for (Map.Entry<String, ConfigValue> entry : cfg.entrySet()) {
            ConfigValue value = entry.getValue();
            if (value.origin().url() != null) {
                Named named = Names.named(entry.getKey());
                Object v = value.unwrapped();
                if (v instanceof String) bind(Key.get(String.class, named)).toInstance((String)v);
                else if (v instanceof Integer) bind(Key.get(Integer.class, named)).toInstance((Integer)v);
                else bind(Key.get(Object.class, named)).toInstance(v);
            }
        }
    }
}