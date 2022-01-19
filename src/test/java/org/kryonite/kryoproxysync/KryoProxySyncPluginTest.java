package org.kryonite.kryoproxysync;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryoproxysync.listener.ProxyPingListener;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KryoProxySyncPluginTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer proxyServerMock;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private MessagingService messagingServiceMock;

  @Test
  void shouldRegisterListener() {
    // Arrange
    KryoProxySyncPlugin testee = new KryoProxySyncPlugin(proxyServerMock, messagingServiceMock);

    // Act
    testee.onInitialize(new ProxyInitializeEvent());

    // Assert
    verify(proxyServerMock.getEventManager()).register(any(), any(ProxyPingListener.class));
  }

  @Test
  void shouldNotRegisterListener_WhenMessagingSetupFailed() throws IOException {
    // Arrange
    KryoProxySyncPlugin testee = new KryoProxySyncPlugin(proxyServerMock, messagingServiceMock);
    doThrow(IOException.class).when(messagingServiceMock).setupExchange(anyString(), any());

    // Act
    testee.onInitialize(new ProxyInitializeEvent());

    // Assert
    verify(proxyServerMock.getEventManager(), never()).register(any(), any(ProxyPingListener.class));
  }
}
