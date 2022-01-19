package org.kryonite.kryoproxysync.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProxyPingListenerTest {

  @InjectMocks
  private ProxyPingListener testee;

  @Mock
  private PlayerCountManager playerCountManagerMock;

  @Test
  void shouldReturnUpdatedPlayerCount_WhenProxyPingReceived() {
    // Arrange
    int playerCount = 12;

    ServerPing serverPing = ServerPing.builder()
        .description(Component.empty())
        .build();
    ProxyPingEvent proxyPingEvent = new ProxyPingEvent(mock(InboundConnection.class), serverPing);

    when(playerCountManagerMock.getPlayerCount()).thenReturn(playerCount);

    // Act
    testee.onProxyPing(proxyPingEvent);

    // Assert
    ServerPing result = proxyPingEvent.getPing();
    assertTrue(result.getPlayers().isPresent());
    assertEquals(playerCount, result.getPlayers().get().getOnline());
  }
}
