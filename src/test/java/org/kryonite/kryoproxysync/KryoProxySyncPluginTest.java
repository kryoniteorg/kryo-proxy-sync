package org.kryonite.kryoproxysync;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.velocitypowered.api.proxy.ProxyServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KryoProxySyncPluginTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer proxyServerMock;

  @Test
  void shouldInitializePlugin() {
    // Arrange - Act
    KryoProxySyncPlugin result = new KryoProxySyncPlugin(proxyServerMock);

    // Assert
    assertNotNull(result, "Instance was null");
  }
}
