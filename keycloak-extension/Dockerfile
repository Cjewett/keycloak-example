FROM quay.io/keycloak/keycloak:26.1.4 AS builder

COPY ./custom-user-provider/target/custom-user-provider-jar-with-dependencies.jar /opt/keycloak/providers/
COPY ./themes/ /opt/keycloak/themes/