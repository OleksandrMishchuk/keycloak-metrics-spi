package org.jboss.aerogear.keycloak.metrics;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

public class MetricsEventListener implements EventListenerProvider {

    public final static String ID = "metrics-listener";

    private final static Logger logger = Logger.getLogger(MetricsEventListener.class);
    private final KeycloakSession keycloakSession;

    public MetricsEventListener(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }


    @Override
    public void onEvent(Event event) {
        logEventDetails(event);

        switch (event.getType()) {
            case LOGIN:
                PrometheusExporter.instance().recordLogin(event, keycloakSession.realms());
                PrometheusExporter.instance().recordSessions(event, keycloakSession);
                break;
            case CLIENT_LOGIN:
                PrometheusExporter.instance().recordClientLogin(event, keycloakSession.realms());
                PrometheusExporter.instance().recordSessions(event, keycloakSession);
                break;
            case LOGOUT:
                PrometheusExporter.instance().recordSessions(event, keycloakSession);
                break;
            case REGISTER:
                PrometheusExporter.instance().recordRegistration(event, keycloakSession.realms());
                break;
            case REFRESH_TOKEN:
                PrometheusExporter.instance().recordRefreshToken(event, keycloakSession.realms());
                break;
            case CODE_TO_TOKEN:
                PrometheusExporter.instance().recordCodeToToken(event, keycloakSession.realms());
                break;
            case REGISTER_ERROR:
                PrometheusExporter.instance().recordRegistrationError(event, keycloakSession.realms());
                break;
            case LOGIN_ERROR:
                PrometheusExporter.instance().recordLoginError(event, keycloakSession.realms());
                break;
            case CLIENT_LOGIN_ERROR:
                PrometheusExporter.instance().recordClientLoginError(event, keycloakSession.realms());
                break;
            case REFRESH_TOKEN_ERROR:
                PrometheusExporter.instance().recordRefreshTokenError(event, keycloakSession.realms());
                break;
            case CODE_TO_TOKEN_ERROR:
                PrometheusExporter.instance().recordCodeToTokenError(event, keycloakSession.realms());
                break;
            default:
                PrometheusExporter.instance().recordGenericEvent(event, keycloakSession.realms());
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        logAdminEventDetails(event);

        PrometheusExporter.instance().recordGenericAdminEvent(event, keycloakSession.realms());
    }

    private void logEventDetails(Event event) {
        logger.debugf("Received user event of type %s in realm %s",
                event.getType().name(),
                event.getRealmId());
    }

    private void logAdminEventDetails(AdminEvent event) {
        logger.debugf("Received admin event of type %s (%s) in realm %s",
                event.getOperationType().name(),
                event.getResourceType().name(),
                event.getRealmId());
    }

    @Override
    public void close() {
        // unused
    }
}
