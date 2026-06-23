package dev.apolo.commands.impl;

import dev.apolo.api.messaging.MessageKey;
import dev.apolo.api.model.RepairResultModel;
import dev.apolo.api.result.ServiceResult;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IRepairService;
import dev.apolo.commands.base.BaseCommand;
import dev.apolo.commands.base.CommandContext;
import dev.apolo.commands.base.TabCompleterBase;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RepairCommand extends BaseCommand {
    private final IRepairService repairService;
    private static final List<String> MODES = Arrays.asList("hand", "armor", "all");

    public RepairCommand(IMessageService messageService, IRepairService repairService) {
        super(messageService);
        this.repairService = repairService;
    }

    @Override
    protected void execute(CommandContext context) {
        if (!requirePlayer(context)) return;
        if (!requirePermission(context, "apolo.repair")) return;

        Player player = context.getPlayer();

        if (repairService.isOnCooldown(player)) {
            long remaining = repairService.getCooldownRemaining(player);
            messageService.sendMessage(player, MessageKey.REPAIR_COOLDOWN,
                Collections.singletonMap("seconds", String.valueOf(remaining)));
            return;
        }

        String mode = context.getArgs().length == 0 ? "hand" : context.getArgs()[0].toLowerCase();
        ServiceResult<RepairResultModel> result;

        switch (mode) {
            case "armor":
                if (!requirePermission(context, "apolo.repair.armor")) return;
                result = repairService.repairArmor(player);
                if (result.isSuccess()) {
                    messageService.sendMessage(player, MessageKey.REPAIR_SUCCESS_ARMOR);
                } else {
                    result.getMessageKey().ifPresent(key ->
                        messageService.sendMessage(player, key, result.getPlaceholders()));
                }
                break;
            case "all":
                if (!requirePermission(context, "apolo.repair.all")) return;
                result = repairService.repairAll(player);
                if (result.isSuccess()) {
                    messageService.sendMessage(player, MessageKey.REPAIR_SUCCESS_ALL);
                } else {
                    result.getMessageKey().ifPresent(key ->
                        messageService.sendMessage(player, key, result.getPlaceholders()));
                }
                break;
            default:
                result = repairService.repairHand(player);
                if (result.isSuccess()) {
                    messageService.sendMessage(player, MessageKey.REPAIR_SUCCESS_HAND);
                } else {
                    result.getMessageKey().ifPresent(key ->
                        messageService.sendMessage(player, key, result.getPlaceholders()));
                }
                break;
        }
    }

    @Override
    protected List<String> tabComplete(CommandContext context) {
        if (context.getArgs().length == 1) {
            return TabCompleterBase.filter(MODES, context.getArgs()[0]);
        }
        return Collections.emptyList();
    }
}
