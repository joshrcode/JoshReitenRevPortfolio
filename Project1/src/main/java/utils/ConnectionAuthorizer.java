package utils;

import org.postgresql.Driver;

final class ConnectionAuthorizer {
    private final String URL =
            "jdbc:postgresql://first-database.ca2hagivlgin.us-east-2.rds.amazonaws.com:5432/reimbursement";

    private final String USERNAME = "jrcode";
    private final String PASSWORD = "firstDatabasePassw0rd";
    private final Driver DRIVER = new Driver();


    // Getters


    public String getURL() {return URL;}

    public String getUSERNAME() {return USERNAME;}

    public String getPASSWORD() {return PASSWORD;}

    public Driver getDRIVER() {return DRIVER;}
}
