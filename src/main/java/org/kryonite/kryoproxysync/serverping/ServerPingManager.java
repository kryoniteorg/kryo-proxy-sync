package org.kryonite.kryoproxysync.serverping;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.kryoproxysync.persistence.entity.ServerPingEntity;
import org.kryonite.kryoproxysync.persistence.repository.ServerPingRepository;

@Slf4j
@RequiredArgsConstructor
public class ServerPingManager {

  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private final ServerPingRepository serverPingRepository;

  @Getter
  private ServerPingEntity serverPing;

  public void setup() {
    executorService.scheduleAtFixedRate(() -> {
      try {
        serverPing = serverPingRepository.get();
      } catch (SQLException exception) {
        log.error("Failed to update serverPing", exception);
      }
    }, 0, 5, TimeUnit.SECONDS);
  }
}
