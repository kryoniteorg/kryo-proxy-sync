package org.kryonite.kryoproxysync.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryomessaging.service.message.MessageCallback;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;
import org.kryonite.kryoproxysync.messaging.message.MaintenanceChanged;

@RequiredArgsConstructor
public class MaintenanceChangedConsumer implements MessageCallback<MaintenanceChanged> {

  private final MaintenanceManager maintenanceManager;

  @Override
  public void messageReceived(Message<MaintenanceChanged> message) {
    maintenanceManager.updateMaintenance(message.getBody().isEnabled());
  }
}
