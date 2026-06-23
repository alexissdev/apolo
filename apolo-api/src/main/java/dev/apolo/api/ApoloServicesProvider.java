package dev.apolo.api;

import dev.apolo.api.service.IEconomyService;
import dev.apolo.api.service.IFlyService;
import dev.apolo.api.service.IGameModeService;
import dev.apolo.api.service.IGodModeService;
import dev.apolo.api.service.IMessageService;
import dev.apolo.api.service.IRepairService;
import dev.apolo.api.service.ITransactionService;
import dev.apolo.api.service.ITpaService;
import dev.apolo.api.service.IUserService;
import dev.apolo.api.service.IWarpService;

public interface ApoloServicesProvider {
    IGameModeService getGameModeService();
    ITpaService getTpaService();
    IWarpService getWarpService();
    IFlyService getFlyService();
    IGodModeService getGodModeService();
    IRepairService getRepairService();
    IMessageService getMessageService();
    IEconomyService getEconomyService();
    IUserService getUserService();
    ITransactionService getTransactionService();
}
