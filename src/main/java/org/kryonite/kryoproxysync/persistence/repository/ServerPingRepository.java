package org.kryonite.kryoproxysync.persistence.repository;

import java.sql.SQLException;
import org.kryonite.kryoproxysync.persistence.entity.ServerPingEntity;

public interface ServerPingRepository {

  ServerPingEntity get() throws SQLException;
}
