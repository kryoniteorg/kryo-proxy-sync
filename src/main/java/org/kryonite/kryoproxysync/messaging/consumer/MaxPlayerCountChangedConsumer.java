package org.kryonite.kryoproxysync.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryomessaging.service.message.MessageCallback;
import org.kryonite.kryoproxysync.messaging.message.MaxPlayerCountChanged;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;

@RequiredArgsConstructor
public class MaxPlayerCountChangedConsumer implements MessageCallback<MaxPlayerCountChanged> {

  private final PlayerCountManager playerCountManager;

  @Override
  public void messageReceived(Message<MaxPlayerCountChanged> message) {
    playerCountManager.updateMaxPlayerCount(message.getBody());
  }
}
