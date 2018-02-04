package se.mbaeumer.fxlink.handlers;

import java.sql.Connection;

public interface DatabaseConnectionHandler {
    Connection getConnection();

}
