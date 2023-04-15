package me.krzysztofprogramming.userprovider.roles;

import me.krzysztofprogramming.userprovider.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RolesManagerTest {
    private final RolesManager rolesManager = new RolesManager();

    @Test
    public void shouldAddRoles() {
        //given
        CustomRoleModel roleA = new CustomRoleModel("A", "", null);
        CustomRoleModel roleB = new CustomRoleModel("B", "", Set.of(roleA));
        CustomRoleModel roleC = new CustomRoleModel("C", "", Collections.emptySet());
        roleA.setAssociatedRoles(Set.of(roleB));

        Map<String, CustomRoleModel> expectedRolesMap = Map.of(
                "A", roleA,
                "B", roleB,
                "C", roleC
        );
        //when
        rolesManager.addRoles(TestUtils.ASSOCIATED_ROLES);

        //then
        Assertions.assertThat(rolesManager).returns(expectedRolesMap, RolesManager::getRolesByIds);
    }
}
