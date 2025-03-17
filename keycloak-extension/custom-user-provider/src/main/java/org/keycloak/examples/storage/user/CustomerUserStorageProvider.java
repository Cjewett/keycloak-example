package main.java.org.keycloak.examples.storage.user;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import main.java.org.keycloak.examples.storage.user.CustomerAdapter;

import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {
    private static final Logger logger = Logger.getLogger(CustomerUserStorageProvider.class);

    private KeycloakSession session;
    private ComponentModel model;

    private static final String DB_URL = "jdbc:sqlserver://mssql:1433;databaseName=master;user=sa;password=LongerPassword1*;trustServerCertificate=true";

    public CustomerUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        logger.info("getUserByUsername: " + username);
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement("SELECT id, username, password FROM [customers] WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                CustomerEntity userEntity = new CustomerEntity();
                userEntity.setId(rs.getString("id"));
                userEntity.setUsername(rs.getString("username"));
                userEntity.setPassword(rs.getString("password"));

                return new CustomerAdapter(session, realm, model, userEntity);
            }
        } catch (Exception e) {
            logger.error("Error fetching user from database", e);
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        logger.info("getUserByEmail: " + email);
        
        // If you don't store emails, return null
        return null;
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        logger.info("getUserById: " + id);

        // Extract the username from the Keycloak ID
        StorageId storageId = new StorageId(id);
        String externalId = storageId.getExternalId();
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement("SELECT id, username, password FROM [customers] WHERE id = ?")) {
            stmt.setString(1, externalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                CustomerEntity userEntity = new CustomerEntity();
                userEntity.setId(rs.getString("id"));
                userEntity.setUsername(rs.getString("username"));
                userEntity.setPassword(rs.getString("password"));

                return new CustomerAdapter(session, realm, model, userEntity);
            }
        } catch (Exception e) {
            logger.error("Error fetching user from database", e);
        }
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    public boolean validate(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(input instanceof UserCredentialModel)) return false;
        if (!supportsCredentialType(input.getType())) return false;

        String username = user.getUsername();
        String enteredPassword = input.getChallengeResponse();

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM [customers] WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(enteredPassword);
            }
        } catch (Exception e) {
            logger.error("Error validating user password", e);
        }
        return false;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        return validate(realm, user, input);
    }

    @Override
    public void close() {
        logger.info("Closing CustomerUserStorageProvider");
    }
}
