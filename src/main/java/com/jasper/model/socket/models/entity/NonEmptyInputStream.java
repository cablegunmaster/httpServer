package com.jasper.model.socket.models.entity;

import javax.annotation.Nonnull;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * @author Lorber Sebastien <i>(lorber.sebastien@gmail.com)</i>
 */
//TODO see if this class actually adds value or obstructs.
public class NonEmptyInputStream extends FilterInputStream {

    /**
     * Once this stream has been created, do not consume the original InputStream
     * because there will be one missing byte...
     *
     * @param originalInputStream
     * @throws IOException
     * @throws EmptyInputStreamException
     */
    public NonEmptyInputStream(InputStream originalInputStream) throws IOException, EmptyInputStreamException {
        super(checkStreamIsNotEmpty(originalInputStream));
    }


    /**
     * Permits to check the InputStream is empty or not
     * Please note that only the returned InputStream must be consumed.
     * <p>
     * see:
     * http://stackoverflow.com/questions/1524299/how-can-i-check-if-an-inputstream-is-empty-without-reading-from-it
     *
     * @param inputStream
     * @return
     */
    private static InputStream checkStreamIsNotEmpty(@Nonnull InputStream inputStream) throws IOException, EmptyInputStreamException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
        int b;
        b = pushbackInputStream.read();
        if (b == -1) {
            throw new EmptyInputStreamException("No byte can be read from stream " + inputStream);
        }
        pushbackInputStream.unread(b);
        return pushbackInputStream;
    }

    public static class EmptyInputStreamException extends RuntimeException {
        public EmptyInputStreamException(String message) {
            super(message);
        }
    }

}
