package org.kryonite.kryoproxysync.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.kryonite.kryoproxysync.messaging.MessagingController;
import org.kryonite.kryoproxysync.persistence.repository.ConfigRepository;
import org.kryonite.kryoproxysync.playercount.PlayerCountManager;

@Slf4j
@RequiredArgsConstructor
public class MaxPlayerCountCommand implements SimpleCommand {

  private final ConfigRepository configRepository;
  private final MessagingController messagingController;

  @Override
  public void execute(Invocation invocation) {
    CommandSource source = invocation.source();

    String[] arguments = invocation.arguments();
    if (arguments.length == 0) {
      source.sendMessage(Component.text("Usage: /maxplayercount <number>"));
    } else {
      String count = arguments[0];
      try {
        setMaxPlayerCount(source, count);
      } catch (SQLException exception) {
        log.error("Failed to save player count!", exception);
      } catch (NumberFormatException exception) {
        source.sendMessage(Component.text(count + " is not a valid number!"));
      }
    }
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    return invocation.source().hasPermission(PlayerCountManager.SET_MAX_PLAYER_COUNT_PERMISSIONS);
  }

  private void setMaxPlayerCount(CommandSource source, String count) throws SQLException {
    int parsedCount = Integer.parseInt(count);
    configRepository.setMaxPlayerCount(parsedCount);
    messagingController.sendMaxPlayerCountChanged(parsedCount);
    source.sendMessage(Component.text("Updated max player count to " + count));
  }
}
