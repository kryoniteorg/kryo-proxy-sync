package org.kryonite.kryoproxysync.playercount;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;

public class PlayerCountManager {

  private final Map<String, PlayerCountChanged> servers = new HashMap<>();

  @Getter
  private int playerCount;

  public void updatePlayerCount(PlayerCountChanged playerCountChanged) {
    servers.put(playerCountChanged.getServerName(), playerCountChanged);

    long now = System.currentTimeMillis();
    servers.entrySet().removeIf(entry -> entry.getValue().getUpdatedAt() + 7_500 < now);

    playerCount = servers.values().stream()
        .mapToInt(PlayerCountChanged::getCount)
        .sum();
  }
}
