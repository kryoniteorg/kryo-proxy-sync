package org.kryonite.kryoproxysync.serverping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryoproxysync.persistence.entity.ServerPingEntity;
import org.kryonite.kryoproxysync.persistence.repository.ConfigRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServerPingManagerTest {

  @InjectMocks
  private ServerPingManager testee;

  @Mock
  private ConfigRepository configRepositoryMock;

  @Test
  void shouldUpdateServerPing_WhenSetupIsCalled() throws SQLException {
    // Arrange
    ServerPingEntity serverPingEntity = ServerPingEntity.create(1, "Test", List.of("Test", "12"), 10);
    when(configRepositoryMock.getServerPing()).thenReturn(serverPingEntity);

    // Act
    testee.setup();

    // Assert
    Awaitility.await()
        .atMost(1, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(configRepositoryMock).getServerPing());
    assertEquals(serverPingEntity, testee.getServerPing());
  }

  @Test
  void shouldUpdateServerPing_Every5Seconds() throws SQLException {
    // Arrange
    ServerPingEntity serverPingEntity = ServerPingEntity.create(1, "Test", List.of("Test", "12"), 10);
    when(configRepositoryMock.getServerPing()).thenReturn(serverPingEntity);

    // Act
    testee.setup();

    // Assert
    Awaitility.await()
        .atMost(6, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(configRepositoryMock, times(2)).getServerPing());
    assertEquals(serverPingEntity, testee.getServerPing());
  }
}
