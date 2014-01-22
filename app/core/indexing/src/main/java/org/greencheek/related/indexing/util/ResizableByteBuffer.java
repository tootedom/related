package org.greencheek.related.indexing.util;

import java.nio.ByteBuffer;

/**
 * Represents a byte[] that can dynamically grow as bytes are appended to it.
 */
public interface ResizableByteBuffer {
    int size();

    void reset();

    byte[] getBuf();

    ByteBuffer toByteBuffer();

    void append(byte b);

    void append(byte[] bytes);

    void append(byte[] b, int off, int len);
}
