package org.kryonite.kryoproxysync.persistence.entity;

import java.util.List;
import lombok.Data;

@Data(staticConstructor = "create")
public class ServerPingEntity {

  private final int id;
  private final String description;
  private final List<String> samplePlayers;
  private final int maxPlayerCount;
}
