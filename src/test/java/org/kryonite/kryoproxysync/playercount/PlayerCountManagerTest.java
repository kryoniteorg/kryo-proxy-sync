package org.kryonite.kryoproxysync.playercount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.proxy.Player;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryoproxysync.messaging.message.MaxPlayerCountChanged;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;
import org.kryonite.kryoproxysync.persistence.repository.ConfigRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerCountManagerTest {

  private PlayerCountManager testee;

  @Mock
  private ConfigRepository configRepositoryMock;

  @Test
  void shouldReadMaxPlayerCount() throws SQLException {
    // Arrange
    int maxPlayerCount = 0;

    when(configRepositoryMock.getMaxPlayerCount()).thenReturn(maxPlayerCount);

    // Act
    testee = new PlayerCountManager(configRepositoryMock);

    // Assert
    assertFalse(testee.canJoinServer(mock(Player.class)));
  }

  @Test
  void shouldUpdatePlayerCount() {
    // Arrange
    int count = 5;
    PlayerCountChanged playerCountChanged = new PlayerCountChanged(count, "test", System.currentTimeMillis());
    testee = new PlayerCountManager(configRepositoryMock);

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
    testee = new PlayerCountManager(configRepositoryMock);

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
    testee = new PlayerCountManager(configRepositoryMock);

    // Act
    testee.updatePlayerCount(playerCountChanged1);
    testee.updatePlayerCount(playerCountChanged2);

    // Assert
    assertEquals(count2, testee.getPlayerCount());
  }

  @Test
  void shouldJoinServerWithSpecificPermission() {
    // Arrange
    testee = new PlayerCountManager(configRepositoryMock);
    Player player = mock(Player.class);
    when(player.hasPermission(PlayerCountManager.BYPASS_MAX_PLAYER_COUNT_PERMISSIONS)).thenReturn(true);

    // Act
    boolean result = testee.canJoinServer(player);

    // Assert
    assertTrue(result);
  }

  @Test
  void shouldUpdateMaxPlayerCount() throws SQLException {
    // Arrange
    int maxPlayerCount = 0;

    when(configRepositoryMock.getMaxPlayerCount()).thenReturn(maxPlayerCount);
    testee = new PlayerCountManager(configRepositoryMock);

    // Act
    testee.updateMaxPlayerCount(new MaxPlayerCountChanged(10));

    // Assert
    assertTrue(testee.canJoinServer(mock(Player.class)));
  }
}
