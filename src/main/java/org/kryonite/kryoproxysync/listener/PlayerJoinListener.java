package org.kryonite.kryoproxysync.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;

@RequiredArgsConstructor
public class PlayerJoinListener {

  private static final TextComponent maintenanceText = Component.text("Server is in maintenance!\nCome back later.");

  private final MaintenanceManager maintenanceManager;

  @Subscribe
  public void onPlayerLogin(LoginEvent event) {
    if (!maintenanceManager.isMaintenance()) {
      return;
    }

    Player player = event.getPlayer();
    if (maintenanceManager.canBypassMaintenance(player)) {
      return;
    }

    event.setResult(ResultedEvent.ComponentResult.denied(maintenanceText));
  }
}
