package com.nucleus.opengl;

import java.util.ArrayList;
import java.util.List;

import com.nucleus.BackendException;
import com.nucleus.opengl.GLESWrapper.Error;

/**
 * An exception for GL / GLES error codes, since GL and GLES are based on C APIs there is no support for Exceptions
 * in the API.
 * Use this exception when there is an error reported from GL / GLES.
 * 
 * @author Richard Sahlin
 *
 */
public class GLException extends BackendException {

    /**
     * One or more error codes from OpenGL/ES
     */
    private List<Integer> errorCodes = new ArrayList<Integer>();

    /**
     * Creates a new GLException with detail message and one error code from OpenGL.
     * 
     * @param detailMessage
     * @param errorCode
     */
    public GLException(String detailMessage, int errorCode) {
        super(detailMessage);
        errorCodes.add(errorCode);
    }

    /**
     * Creates a new GLException with detail message
     * 
     * @param detailMessage
     */
    public GLException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Creates a new GLException with message and list of error codes.
     * 
     * @param detailMessage
     * @param errorCodes One or more GLError codes.
     */
    public GLException(String detailMessage, List<Integer> errorCodes) {
        super(detailMessage);
        this.errorCodes = errorCodes;
    }

    /**
     * Returns the error code(s) that are the reason for this exception, can be 1 or more error codes from OpenGL.
     * 
     * @return One or more OpenGL error code(s)
     */
    public List<Integer> getErrorCodes() {
        return errorCodes;
    }

    /**
     * Checks if the exception contains the specified error code, ie the error code is in the list of error codes for
     * this exception.
     * 
     * @param errorCode
     * @return True if the list of error codes in this exception contains errorCode, false otherwise.
     */
    public boolean isErrorCode(int errorCode) {
        for (int error : errorCodes) {
            if (error == errorCode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Utility method to turn this exception into readable string.
     */
    @Override
    public String toString() {
        StringBuilder message = new StringBuilder(getMessage());
        for (int error : errorCodes) {
            Error e = Error.getError(error);
            if (e != null) {
                message.append(", " + e + "(" + error + ")");
            } else {
                message.append(", " + error);
            }
        }
        return message.toString();
    }

}
