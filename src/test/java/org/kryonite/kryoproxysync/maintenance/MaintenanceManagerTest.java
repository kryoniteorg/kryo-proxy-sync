package org.kryonite.kryoproxysync.maintenance;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryoproxysync.persistence.repository.MaintenanceRepository;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaintenanceManagerTest {

  @InjectMocks
  private MaintenanceManager testee;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer proxyServerMock;

  @Mock
  private MaintenanceRepository maintenanceRepositoryMock;

  @Test
  void shouldDisconnectPlayers_WhenMaintenanceEnabled() {
    // Arrange
    boolean maintenance = true;
    Player player = mock(Player.class);

    when(proxyServerMock.getAllPlayers()).thenReturn(List.of(player));

    // Act
    testee.updateMaintenance(maintenance);

    // Assert
    verify(player).disconnect(any());
  }

  @Test
  void shouldNotDisconnectPlayers_WhenMaintenanceEnabledButHasPermission() {
    // Arrange
    Player player = mock(Player.class);
    when(player.hasPermission(MaintenanceManager.MAINTENANCE_PERMISSION)).thenReturn(false);
    when(player.hasPermission(MaintenanceManager.MAINTENANCE_BYPASS_PERMISSION)).thenReturn(true);

    when(proxyServerMock.getAllPlayers()).thenReturn(List.of(player));

    // Act
    testee.updateMaintenance(true);

    // Assert
    verify(player, never()).disconnect(any());
  }

  @Test
  void shouldReturnTrue_WhenPlayerHasBypassPermission() {
    // Arrange
    Player player = mock(Player.class);
    when(player.hasPermission(MaintenanceManager.MAINTENANCE_PERMISSION)).thenReturn(false);
    when(player.hasPermission(MaintenanceManager.MAINTENANCE_BYPASS_PERMISSION)).thenReturn(true);

    // Act
    boolean result = testee.canBypassMaintenance(player);

    // Assert
    assertTrue(result);
  }

  @Test
  void shouldReturnTrue_WhenPlayerHasMaintenancePermission() {
    // Arrange
    Player player = mock(Player.class);
    when(player.hasPermission(MaintenanceManager.MAINTENANCE_PERMISSION)).thenReturn(true);

    // Act
    boolean result = testee.canBypassMaintenance(player);

    // Assert
    assertTrue(result);
  }
}
