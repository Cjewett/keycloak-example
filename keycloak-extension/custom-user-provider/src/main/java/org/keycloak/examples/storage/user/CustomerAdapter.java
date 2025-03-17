package main.java.org.keycloak.examples.storage.user;

import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CustomerAdapter extends AbstractUserAdapterFederatedStorage {
    private static final Logger logger = Logger.getLogger(CustomerAdapter.class);
    private CustomerEntity entity;
    private String keycloakId;

    public CustomerAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, CustomerEntity entity) {
        super(session, realm, model);
        this.entity = entity;
        this.keycloakId = StorageId.keycloakId(model, entity.getId());
    }

    @Override
    public String getUsername() { return entity.getUsername(); }
    @Override
    public void setUsername(String username) { entity.setUsername(username); }

    @Override
    public String getId() { return keycloakId; }

    public String getPassword() { return entity.getPassword(); }
    public void setPassword(String password) { entity.setPassword(password); }
}
