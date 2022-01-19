package org.kryonite.kryoproxysync.messaging;

import static org.kryonite.kryoproxysync.messaging.MessagingController.PLAYER_COUNT_CHANGED_EXCHANGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.rabbitmq.client.BuiltinExchangeType;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryoproxysync.messaging.consumer.PlayerCountChangedConsumer;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessagingControllerTest {

  private final String serverName = "Testee";

  private MessagingController testee;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer proxyServerMock;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private MessagingService messagingServiceMock;

  @Mock
  private PlayerCountManager playerCountManagerMock;

  @BeforeEach
  void setup() {
    testee = new MessagingController(messagingServiceMock, playerCountManagerMock, proxyServerMock, serverName);
  }

  @Test
  void shouldSetupMessaging() throws IOException {
    // Arrange - Act
    testee.setupPlayerCountChanged();

    // Assert
    verify(messagingServiceMock).setupExchange(PLAYER_COUNT_CHANGED_EXCHANGE, BuiltinExchangeType.FANOUT);
    verify(messagingServiceMock).bindQueueToExchange(serverName, PLAYER_COUNT_CHANGED_EXCHANGE);
    verify(messagingServiceMock).startConsuming(eq(serverName), any(PlayerCountChangedConsumer.class),
        eq(PlayerCountChanged.class));
  }

  @Test
  void shouldSendUpdatePlayerCountMessage_WhenFiveSecondsHavePassed() throws IOException {
    // Arrange - Act
    testee.setupPlayerCountChanged();

    // Assert
    Awaitility.await()
        .atMost(6, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(messagingServiceMock).sendMessage(any()));
  }
}
