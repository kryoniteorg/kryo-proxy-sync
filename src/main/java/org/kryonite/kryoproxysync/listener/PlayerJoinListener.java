package org.kryonite.kryoproxysync.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;

@RequiredArgsConstructor
public class PlayerJoinListener {

  private static final TextComponent maintenanceText = Component.text("Server is in maintenance!\nCome back later.");
  private static final TextComponent serverFullText = Component.text("Server is currently full!\nCome back later.");

  private final MaintenanceManager maintenanceManager;
  private final PlayerCountManager playerCountManager;

  @Subscribe(order = PostOrder.EARLY)
  public void onPlayerLogin(LoginEvent event) {
    Player player = event.getPlayer();
    if (!maintenanceManager.isMaintenance()) {
      if (!playerCountManager.canJoinServer(player)) {
        event.setResult(ResultedEvent.ComponentResult.denied(serverFullText));
      }

      return;
    }

    handleOnMaintenance(event, player);
  }

  private void handleOnMaintenance(LoginEvent event, Player player) {
    if (maintenanceManager.canBypassMaintenance(player)) {
      return;
    }

    event.setResult(ResultedEvent.ComponentResult.denied(maintenanceText));
  }
}
