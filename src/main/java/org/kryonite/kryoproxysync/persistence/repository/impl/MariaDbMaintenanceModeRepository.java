package org.kryonite.kryoproxysync.persistence.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.kryoproxysync.persistence.repository.MaintenanceRepository;

@Slf4j
public class MariaDbMaintenanceModeRepository implements MaintenanceRepository {

  protected static final String CREATE_MAINTENANCE_TABLE =
      "CREATE TABLE IF NOT EXISTS maintenance "
          + "(id int primary key auto_increment, "
          + "enabled boolean not null)";
  protected static final String INSERT_INITIAL_ENTRY =
      "INSERT IGNORE INTO maintenance (id, enabled) "
          + "VALUES(1, true)";
  protected static final String GET_MAINTENANCE = "SELECT * FROM maintenance WHERE id = 1";
  protected static final String UPDATE_MAINTENANCE = "UPDATE maintenance SET enabled = ? WHERE id = 1";

  private final HikariDataSource dataSource;

  public MariaDbMaintenanceModeRepository(HikariDataSource dataSource) throws SQLException {
    this.dataSource = dataSource;

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(CREATE_MAINTENANCE_TABLE)) {
      preparedStatement.executeUpdate();
    }

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INITIAL_ENTRY)) {
      preparedStatement.executeUpdate();
    }
  }

  @Override
  public boolean isInMaintenance() throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(GET_MAINTENANCE)) {

      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.first();
      return resultSet.getBoolean("enabled");
    }
  }

  @Override
  public void setMaintenance(boolean enabled) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MAINTENANCE)) {
      preparedStatement.setBoolean(1, enabled);

      preparedStatement.executeUpdate();
    }
  }
}
