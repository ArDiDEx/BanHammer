package fr.ardidex.banhammer.settings;

public record DatabaseAuth(String host, int port, String database, String user, String password, boolean useSSL) {
}
