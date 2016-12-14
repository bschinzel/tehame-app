package de.tehame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class InputStreamReader {

    private InputStreamReader() { super(); }

    /**
     * Liest einen InputStream aus und erstellt daraus ein Byte Array.
     * @param inputStream Input Stream.
     * @return Byte Array.
     * @throws IOException I/O Error.
     */
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}
