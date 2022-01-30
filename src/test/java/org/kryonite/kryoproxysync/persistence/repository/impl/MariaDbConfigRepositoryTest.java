package org.kryonite.kryoproxysync.persistence.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kryonite.kryoproxysync.persistence.repository.impl.MariaDbConfigRepository.CREATE_CONFIG_TABLE;
import static org.kryonite.kryoproxysync.persistence.repository.impl.MariaDbConfigRepository.GET_CONFIG;
import static org.kryonite.kryoproxysync.persistence.repository.impl.MariaDbConfigRepository.INSERT_INITIAL_ENTRY;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryoproxysync.persistence.entity.ServerPingEntity;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MariaDbConfigRepositoryTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private HikariDataSource dataSource;

  @Test
  void shouldCreateTableOnStartupAndInsertEntry() throws SQLException {
    // Act
    new MariaDbConfigRepository(dataSource);

    // Assert
    verify(dataSource.getConnection()).prepareStatement(CREATE_CONFIG_TABLE);
    verify(dataSource.getConnection()).prepareStatement(INSERT_INITIAL_ENTRY);
  }

  @Test
  void shouldGetServerPingEntity() throws SQLException {
    // Arrange
    ServerPingEntity serverPingEntity = ServerPingEntity.create(1, "Test", List.of("Test", "12"), 10);

    MariaDbConfigRepository testee = new MariaDbConfigRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(GET_CONFIG).executeQuery().getInt("id"))
        .thenReturn(serverPingEntity.getId());
    when(dataSource.getConnection().prepareStatement(GET_CONFIG).executeQuery().getString("description"))
        .thenReturn(serverPingEntity.getDescription());
    when(dataSource.getConnection().prepareStatement(GET_CONFIG).executeQuery().getString("sample_players"))
        .thenReturn(String.join("\n", serverPingEntity.getSamplePlayers()));
    when(dataSource.getConnection().prepareStatement(GET_CONFIG).executeQuery().getInt("max_player_count"))
        .thenReturn(serverPingEntity.getMaxPlayerCount());

    // Act
    ServerPingEntity result = testee.getServerPing();

    // Assert
    assertEquals(serverPingEntity, result);
  }

  @Test
  void shouldReturnEmptyList_WhenSamplePlayersIsEmpty() throws SQLException {
    // Arrange
    ServerPingEntity serverPingEntity = ServerPingEntity.create(1, "Test", Collections.emptyList(), 10);

    MariaDbConfigRepository testee = new MariaDbConfigRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(GET_CONFIG).executeQuery().getInt("id"))
        .thenReturn(serverPingEntity.getId());
    when(dataSource.getConnection().prepareStatement(GET_CONFIG).executeQuery().getString("description"))
        .thenReturn(serverPingEntity.getDescription());
    when(dataSource.getConnection().prepareStatement(GET_CONFIG).executeQuery().getString("sample_players"))
        .thenReturn(null);
    when(dataSource.getConnection().prepareStatement(GET_CONFIG).executeQuery().getInt("max_player_count"))
        .thenReturn(serverPingEntity.getMaxPlayerCount());

    // Act
    ServerPingEntity result = testee.getServerPing();

    // Assert
    assertEquals(serverPingEntity, result);
  }
}
