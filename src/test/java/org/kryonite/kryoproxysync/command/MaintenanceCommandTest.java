package org.kryonite.kryoproxysync.command;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.command.SimpleCommand;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;
import org.kryonite.kryoproxysync.messaging.MessagingController;
import org.kryonite.kryoproxysync.persistence.repository.MaintenanceRepository;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaintenanceCommandTest {

  @InjectMocks
  private MaintenanceCommand testee;

  @Mock
  private MaintenanceRepository maintenanceRepositoryMock;

  @Mock
  private MessagingController messagingControllerMock;

  @Test
  void shouldSetMaintenance_WhenArgumentIsEnable() throws SQLException {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"enable"});

    // Act
    testee.execute(invocation);

    // Assert
    verify(maintenanceRepositoryMock).setMaintenance(true);
    verify(messagingControllerMock).sendMaintenanceChanged(true);
    verify(invocation.source()).sendMessage(any());
  }

  @Test
  void shouldDisableMaintenance_WhenArgumentIsDisable() throws SQLException {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"disable"});

    // Act
    testee.execute(invocation);

    // Assert
    verify(maintenanceRepositoryMock).setMaintenance(false);
    verify(messagingControllerMock).sendMaintenanceChanged(false);
    verify(invocation.source()).sendMessage(any());
  }

  @Test
  void shouldSuggestOptions_WhenNoOptionIsGiven() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {});

    // Act
    List<String> suggestions = testee.suggest(invocation);

    // Assert
    Assertions.assertEquals(List.of("enable", "disable"), suggestions);
  }

  @Test
  void shouldSuggestOptions_WhenPartOfOptionIsGiven() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"ena"});

    // Act
    List<String> suggestions = testee.suggest(invocation);

    // Assert
    Assertions.assertEquals(List.of("enable"), suggestions);
  }

  @Test
  void shouldCheckPermission() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.source().hasPermission(MaintenanceManager.MAINTENANCE_PERMISSION)).thenReturn(true);

    // Act
    boolean result = testee.hasPermission(invocation);

    // Assert
    assertTrue(result);
  }
}
