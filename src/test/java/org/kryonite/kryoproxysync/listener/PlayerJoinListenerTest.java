package org.kryonite.kryoproxysync.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerJoinListenerTest {

  @InjectMocks
  private PlayerJoinListener testee;

  @Mock
  private MaintenanceManager maintenanceManagerMock;

  @Test
  void shouldAllowLogin_WhenMaintenanceDisabled() {
    // Arrange
    LoginEvent loginEvent = new LoginEvent(mock(Player.class));

    // Act
    testee.onPlayerLogin(loginEvent);

    // Assert
    assertEquals(ResultedEvent.ComponentResult.allowed(), loginEvent.getResult());
  }

  @Test
  void shouldAllowLogin_WhenMaintenanceEnabledWithBypassPermission() {
    // Arrange
    when(maintenanceManagerMock.isMaintenance()).thenReturn(true);

    Player player = mock(Player.class);
    when(maintenanceManagerMock.canBypassMaintenance(player)).thenReturn(true);
    LoginEvent loginEvent = new LoginEvent(player);

    // Act
    testee.onPlayerLogin(loginEvent);

    // Assert
    assertEquals(ResultedEvent.ComponentResult.allowed(), loginEvent.getResult());
  }

  @Test
  void shouldDenyLogin_WhenMaintenanceEnabledWithoutBypassPermission() {
    // Arrange
    when(maintenanceManagerMock.isMaintenance()).thenReturn(true);

    LoginEvent loginEvent = new LoginEvent(mock(Player.class));

    // Act
    testee.onPlayerLogin(loginEvent);

    // Assert
    assertFalse(loginEvent.getResult().isAllowed());
  }
}
