package dev.apolo.core.repository.interfaces;

import dev.apolo.api.model.WarpModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IWarpRepository {
    void save(WarpModel warp);
    Optional<WarpModel> findByName(String name);
    Set<String> getAllNames();
    List<WarpModel> findAll();
    void delete(String name);
    boolean exists(String name);
}
