package me.krzysztofprogramming.userprovider.roles;

import lombok.*;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.ReadOnlyException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
class CustomRoleModel implements RoleModel {

    final static String ERROR_MESSAGE = "This property is read only";
    private final RolesCacheStorage rolesCacheStorage;
    @Setter(AccessLevel.PACKAGE)
    private RealmModel realmModel;
    @EqualsAndHashCode.Include
    private String name;
    private String description;
    @Setter(AccessLevel.PACKAGE)
    private Set<RoleModel> associatedRoles;

    @Override
    public String getId() {
        return name;
    }

    @Override
    public void setName(String name) {
        throw new ReadOnlyException(ERROR_MESSAGE);
    }

    @Override
    public void setDescription(String description) {
        throw new ReadOnlyException(ERROR_MESSAGE);
    }

    @Override
    public boolean isComposite() {
        return !associatedRoles.isEmpty();
    }

    @Override
    public void addCompositeRole(RoleModel role) {
        throw new ReadOnlyException(ERROR_MESSAGE);
    }

    @Override
    public void removeCompositeRole(RoleModel role) {
        throw new ReadOnlyException(ERROR_MESSAGE);
    }

    @Override
    public Stream<RoleModel> getCompositesStream(String search, Integer first, Integer max) {
        if (first == null) first = 0;
        if (max == null) max = Integer.MAX_VALUE;

        return associatedRoles.stream().skip(first).limit(max);
    }

    @Override
    public boolean isClientRole() {
        return false;
    }

    @Override
    public String getContainerId() {
        return realmModel.getId();
    }

    @Override
    public RoleContainerModel getContainer() {
        return realmModel;
    }

    @Override
    public boolean hasRole(RoleModel role) {
        return associatedRoles.contains(role);
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        throw new ReadOnlyException(ERROR_MESSAGE);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        throw new ReadOnlyException(ERROR_MESSAGE);
    }

    @Override
    public void removeAttribute(String name) {
        if (!SearchableFields.DESCRIPTION.getName().equals(name))
            throw new ReadOnlyException(ERROR_MESSAGE);
        this.setDescription(null);
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return getAttributes().get(name).stream();
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(SearchableFields.COMPOSITE_ROLE.getName(), isComposite() + "");
        attributes.add(SearchableFields.DESCRIPTION.getName(), description);
        attributes.add(SearchableFields.ID.getName(), getName());
        attributes.add(SearchableFields.NAME.getName(), getName());

        return attributes;
    }

    @Override
    public String toString() {
        return "CustomRoleModel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", associatedRoles=" + associatedRoles.stream().map(RoleModel::getName).toList() +
                '}';
    }
}
