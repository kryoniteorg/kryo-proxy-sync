package org.kryonite.kryoproxysync.messaging.message;

import lombok.Data;

@Data
public class MaintenanceChanged {

  private final boolean enabled;
}
