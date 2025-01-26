cp -r /docker-entrypoint-initdb.d/orig-themes/* /opt/bitnami/keycloak/themes
cp -r /docker-entrypoint-initdb.d/themes/* /opt/bitnami/keycloak/themes

cp /docker-entrypoint-initdb.d/keycloak-2fa-email-authenticator.jar /opt/bitnami/keycloak/providers

kc.sh import --file /docker-entrypoint-initdb.d/realm-export.json --override false