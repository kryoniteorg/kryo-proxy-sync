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
import org.kryonite.kryoproxysync.messaging.consumer.PlayerCountChangedConsumer;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;

@RequiredArgsConstructor
public class MessagingController {

  protected static final String PLAYER_COUNT_CHANGED_EXCHANGE = "player_count_changed";
  private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  private final MessagingService messagingService;
  private final PlayerCountManager playerCountManager;
  private final ProxyServer proxyServer;
  private final String serverName;

  public void setupPlayerCountChanged() throws IOException {
    messagingService.setupExchange(PLAYER_COUNT_CHANGED_EXCHANGE, BuiltinExchangeType.FANOUT);

    messagingService.bindQueueToExchange(serverName, PLAYER_COUNT_CHANGED_EXCHANGE);
    messagingService.startConsuming(serverName, new PlayerCountChangedConsumer(playerCountManager),
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
}
