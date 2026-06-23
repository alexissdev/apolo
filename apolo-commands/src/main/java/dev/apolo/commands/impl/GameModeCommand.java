package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.service.IGameModeService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import dev.apolo.commands.base.TabCompleterBase;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class GameModeCommand extends BaseCommand {
    private final IGameModeService gameModeService;
    private static final List<String> MODES = Arrays.asList("survival", "creative", "adventure", "spectator", "0", "1", "2", "3");

    public GameModeCommand(IMessageService messageService, IGameModeService gameModeService) {
        super(messageService);
        this.gameModeService = gameModeService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePermission(context, "apolo.gamemode")) return;

        if (context.getArgs().length == 0) {
            messageService.sendMessage(context.getSender(), MessageKey.GAMEMODE_USAGE);
            return;
        }

        String modeInput = context.getArgs()[0];
        GameMode gameMode = gameModeService.parseGameMode(modeInput);
        if (gameMode == null) {
            messageService.sendMessage(context.getSender(), MessageKey.GAMEMODE_INVALID,
                Collections.singletonMap("input", modeInput));
            return;
        }

        String modePerm = "apolo.gamemode." + gameMode.name().toLowerCase();
        if (!context.getSender().hasPermission(modePerm)) {
            messageService.sendMessage(context.getSender(), MessageKey.GAMEMODE_NO_PERMISSION_MODE,
                Collections.singletonMap("gamemode", gameMode.name().toLowerCase()));
            return;
        }

        if (context.getArgs().length >= 2) {
            if (!requirePermission(context, "apolo.gamemode.others")) return;
            Player target = findOnlinePlayer(context, context.getArgs()[1]);
            if (target == null) return;

            gameModeService.setGameMode(target, gameMode);
            messageService.sendMessage(context.getSender(), MessageKey.GAMEMODE_CHANGED_OTHER_SENDER,
                Map.of("player", target.getName(), "gamemode", gameMode.name().toLowerCase()));
            messageService.sendMessage(target, MessageKey.GAMEMODE_CHANGED_OTHER_TARGET,
                Map.of("player", context.getSender().getName(), "gamemode", gameMode.name().toLowerCase()));
        } else {
            if (!requirePlayer(context)) return;
            Player player = context.getPlayer();
            gameModeService.setGameMode(player, gameMode);
            messageService.sendMessage(player, MessageKey.GAMEMODE_CHANGED_SELF,
                Collections.singletonMap("gamemode", gameMode.name().toLowerCase()));
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1) {
            return TabCompleterBase.filter(MODES, context.getArgs()[0]);
        }
        if (context.getArgs().length == 2 && context.getSender().hasPermission("apolo.gamemode.others")) {
            return null;
        }
        return Collections.emptyList();
    }
}
