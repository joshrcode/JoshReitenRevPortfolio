package web.responses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.servlets.Servlet;

public abstract class Response {
    private static Logger logger = LogManager.getLogger(Servlet.class);
    private String message;

    public String getMessage() {
        return this.message;
    }

    protected void setMessage(String message) {
        this.message = message;
        logger.info(this.getClass().getSimpleName()+" created.");
    }
}