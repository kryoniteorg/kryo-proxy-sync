package org.kryonite.kryoproxysync;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryoproxysync.listener.ProxyPingListener;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KryoProxySyncPluginTest {

  @InjectMocks
  private KryoProxySyncPlugin testee;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer proxyServerMock;

  @Mock
  private MessagingService messagingServiceMock;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private HikariDataSource hikariDataSourceMock;

  @Test
  void shouldRegisterListener() {
    // Arrange
    ProxyInitializeEvent proxyInitializeEvent = new ProxyInitializeEvent();

    // Act
    testee.onInitialize(proxyInitializeEvent);

    // Assert
    verify(proxyServerMock.getEventManager()).register(any(), any(ProxyPingListener.class));
  }

  @Test
  void shouldNotRegisterListener_WhenMessagingSetupFailed() throws IOException {
    // Arrange
    doThrow(IOException.class).when(messagingServiceMock).setupExchange(anyString(), any());

    // Act
    testee.onInitialize(new ProxyInitializeEvent());

    // Assert
    verify(proxyServerMock.getEventManager(), never()).register(any(), any(ProxyPingListener.class));
  }

  @Test
  void shouldNotRegisterListener_WhenServerPingSetupFailed() throws SQLException {
    // Arrange
    doThrow(SQLException.class).when(hikariDataSourceMock).getConnection();

    // Act
    testee.onInitialize(new ProxyInitializeEvent());

    // Assert
    verify(proxyServerMock.getEventManager(), never()).register(any(), any(ProxyPingListener.class));
  }
}
