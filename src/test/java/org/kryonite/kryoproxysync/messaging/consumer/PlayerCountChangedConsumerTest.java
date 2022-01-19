package org.kryonite.kryoproxysync.messaging.consumer;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerCountChangedConsumerTest {

  @InjectMocks
  private PlayerCountChangedConsumer testee;

  @Mock
  private PlayerCountManager playerCountManagerMock;

  @Test
  void shouldUpdatePlayerCount() {
    // Arrange
    PlayerCountChanged playerCountChanged = new PlayerCountChanged(2, "testee", System.currentTimeMillis());
    Message<PlayerCountChanged> message = Message.create("test", playerCountChanged);

    // Act
    testee.messageReceived(message);

    // Assert
    verify(playerCountManagerMock).updatePlayerCount(playerCountChanged);
  }
}
