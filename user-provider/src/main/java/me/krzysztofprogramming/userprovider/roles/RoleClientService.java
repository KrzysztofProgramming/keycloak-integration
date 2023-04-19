package me.krzysztofprogramming.userprovider.roles;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import me.krzysztofprogramming.userprovider.client.AbstractClientService;
import me.krzysztofprogramming.userprovider.client.PageResponseDto;
import me.krzysztofprogramming.userprovider.user.CustomUserStorageProviderFactory;
import org.keycloak.component.ComponentModel;

import java.util.Collections;
import java.util.Optional;

class RoleClientService extends AbstractClientService {

    private final RoleClient roleClient;

    public RoleClientService(ComponentModel componentModel) {
        super(componentModel);
        this.roleClient = createRoleClient();
    }

    private RoleClient createRoleClient() {
        return Feign.builder().
                client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.FULL)
                .target(RoleClient.class, getComponentModel().get(CustomUserStorageProviderFactory.URL));
    }

    public Optional<SingleRoleResponseDto> getRoleByName(String name) {
        return catchErrors(
                () -> Optional.of(roleClient.getRole(name, getApiKey()))
        );
    }

    public GetRolesResponseDto getRoles(int pageNumber, int pageSize) {
        return catchErrors(
                () -> roleClient.getRoles(createPageParamsMap(pageNumber, pageSize), getApiKey()),
                e -> new GetRolesResponseDto(new GetRolesResponseListDto(Collections.emptySet()),
                        Collections.emptySet(), new PageResponseDto(0, pageSize, 0, pageNumber))
        );
    }
}
