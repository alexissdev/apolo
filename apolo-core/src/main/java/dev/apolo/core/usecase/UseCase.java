package dev.apolo.core.usecase;

import dev.apolo.api.result.ServiceResult;

public interface UseCase<INPUT, OUTPUT> {
    ServiceResult<OUTPUT> execute(INPUT input);
}
