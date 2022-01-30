package org.kryonite.kryoproxysync.persistence.repository;

import java.sql.SQLException;
import org.kryonite.kryoproxysync.persistence.entity.ServerPingEntity;

public interface ConfigRepository {

  ServerPingEntity getServerPing() throws SQLException;

  int getMaxPlayerCount() throws SQLException;

  void setMaxPlayerCount(int count) throws SQLException;
}
