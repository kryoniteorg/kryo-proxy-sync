package org.kryonite.kryoproxysync.messaging;

import com.rabbitmq.client.BuiltinExchangeType;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;
import org.kryonite.kryoproxysync.messaging.consumer.MaintenanceChangedConsumer;
import org.kryonite.kryoproxysync.messaging.consumer.MaxPlayerCountChangedConsumer;
import org.kryonite.kryoproxysync.messaging.consumer.PlayerCountChangedConsumer;
import org.kryonite.kryoproxysync.messaging.message.MaintenanceChanged;
import org.kryonite.kryoproxysync.messaging.message.MaxPlayerCountChanged;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;

@RequiredArgsConstructor
public class MessagingController {

  protected static final String PLAYER_COUNT_CHANGED_EXCHANGE = "player_count_changed";
  protected static final String MAX_PLAYER_COUNT_CHANGED_EXCHANGE = "max_player_count_changed";
  protected static final String MAINTENANCE_CHANGED_EXCHANGE = "maintenance_changed";

  private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  private final MessagingService messagingService;
  private final PlayerCountManager playerCountManager;
  private final MaintenanceManager maintenanceManager;
  private final ProxyServer proxyServer;
  private final String serverName;

  public void setupPlayerCountChanged() throws IOException {
    messagingService.setupExchange(PLAYER_COUNT_CHANGED_EXCHANGE, BuiltinExchangeType.FANOUT);

    String queue = PLAYER_COUNT_CHANGED_EXCHANGE + "_" + serverName;
    messagingService.bindQueueToExchange(queue, PLAYER_COUNT_CHANGED_EXCHANGE);
    messagingService.startConsuming(queue, new PlayerCountChangedConsumer(playerCountManager),
        PlayerCountChanged.class);

    executorService.scheduleAtFixedRate(() -> {
      PlayerCountChanged playerCountChanged = new PlayerCountChanged(
          proxyServer.getPlayerCount(),
          serverName,
          System.currentTimeMillis()
      );
      messagingService.sendMessage(Message.create(PLAYER_COUNT_CHANGED_EXCHANGE, playerCountChanged));
    }, 5, 5, TimeUnit.SECONDS);
  }

  public void setupMaxPlayerCountChanged() throws IOException {
    messagingService.setupExchange(MAX_PLAYER_COUNT_CHANGED_EXCHANGE, BuiltinExchangeType.FANOUT);

    String queue = MAX_PLAYER_COUNT_CHANGED_EXCHANGE + "_" + serverName;
    messagingService.bindQueueToExchange(queue, MAX_PLAYER_COUNT_CHANGED_EXCHANGE);
    messagingService.startConsuming(queue, new MaxPlayerCountChangedConsumer(playerCountManager),
        MaxPlayerCountChanged.class);
  }

  public void sendMaxPlayerCountChanged(int count) {
    messagingService.sendMessage(Message.create(MAX_PLAYER_COUNT_CHANGED_EXCHANGE, new MaxPlayerCountChanged(count)));
  }

  public void setupMaintenanceChanged() throws IOException {
    messagingService.setupExchange(MAINTENANCE_CHANGED_EXCHANGE, BuiltinExchangeType.FANOUT);

    String queue = MAINTENANCE_CHANGED_EXCHANGE + "_" + serverName;
    messagingService.bindQueueToExchange(queue, MAINTENANCE_CHANGED_EXCHANGE);
    messagingService.startConsuming(queue, new MaintenanceChangedConsumer(maintenanceManager),
        MaintenanceChanged.class);
  }

  public void sendMaintenanceChanged(boolean enabled) {
    messagingService.sendMessage(Message.create(MAINTENANCE_CHANGED_EXCHANGE, new MaintenanceChanged(enabled)));
  }
}
