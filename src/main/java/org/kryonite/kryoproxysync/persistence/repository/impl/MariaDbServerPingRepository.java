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
import org.kryonite.kryoproxysync.persistence.repository.ServerPingRepository;

public class MariaDbServerPingRepository implements ServerPingRepository {

  protected static final String CREATE_SERVER_PING_TABLE =
      "CREATE TABLE IF NOT EXISTS server_ping "
          + "(id int primary key auto_increment, "
          + "description varchar(128) not null, "
          + "sample_players varchar(128))";
  protected static final String INSERT_INITIAL_ENTRY =
      "INSERT IGNORE INTO server_ping (id, description, sample_players) "
          + "VALUES(1, 'Test', null)";
  protected static final String GET_SERVER_PING = "SELECT * FROM server_ping WHERE id = 1";

  private final HikariDataSource dataSource;

  public MariaDbServerPingRepository(HikariDataSource dataSource) throws SQLException {
    this.dataSource = dataSource;

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SERVER_PING_TABLE)) {
      preparedStatement.executeUpdate();
    }

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INITIAL_ENTRY)) {
      preparedStatement.executeUpdate();
    }
  }

  @Override
  public ServerPingEntity get() throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(GET_SERVER_PING)) {

      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.first();
      return ServerPingEntity.create(
          resultSet.getInt("id"),
          resultSet.getString("description"),
          getSamplePlayers(resultSet)
      );
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
