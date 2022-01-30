package org.kryonite.kryoproxysync.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.kryonite.kryoproxysync.persistence.entity.ServerPingEntity;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;
import org.kryonite.kryoproxysync.serverping.ServerPingManager;

@RequiredArgsConstructor
public class ProxyPingListener {

  private final PlayerCountManager playerCountManager;
  private final ServerPingManager serverPingManager;

  @Subscribe
  public void onProxyPing(ProxyPingEvent event) {
    ServerPingEntity serverPingEntity = serverPingManager.getServerPing();

    ServerPing.Builder serverPingBuilder = event.getPing().asBuilder()
        .onlinePlayers(playerCountManager.getPlayerCount())
        .description(Component.text(serverPingEntity.getDescription()))
        .maximumPlayers(serverPingEntity.getMaxPlayerCount());

    if (!serverPingEntity.getSamplePlayers().isEmpty()) {
      serverPingBuilder.samplePlayers(serverPingEntity.getSamplePlayers().stream()
          .map(samplePlayer -> new ServerPing.SamplePlayer(samplePlayer, UUID.randomUUID()))
          .toList()
          .toArray(ServerPing.SamplePlayer[]::new));
    }

    event.setPing(serverPingBuilder.build());
  }
}
