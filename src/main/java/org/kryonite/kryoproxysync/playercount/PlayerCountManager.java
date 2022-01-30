package org.kryonite.kryoproxysync.playercount;

import com.velocitypowered.api.proxy.Player;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.kryoproxysync.messaging.message.MaxPlayerCountChanged;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;
import org.kryonite.kryoproxysync.persistence.repository.ConfigRepository;

@Slf4j
public class PlayerCountManager {

  public static final String SET_MAX_PLAYER_COUNT_PERMISSIONS = "player.count.max";
  protected static final String BYPASS_MAX_PLAYER_COUNT_PERMISSIONS = "player.count.max.bypass";

  private final Map<String, PlayerCountChanged> servers = new HashMap<>();

  @Getter
  private int playerCount;
  private int maxPlayerCount = 0;

  public PlayerCountManager(ConfigRepository configRepository) {
    try {
      maxPlayerCount = configRepository.getMaxPlayerCount();
    } catch (SQLException exception) {
      log.error("Failed to read max players!", exception);
    }
  }

  public boolean canJoinServer(Player player) {
    return player.hasPermission(BYPASS_MAX_PLAYER_COUNT_PERMISSIONS) || playerCount < maxPlayerCount;
  }

  public void updatePlayerCount(PlayerCountChanged playerCountChanged) {
    servers.put(playerCountChanged.getServerName(), playerCountChanged);

    long now = System.currentTimeMillis();
    servers.entrySet().removeIf(entry -> entry.getValue().getUpdatedAt() + 7_500 < now);

    playerCount = servers.values().stream()
        .mapToInt(PlayerCountChanged::getCount)
        .sum();
  }

  public void updateMaxPlayerCount(MaxPlayerCountChanged maxPlayerCountChanged) {
    maxPlayerCount = maxPlayerCountChanged.getCount();
  }
}
