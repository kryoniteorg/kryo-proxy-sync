package org.kryonite.kryoproxysync.persistence.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MariaDbMaintenanceModeRepositoryTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private HikariDataSource dataSource;

  @Test
  void shouldCreateTableOnStartupAndInsertEntry() throws SQLException {
    // Arrange - Act
    new MariaDbMaintenanceModeRepository(dataSource);

    // Assert
    verify(dataSource.getConnection()).prepareStatement(MariaDbMaintenanceModeRepository.CREATE_MAINTENANCE_TABLE);
    verify(dataSource.getConnection()).prepareStatement(MariaDbMaintenanceModeRepository.INSERT_INITIAL_ENTRY);
  }

  @Test
  void shouldGetMaintenanceMode() throws SQLException {
    // Arrange
    boolean maintenance = true;
    MariaDbMaintenanceModeRepository testee = new MariaDbMaintenanceModeRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(MariaDbMaintenanceModeRepository.GET_MAINTENANCE)
        .executeQuery().getBoolean("enabled")).thenReturn(maintenance);

    // Act
    boolean result = testee.isInMaintenance();

    // Assert
    assertEquals(maintenance, result);
  }

  @Test
  void shouldSetMaintenanceMode() throws SQLException {
    // Arrange
    boolean maintenance = true;
    MariaDbMaintenanceModeRepository testee = new MariaDbMaintenanceModeRepository(dataSource);

    // Act
    testee.setMaintenance(maintenance);

    // Assert
    verify(dataSource.getConnection().prepareStatement(MariaDbMaintenanceModeRepository.UPDATE_MAINTENANCE))
        .executeUpdate();
    verify(dataSource.getConnection().prepareStatement(MariaDbMaintenanceModeRepository.UPDATE_MAINTENANCE))
        .setBoolean(1, maintenance);
  }
}
