package me.krzysztofprogramming.userprovider.client;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.krzysztofprogramming.userprovider.user.CustomUserStorageProviderFactory;
import org.keycloak.component.ComponentModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor
@Data
@Slf4j
public abstract class AbstractClientService {

    private final ComponentModel componentModel;

    protected Map<String, String> createPageParamsMap(int pageNumber, int pageSize) {
        Map<String, String> params = new HashMap<>();
        params.put("page", pageNumber + "");
        params.put("pageSize", pageSize + "");
        return params;
    }

    protected String getApiKey() {
        return componentModel.get(CustomUserStorageProviderFactory.API_KEY);
    }

    protected <T> T catchErrors(Supplier<T> action, Function<FeignException, T> onErrorSupplier) {
        try {
            return action.get();
        } catch (FeignException.NotFound | FeignException.Conflict | FeignException.Forbidden
                 | FeignException.Unauthorized exception) {
            if (exception instanceof FeignException.Forbidden
                    || exception instanceof FeignException.Unauthorized) {
                log.warn("Cannot access user-service, make sure you setup api-key correctly");
            }
            return onErrorSupplier.apply(exception);
        }
    }

    protected <T> Optional<T> catchErrors(Supplier<Optional<T>> action) {
        return catchErrors(action, e -> Optional.empty());
    }
}
