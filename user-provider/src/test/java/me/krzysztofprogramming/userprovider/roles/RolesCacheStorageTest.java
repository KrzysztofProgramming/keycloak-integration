package me.krzysztofprogramming.userprovider.roles;

import me.krzysztofprogramming.userprovider.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RolesCacheStorageTest {

    private final ComponentModel componentModel = mock(ComponentModel.class);
    private final RealmModel realmModel = mock(RealmModel.class);
    private final RolesCacheStorage rolesCacheStorage = new RolesCacheStorage();


    @Test
    public void shouldAddRoles() {
        //given
        when(componentModel.getId()).thenReturn("componentId");

        CustomRoleModel roleA = new CustomRoleModel(rolesCacheStorage, realmModel, "A", "", null);
        CustomRoleModel roleB = new CustomRoleModel(rolesCacheStorage, realmModel, "B", "", Set.of(roleA));
        CustomRoleModel roleC = new CustomRoleModel(rolesCacheStorage, realmModel, "C", "", Collections.emptySet());
        roleA.setAssociatedRoles(Set.of(roleB));


        Map<String, CustomRoleModel> expectedRolesMap = Map.of(
                "A", roleA,
                "B", roleB,
                "C", roleC
        );
        //when
        rolesCacheStorage.addRoles(TestUtils.ASSOCIATED_ROLES);

        //then
        Assertions.assertThat(rolesCacheStorage).returns(expectedRolesMap, RolesCacheStorage::getRolesByNames);
    }
}
