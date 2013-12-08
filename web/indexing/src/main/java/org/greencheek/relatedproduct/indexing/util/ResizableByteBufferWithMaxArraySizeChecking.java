package org.greencheek.relatedproduct.indexing.util;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class ResizableByteBufferWithMaxArraySizeChecking implements ResizableByteBuffer {

    static final int MAX_ARRAY_SIZE =Integer.MAX_VALUE - 8;
    protected byte[] buf;
    protected int currentCapacityLeft;
    protected int position;
    private final int maxCapacity;
    private boolean hasSpaceToGrow = true;

    public ResizableByteBufferWithMaxArraySizeChecking(int initialCapacity, int maxCapacity) {
        currentCapacityLeft = initialCapacity;
        buf = new byte[initialCapacity];
        this.maxCapacity = maxCapacity;
        if(initialCapacity == maxCapacity) {
            hasSpaceToGrow = false;
        }
    }

    @Override
    public int size() {
        return position;
    }

    @Override
    public void reset() {
        position = 0;
        currentCapacityLeft = buf.length;
    }

    @Override
    public byte[] getBuf() {
        return buf;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(buf,0,position);
    }

    private void checkSizeAndGrow(int extra) {
        if(extra>currentCapacityLeft) {
           if(hasSpaceToGrow) {
               grow(extra);
           } else {
               throw new BufferOverflowException();
           }
        }
    }

    private void grow(int extra) {
        int currentCapacity = buf.length;
        int requiredCapacity = position+extra;
        // over the integer width
        if(requiredCapacity<0) throw new BufferOverflowException();
        if(requiredCapacity>MAX_ARRAY_SIZE) throw new BufferOverflowException();

        int newSize = currentCapacity*2;

        if(newSize>=maxCapacity) {
            // new size is less than the required capacity
            newSize = maxCapacity;
            if(newSize<requiredCapacity) throw new BufferOverflowException();

            hasSpaceToGrow = false;
        } else {
            if(newSize<requiredCapacity) {
                newSize = requiredCapacity;
            }
        }


        currentCapacityLeft = newSize - position;

        byte[] newBuf = new byte[newSize];
        System.arraycopy(buf,0,newBuf,0,position);
        buf = newBuf;
    }

    @Override
    public void append(byte b) {
        checkSizeAndGrow(1);
        appendNoResize(b);
    }

    private void appendNoResize(byte c) {
        buf[position++]=c;
        currentCapacityLeft--;
    }

    private void appendNoResize(byte[] bytes,int len) {
        System.arraycopy(bytes,0,buf,position,len);
        position+=len;
        currentCapacityLeft-=len;
    }

    @Override
    public void append(byte[] bytes) {
        int len = bytes.length;
        checkSizeAndGrow(len);
        appendNoResize(bytes,len);
    }

    @Override
    public void append(byte[] b, int off, int len) {
        checkSizeAndGrow(len);
        System.arraycopy(b,off,buf,position,len);
        position+=len;
        currentCapacityLeft-=len;
    }
}