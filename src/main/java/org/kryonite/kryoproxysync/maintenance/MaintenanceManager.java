package org.kryonite.kryoproxysync.maintenance;


import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.sql.SQLException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.kryonite.kryoproxysync.persistence.repository.MaintenanceRepository;

@Slf4j
public class MaintenanceManager {

  public static final String MAINTENANCE_PERMISSION = "maintenance";
  public static final String MAINTENANCE_BYPASS_PERMISSION = "maintenance.bypass";

  private final ProxyServer server;

  @Getter
  private boolean maintenance = true;

  public MaintenanceManager(ProxyServer server, MaintenanceRepository maintenanceRepository) {
    this.server = server;

    try {
      maintenance = maintenanceRepository.isInMaintenance();
    } catch (SQLException exception) {
      log.error("Failed to check if the server is in maintenance mode. Setting maintenance to enabled!", exception);
    }
  }

  public void updateMaintenance(boolean enabled) {
    maintenance = enabled;
    if (maintenance) {
      server.getAllPlayers().stream()
          .filter(player -> !canBypassMaintenance(player))
          .forEach(player -> player.disconnect(Component.text("Server is in maintenance mode!")));
    }
  }

  public boolean canBypassMaintenance(Player player) {
    return player.hasPermission(MAINTENANCE_PERMISSION) || player.hasPermission(MAINTENANCE_BYPASS_PERMISSION);
  }
}
