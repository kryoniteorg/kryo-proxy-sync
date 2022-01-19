package org.kryonite.kryoproxysync.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryomessaging.service.message.MessageCallback;
import org.kryonite.kryoproxysync.messaging.message.PlayerCountChanged;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;

@RequiredArgsConstructor
public class PlayerCountChangedConsumer implements MessageCallback<PlayerCountChanged> {

  private final PlayerCountManager playerCountManager;

  @Override
  public void messageReceived(Message<PlayerCountChanged> message) {
    playerCountManager.updatePlayerCount(message.getBody());
  }
}
