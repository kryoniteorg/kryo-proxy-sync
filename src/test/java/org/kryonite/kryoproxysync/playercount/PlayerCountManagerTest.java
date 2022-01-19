package org.kryonite.kryoproxysync.playercount;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;


class PlayerCountManagerTest {

  private PlayerCountManager testee;

  @BeforeEach
  void setup() {
    testee = new PlayerCountManager();
  }

  @Test
  void shouldUpdatePlayerCount() {
    // Arrange
    int count = 5;
    PlayerCountChanged playerCountChanged = new PlayerCountChanged(count, "test", System.currentTimeMillis());

    // Act
    testee.updatePlayerCount(playerCountChanged);

    // Assert
    assertEquals(count, testee.getPlayerCount());
  }

  @Test
  void shouldSumPlayerCountFromDifferentServers() {
    // Arrange
    int count1 = 5;
    int count2 = 5;
    PlayerCountChanged playerCountChanged1 = new PlayerCountChanged(count1, "test1", System.currentTimeMillis());
    PlayerCountChanged playerCountChanged2 = new PlayerCountChanged(count2, "test2", System.currentTimeMillis());

    // Act
    testee.updatePlayerCount(playerCountChanged1);
    testee.updatePlayerCount(playerCountChanged2);

    // Assert
    assertEquals(count1 + count2, testee.getPlayerCount());
  }

  @Test
  void shouldRemoveOldServerEntries() {
    // Arrange
    int count1 = 5;
    int count2 = 6;
    PlayerCountChanged playerCountChanged1 = new PlayerCountChanged(count1, "test1", System.currentTimeMillis() - 8000);
    PlayerCountChanged playerCountChanged2 = new PlayerCountChanged(count2, "test2", System.currentTimeMillis());

    // Act
    testee.updatePlayerCount(playerCountChanged1);
    testee.updatePlayerCount(playerCountChanged2);

    // Assert
    assertEquals(count2, testee.getPlayerCount());
  }
}
