package dev.apolo.api.service;

import dev.apolo.api.model.TpaRequestModel;
import dev.apolo.api.result.ServiceResult;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface ITpaService {
    ServiceResult<Void> sendRequest(Player from, Player target);
    ServiceResult<Void> sendHereRequest(Player from, Player target);
    ServiceResult<Void> accept(Player acceptor);
    ServiceResult<Void> deny(Player denier);
    boolean hasPendingRequest(Player player);
    Optional<TpaRequestModel> getPendingRequest(Player player);
}
