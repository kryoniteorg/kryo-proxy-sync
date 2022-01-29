package org.kryonite.kryoproxysync.messaging.consumer;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;
import org.kryonite.kryoproxysync.messaging.message.MaintenanceChanged;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaintenanceChangedConsumerTest {

  @InjectMocks
  private MaintenanceChangedConsumer testee;

  @Mock
  private MaintenanceManager maintenanceManagerMock;

  @Test
  void shouldUpdateMaintenanceMode() {
    // Arrange
    boolean maintenance = true;
    Message<MaintenanceChanged> message = Message.create("test", new MaintenanceChanged(maintenance));

    // Act
    testee.messageReceived(message);

    // Assert
    verify(maintenanceManagerMock).updateMaintenance(maintenance);
  }
}
