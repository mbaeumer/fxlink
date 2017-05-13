package se.mbaeumer.fxlink.util;

/**
 * Created by martinbaumer on 10/05/17.
 */
public class SqlExceptionMapper {
    public final static String RESPONSE_DUPLICATE_URL = "The URL does already exist";
    public final static String SQL_ERROR_DUPLICATE = "unique constraint";
    public final static String RESPONSE_URL_TOO_LONG = "The URL is too long";
    public final static String SQL_ERROR_DATA = "data exception";

    public static String constructErrorMessage(String exceptionMessage){
        String message = null;

        if (exceptionMessage.contains(SQL_ERROR_DUPLICATE)){
            message = RESPONSE_DUPLICATE_URL;
        }else if (exceptionMessage.contains(SQL_ERROR_DATA)){
            message = RESPONSE_URL_TOO_LONG;
        }

        return message;
    }
}
