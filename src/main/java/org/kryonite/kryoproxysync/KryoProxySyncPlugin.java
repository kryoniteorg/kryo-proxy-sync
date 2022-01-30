package org.kryonite.kryoproxysync;

import com.google.inject.Inject;
import com.rabbitmq.client.Address;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryomessaging.service.DefaultActiveMqConnectionFactory;
import org.kryonite.kryomessaging.service.DefaultMessagingService;
import org.kryonite.kryoproxysync.command.MaintenanceCommand;
import org.kryonite.kryoproxysync.command.MaxPlayerCountCommand;
import org.kryonite.kryoproxysync.listener.PlayerJoinListener;
import org.kryonite.kryoproxysync.listener.ProxyPingListener;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;
import org.kryonite.kryoproxysync.messaging.MessagingController;
import org.kryonite.kryoproxysync.persistence.repository.ConfigRepository;
import org.kryonite.kryoproxysync.persistence.repository.MaintenanceRepository;
import org.kryonite.kryoproxysync.persistence.repository.impl.MariaDbConfigRepository;
import org.kryonite.kryoproxysync.persistence.repository.impl.MariaDbMaintenanceModeRepository;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;
import org.kryonite.kryoproxysync.serverping.ServerPingManager;
import org.mariadb.jdbc.Driver;

@Slf4j
@AllArgsConstructor
@Plugin(id = "kryo-proxy-sync", name = "Kryo Proxy Sync", authors = "Kryonite Labs", version = "0.2.0")
public class KryoProxySyncPlugin {

  private final ProxyServer server;
  private MessagingService messagingService;
  private HikariDataSource hikariDataSource;

  @Inject
  public KryoProxySyncPlugin(ProxyServer server) {
    this.server = server;
  }

  @Subscribe
  public void onInitialize(ProxyInitializeEvent event) {
    ConfigRepository configRepository;
    MaintenanceRepository maintenanceRepository;
    try {
      setupHikariDataSource();
      configRepository = new MariaDbConfigRepository(hikariDataSource);
      maintenanceRepository = new MariaDbMaintenanceModeRepository(hikariDataSource);
    } catch (SQLException exception) {
      log.error("Failed to setup repositories", exception);
      return;
    }

    PlayerCountManager playerCountManager = new PlayerCountManager(configRepository);
    MaintenanceManager maintenanceManager = new MaintenanceManager(server, maintenanceRepository);

    MessagingController messagingController;
    try {
      messagingController = setupMessagingController(playerCountManager, maintenanceManager);
    } catch (IOException | TimeoutException exception) {
      log.error("Failed to setup MessagingService", exception);
      return;
    }

    ServerPingManager serverPingManager = setupServerPingManager(configRepository);

    setupListener(playerCountManager, serverPingManager, maintenanceManager);
    setupCommands(maintenanceRepository, messagingController, configRepository);
  }

  private MessagingController setupMessagingController(PlayerCountManager playerCountManager,
                                                       MaintenanceManager maintenanceManager)
      throws IOException, TimeoutException {
    if (messagingService == null) {
      messagingService = new DefaultMessagingService(new DefaultActiveMqConnectionFactory(
          List.of(Address.parseAddress(getEnv("RABBITMQ_ADDRESS"))),
          getEnv("RABBITMQ_USERNAME"),
          getEnv("RABBITMQ_PASSWORD")
      ));
    }

    MessagingController messagingController = new MessagingController(
        messagingService,
        playerCountManager,
        maintenanceManager,
        server,
        getEnv("SERVER_NAME")
    );
    messagingController.setupPlayerCountChanged();
    messagingController.setupMaxPlayerCountChanged();
    messagingController.setupMaintenanceChanged();

    return messagingController;
  }

  private void setupHikariDataSource() throws SQLException {
    if (hikariDataSource == null) {
      DriverManager.registerDriver(new Driver());
      HikariConfig hikariConfig = new HikariConfig();
      hikariConfig.setJdbcUrl(getEnv("CONNECTION_STRING"));
      hikariDataSource = new HikariDataSource(hikariConfig);
    }
  }

  private String getEnv(String name) {
    String connectionString = System.getenv(name);
    if (connectionString == null) {
      connectionString = System.getProperty(name);
    }

    return connectionString;
  }

  private ServerPingManager setupServerPingManager(ConfigRepository configRepository) {
    ServerPingManager serverPingManager = new ServerPingManager(configRepository);
    serverPingManager.setup();
    return serverPingManager;
  }

  private void setupListener(PlayerCountManager playerCountManager, ServerPingManager serverPingManager,
                             MaintenanceManager maintenanceManager) {
    EventManager eventManager = server.getEventManager();
    eventManager.register(this, new ProxyPingListener(playerCountManager, serverPingManager));
    eventManager.register(this, new PlayerJoinListener(maintenanceManager, playerCountManager));
  }

  private void setupCommands(MaintenanceRepository maintenanceRepository, MessagingController messagingController,
                             ConfigRepository configRepository) {
    CommandMeta maintenance = server.getCommandManager().metaBuilder("maintenance").build();
    server.getCommandManager().register(maintenance,
        new MaintenanceCommand(maintenanceRepository, messagingController));

    CommandMeta maxPlayerCount = server.getCommandManager().metaBuilder("maxplayercount").build();
    server.getCommandManager().register(maxPlayerCount,
        new MaxPlayerCountCommand(configRepository, messagingController));
  }
}
