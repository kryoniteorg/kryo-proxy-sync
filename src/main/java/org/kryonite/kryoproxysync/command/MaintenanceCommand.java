package org.kryonite.kryoproxysync.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.kryonite.kryoproxysync.maintenance.MaintenanceManager;
import org.kryonite.kryoproxysync.messaging.MessagingController;
import org.kryonite.kryoproxysync.persistence.repository.MaintenanceRepository;

@Slf4j
@RequiredArgsConstructor
public class MaintenanceCommand implements SimpleCommand {
  private static final String[] ARGS_OPTIONS = {"enable", "disable"};

  private final MaintenanceRepository maintenanceRepository;
  private final MessagingController messagingController;

  @Override
  public void execute(Invocation invocation) {
    CommandSource source = invocation.source();

    String[] arguments = invocation.arguments();

    try {
      if (arguments[0].equals(ARGS_OPTIONS[0])) {
        setMaintenance(true);
        source.sendMessage(Component.text("Maintenance mode enabled."));
      } else if (arguments[0].equals(ARGS_OPTIONS[1])) {
        setMaintenance(false);
        source.sendMessage(Component.text("Maintenance mode disabled."));
      }
    } catch (SQLException exception) {
      log.error("Failed to update maintenance mode", exception);
      source.sendMessage(Component.text("Failed to update maintenance mode!"));
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    String[] arguments = invocation.arguments();

    if (arguments.length == 0) {
      return List.of(ARGS_OPTIONS);
    }

    if (arguments.length == 1) {
      return Stream.of(ARGS_OPTIONS)
          .filter(argument -> argument.contains(arguments[0]))
          .toList();
    }

    return Collections.emptyList();
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    return invocation.source().hasPermission(MaintenanceManager.MAINTENANCE_PERMISSION);
  }

  private void setMaintenance(boolean enabled) throws SQLException {
    maintenanceRepository.setMaintenance(enabled);
    messagingController.sendMaintenanceChanged(enabled);
  }
}
