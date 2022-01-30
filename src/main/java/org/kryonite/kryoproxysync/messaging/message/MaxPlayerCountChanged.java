package org.kryonite.kryoproxysync.messaging.message;

import lombok.Data;

@Data
public class MaxPlayerCountChanged {

  private final int count;
}
