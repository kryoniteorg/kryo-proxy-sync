package org.kryonite.kryoproxysync.persistence.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.kryonite.kryoproxysync.persistence.entity.ServerPingEntity;
import org.kryonite.kryoproxysync.persistence.repository.ConfigRepository;

public class MariaDbConfigRepository implements ConfigRepository {

  protected static final String CREATE_CONFIG_TABLE =
      "CREATE TABLE IF NOT EXISTS config "
          + "(id int primary key, "
          + "description varchar(128) not null, "
          + "sample_players varchar(128), "
          + "max_player_count int not null)";
  protected static final String INSERT_INITIAL_ENTRY =
      "INSERT IGNORE INTO config (id, description, sample_players, max_player_count) "
          + "VALUES(1, 'Test', null, 200)";
  protected static final String GET_CONFIG = "SELECT * FROM config WHERE id = 1";
  protected static final String UPDATE_MAX_PLAYER_COUNT = "UPDATE config SET max_player_count = ? WHERE id = 1";

  private final HikariDataSource dataSource;

  public MariaDbConfigRepository(HikariDataSource dataSource) throws SQLException {
    this.dataSource = dataSource;

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CONFIG_TABLE)) {
      preparedStatement.executeUpdate();
    }

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INITIAL_ENTRY)) {
      preparedStatement.executeUpdate();
    }
  }

  @Override
  public ServerPingEntity getServerPing() throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(GET_CONFIG)) {

      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.first();
      return ServerPingEntity.create(
          resultSet.getInt("id"),
          resultSet.getString("description"),
          getSamplePlayers(resultSet),
          resultSet.getInt("max_player_count")
      );
    }
  }

  @Override
  public int getMaxPlayerCount() throws SQLException {
    return getServerPing().getMaxPlayerCount();
  }

  @Override
  public void setMaxPlayerCount(int count) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MAX_PLAYER_COUNT)) {
      preparedStatement.setInt(1, count);

      preparedStatement.executeUpdate();
    }
  }

  private List<String> getSamplePlayers(ResultSet resultSet) throws SQLException {
    String samplePlayers = resultSet.getString("sample_players");
    if (samplePlayers == null || samplePlayers.isEmpty()) {
      return Collections.emptyList();
    }

    return Arrays.stream(samplePlayers.split("\n")).toList();
  }
}
