package me.krzysztofprogramming.userprovider.user;

import me.krzysztofprogramming.userprovider.client.UserClientService;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.keycloak.utils.StringUtil;

import java.util.List;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    public static final String URL = "url";
    public static final String API_KEY = "api_key";
    private static final List<ProviderConfigProperty> configProperties;

    static {
        configProperties = ProviderConfigurationBuilder.create()
                .property()
                .name(URL)
                .label("API URL")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .property()
                .name(API_KEY)
                .label("API key")
                .type(ProviderConfigProperty.PASSWORD)
                .add()
                .build();
    }

    private UserClientService userClientService;

    @Override
    public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new CustomUserStorageProvider(session, model, new UserClientService(model));
    }

    @Override
    public String getId() {
        return "custom-user-storage-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        StringBuilder message = new StringBuilder();
        if (StringUtil.isBlank(config.get(URL))) message.append("API url is not specified");
        if (StringUtil.isBlank(config.get(API_KEY))) message.append("API key is not specified");

        if (!message.isEmpty()) {
            message.insert(0, "Wrong properties");
            throw new ComponentValidationException(message.toString());
        }
    }

}
