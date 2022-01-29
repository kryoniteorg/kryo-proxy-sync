package org.kryonite.kryoproxysync.persistence.repository;

import java.sql.SQLException;

public interface MaintenanceRepository {

  boolean isInMaintenance() throws SQLException;

  void setMaintenance(boolean enabled) throws SQLException;
}
