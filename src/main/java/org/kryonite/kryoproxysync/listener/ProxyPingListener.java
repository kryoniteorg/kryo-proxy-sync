package org.kryonite.kryoproxysync.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import lombok.RequiredArgsConstructor;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;

@RequiredArgsConstructor
public class ProxyPingListener {

  private final PlayerCountManager playerCountManager;

  @Subscribe
  public void onProxyPing(ProxyPingEvent event) {
    ServerPing serverPing = event.getPing().asBuilder()
        .onlinePlayers(playerCountManager.getPlayerCount())
        .build();
    event.setPing(serverPing);
  }
}
