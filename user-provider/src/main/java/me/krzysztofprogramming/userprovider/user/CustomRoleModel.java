package me.krzysztofprogramming.userprovider.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.ReadOnlyException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class CustomRoleModel implements RoleModel {

    private static final String EXCEPTION_MESSAGE = "Role is read only for this update";

    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String description;

    @Setter
    private Set<RoleModel> associatedRoles;

    @Override
    public void setDescription(String description) {
        throw new ReadOnlyException(EXCEPTION_MESSAGE);
    }

    @Override
    public void setName(String name) {
        throw new ReadOnlyException(EXCEPTION_MESSAGE);
    }

    @Override
    public boolean isComposite() {
        return !associatedRoles.isEmpty();
    }

    @Override
    public void addCompositeRole(RoleModel role) {
        associatedRoles.add(role);
    }

    @Override
    public void removeCompositeRole(RoleModel role) {
        throw new ReadOnlyException(EXCEPTION_MESSAGE);
    }

    @Override
    public Stream<RoleModel> getCompositesStream() {
        return associatedRoles.stream();
    }

    @Override
    public Stream<RoleModel> getCompositesStream(String search, Integer first, Integer max) {
        max = max == null ? Integer.MAX_VALUE : max;
        first = first == null ? 0 : first;
        return associatedRoles.stream().filter(role -> role.getName().startsWith(search)).skip(first).limit(max);
    }

    @Override
    public boolean isClientRole() {
        return true;
    }

    @Override
    public String getContainerId() {
        return null;
    }

    @Override
    public RoleContainerModel getContainer() {
        return null;
    }

    @Override
    public boolean hasRole(RoleModel role) {
        return associatedRoles.contains(role);
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        throw new ReadOnlyException(EXCEPTION_MESSAGE);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        throw new ReadOnlyException(EXCEPTION_MESSAGE);
    }

    @Override
    public void removeAttribute(String name) {
        throw new ReadOnlyException(EXCEPTION_MESSAGE);
    }

    @Override
    public String getFirstAttribute(String name) {
        List<String> attributes = getAttributes().getOrDefault(name, List.of());
        return attributes.isEmpty() ? null : attributes.get(0);
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return getAttributes().keySet().stream();
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributesMap = new MultivaluedHashMap<>();
        attributesMap.add(SearchableFields.COMPOSITE_ROLE.getName(), isComposite() + "");
        attributesMap.add(SearchableFields.DESCRIPTION.getName(), getDescription());
        attributesMap.add(SearchableFields.ID.getName(), getId());
        return attributesMap;
    }
}
