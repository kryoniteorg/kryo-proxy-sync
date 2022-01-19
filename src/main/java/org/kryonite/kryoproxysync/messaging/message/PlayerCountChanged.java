package org.kryonite.kryoproxysync.messaging.message;

import lombok.Data;

@Data
public class PlayerCountChanged {

  private final int count;
  private final String serverName;
  private final long updatedAt;
}
