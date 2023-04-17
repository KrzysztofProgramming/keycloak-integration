package me.krzysztofprogramming.userprovider.roles;

import me.krzysztofprogramming.userprovider.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.component.ComponentModel;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RolesManagerTest {

    private final ComponentModel componentModel = mock(ComponentModel.class);
    private final RolesManager rolesManager = new RolesManager(componentModel);


    @Test
    public void shouldAddRoles() {
        //given
        when(componentModel.getId()).thenReturn("componentId");

        CustomRoleModel roleA = new CustomRoleModel(rolesManager, "A", "", null);
        CustomRoleModel roleB = new CustomRoleModel(rolesManager, "B", "", Set.of(roleA));
        CustomRoleModel roleC = new CustomRoleModel(rolesManager, "C", "", Collections.emptySet());
        roleA.setAssociatedRoles(Set.of(roleB));


        Map<String, CustomRoleModel> expectedRolesMap = Map.of(
                "A", roleA,
                "B", roleB,
                "C", roleC
        );
        //when
        rolesManager.addRoles(TestUtils.ASSOCIATED_ROLES);

        //then
        Assertions.assertThat(rolesManager).returns(expectedRolesMap, RolesManager::getRolesByNames);
    }
}
