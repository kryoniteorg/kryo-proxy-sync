package org.kryonite.kryoproxysync.messaging.consumer;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryoproxysync.messaging.message.MaxPlayerCountChanged;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaxPlayerCountChangedConsumerTest {

  @InjectMocks
  private MaxPlayerCountChangedConsumer testee;

  @Mock
  private PlayerCountManager playerCountManagerMock;

  @Test
  void shouldUpdateMaxPlayerCount() {
    // Arrange
    MaxPlayerCountChanged maxPlayerCountChanged = new MaxPlayerCountChanged(10);
    Message<MaxPlayerCountChanged> message = Message.create("test", maxPlayerCountChanged);

    // Act
    testee.messageReceived(message);

    // Assert
    verify(playerCountManagerMock).updateMaxPlayerCount(maxPlayerCountChanged);
  }
}
