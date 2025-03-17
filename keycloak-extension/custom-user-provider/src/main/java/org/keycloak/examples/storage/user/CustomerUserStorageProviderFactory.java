package main.java.org.keycloak.examples.storage.user;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class CustomerUserStorageProviderFactory implements UserStorageProviderFactory<CustomerUserStorageProvider> {
    public static final String PROVIDER_ID = "custom-user-provider";

    private static final Logger logger = Logger.getLogger(CustomerUserStorageProviderFactory.class);

    @Override
    public CustomerUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        logger.info("Creating instance of CustomerUserStorageProvider");
        return new CustomerUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Custom User Storage Provider that authenticates against an external MSSQL database";
    }

    @Override
    public void close() {
        logger.info("Closing CustomerUserStorageProviderFactory");
    }
}
