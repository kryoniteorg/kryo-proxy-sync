package org.kryonite.kryoproxysync.command;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.command.SimpleCommand;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryoproxysync.messaging.MessagingController;
import org.kryonite.kryoproxysync.persistence.repository.ConfigRepository;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaxPlayerCountCommandTest {

  @InjectMocks
  private MaxPlayerCountCommand testee;

  @Mock
  private ConfigRepository configRepositoryMock;

  @Mock
  private MessagingController messagingControllerMock;

  @Test
  void shouldUpdateMaxPlayerCount() throws SQLException {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"10"});

    // Act
    testee.execute(invocation);

    // Assert
    verify(configRepositoryMock).setMaxPlayerCount(10);
    verify(messagingControllerMock).sendMaxPlayerCountChanged(10);
  }

  @Test
  void shouldNotUpdateMaxPlayerCount_WhenWrongInputGiven() throws SQLException {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"ab"});

    // Act
    testee.execute(invocation);

    // Assert
    verify(configRepositoryMock, never()).setMaxPlayerCount(anyInt());
    verify(messagingControllerMock, never()).sendMaxPlayerCountChanged(anyInt());
  }

  @Test
  void shouldCheckPermission() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.source().hasPermission(PlayerCountManager.SET_MAX_PLAYER_COUNT_PERMISSIONS)).thenReturn(true);

    // Act
    boolean result = testee.hasPermission(invocation);

    // Assert
    assertTrue(result);
  }
}
