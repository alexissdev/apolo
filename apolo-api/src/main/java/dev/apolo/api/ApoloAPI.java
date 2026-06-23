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

public final class ApoloAPI {

    private static ApoloServicesProvider provider;

    private ApoloAPI() {}

    public static void setProvider(ApoloServicesProvider provider) {
        ApoloAPI.provider = provider;
    }

    public static boolean isAvailable() {
        return provider != null;
    }

    private static void checkAvailable() {
        if (provider == null) {
            throw new IllegalStateException("ApoloAPI has not been initialized yet.");
        }
    }

    public static IGameModeService getGameModeService() {
        checkAvailable();
        return provider.getGameModeService();
    }

    public static ITpaService getTpaService() {
        checkAvailable();
        return provider.getTpaService();
    }

    public static IWarpService getWarpService() {
        checkAvailable();
        return provider.getWarpService();
    }

    public static IFlyService getFlyService() {
        checkAvailable();
        return provider.getFlyService();
    }

    public static IGodModeService getGodModeService() {
        checkAvailable();
        return provider.getGodModeService();
    }

    public static IRepairService getRepairService() {
        checkAvailable();
        return provider.getRepairService();
    }

    public static IMessageService getMessageService() {
        checkAvailable();
        return provider.getMessageService();
    }

    public static IEconomyService getEconomyService() {
        checkAvailable();
        return provider.getEconomyService();
    }

    public static IUserService getUserService() {
        checkAvailable();
        return provider.getUserService();
    }

    public static ITransactionService getTransactionService() {
        checkAvailable();
        return provider.getTransactionService();
    }
}
