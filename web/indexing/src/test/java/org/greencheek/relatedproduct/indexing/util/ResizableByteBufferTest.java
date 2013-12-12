package org.greencheek.relatedproduct.indexing.util;


import org.junit.Test;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public abstract class ResizableByteBufferTest {

    public abstract ResizableByteBuffer getResizeableByteBuffer(int min, int max);

    @Test
    public void testCanAppend() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(1,2);
        buffer.append((byte)88);

        ByteBuffer b =  buffer.toByteBuffer();
        assertEquals(1, b.limit());

        assertEquals(88,b.get());

        try {
            b.get();
            fail("should not be able to get another byte");
        } catch(BufferUnderflowException e) {

        }
    }

    @Test
    public void testCanAppendSizeAndMultipleBytes() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(1,16);
        buffer.append((byte)88);
        buffer.append(new byte[]{1,2,3,4});
        buffer.append((byte)6);

        ByteBuffer b =  buffer.toByteBuffer();
        assertEquals(6, b.limit());

        assertEquals(88,b.get());
        assertEquals(1,b.get());
        assertEquals(2,b.get());
        assertEquals(3,b.get());
        assertEquals(4,b.get());
        assertEquals(6,b.get());
    }

    @Test
    public void testCanGetUnderlyingByteArray() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(1,4);
        buffer.append((byte)88);
        buffer.append((byte)88);
        buffer.append((byte)88);

        assertNotNull(buffer.getBuf());

        assertTrue(buffer.getBuf().length>2);
    }

    @Test
    public void testCanGrow() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(1,2);
        buffer.append((byte)88);
        buffer.append((byte)89);

        ByteBuffer b =  buffer.toByteBuffer();
        assertEquals(2,b.limit());

        assertEquals(88,b.get());
        assertEquals(89,b.get());

        try {
            b.get();
            fail("should not be able to get another byte");
        } catch(BufferUnderflowException e) {

        }
    }

    @Test
    public void testSizeIsCorrect() {
        ResizableByteBuffer buffer =  getResizeableByteBuffer(1, 2);
        buffer.append((byte)88);
        buffer.append((byte)89);

        assertEquals(2, buffer.size());

        buffer =  getResizeableByteBuffer(1, 4);
        buffer.append((byte)88);
        buffer.append((byte)89);
        buffer.append((byte)89);

        assertEquals(3,buffer.size());
    }

    @Test
    public void testCannotGrowOverMaxSize() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(1,2);
        buffer.append((byte)88);
        buffer.append((byte)89);
        try {
            buffer.append((byte)90);
            fail("should not be able to append more than the maximum");
        } catch(BufferOverflowException e) {

        }


        ByteBuffer b =  buffer.toByteBuffer();
        assertEquals(2,b.limit());

        assertEquals(88,b.get());
        assertEquals(89,b.get());

        try {
            b.get();
            fail("should not be able to get another byte");
        } catch(BufferUnderflowException e) {

        }
    }


    @Test
    public void testCannotGrowOverMaxSizeWithSameMinMaxInitialisation() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(2,2);
        buffer.append((byte)88);
        buffer.append((byte)89);
        try {
            buffer.append((byte)90);
            fail("should not be able to append more than the maximum");
        } catch(BufferOverflowException e) {

        }


        ByteBuffer b =  buffer.toByteBuffer();
        assertEquals(2,b.limit());

        assertEquals(88,b.get());
        assertEquals(89,b.get());

        try {
            b.get();
            fail("should not be able to get another byte");
        } catch(BufferUnderflowException e) {

        }
    }

    @Test
    public void testCannotGrowOverMaxSizeWithByteArrayAppend() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(1,2);
        buffer.append((byte)88);

        try {
            buffer.append(new byte[]{89,90});
            fail("should not be able to append more than the maximum");
        } catch(BufferOverflowException e) {

        }


        ByteBuffer b =  buffer.toByteBuffer();
        assertEquals(1,b.limit());
        assertEquals(88,b.get());

        try {
            b.get();
            fail("should not be able to get another byte");
        } catch(BufferUnderflowException e) {

        }
    }


    @Test
    public void testCanResetBuffer() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(1,10);
        buffer.append((byte)90);
        buffer.append(new byte[]{91,92,93,94,95,96,97,98,99});



        try {
            buffer.append((byte)100);
            fail("should not be able to add another byte");
        } catch(BufferOverflowException e) {

        }

        buffer.reset();
        buffer.append(new byte[]{1,2,3,4,5,6,7,8,9,10});

        try {
            buffer.append((byte)11);
            fail("should not be able to add another byte");
        } catch(BufferOverflowException e) {

        }

        ByteBuffer b =  buffer.toByteBuffer();
        assertEquals(10,b.limit());


        assertEquals(1,b.get());
        assertEquals(2,b.get());
        assertEquals(3,b.get());
        assertEquals(4,b.get());
        assertEquals(5,b.get());
        assertEquals(6,b.get());
        assertEquals(7,b.get());
        assertEquals(8,b.get());
        assertEquals(9,b.get());
        assertEquals(10,b.get());

        try {
            b.get();
            fail("should not be able to get another byte");
        } catch(BufferUnderflowException e) {

        }
    }

    @Test
    public void testCanResetBufferWithPartialByteArrayAppend() {
        ResizableByteBuffer buffer = getResizeableByteBuffer(1,10);
        buffer.append((byte)90);
        buffer.append(new byte[]{91,92,93,94,95,96,97,98,99});



        try {
            buffer.append((byte)100);
            fail("should not be able to add another byte");
        } catch(BufferOverflowException e) {

        }

        buffer.reset();
        buffer.append(new byte[]{1,2,3,4,5,6,7,8,9,10},0,5);
        buffer.append(new byte[]{1,2,3,4,5,6,7,8,9,10},6,4);

        try {
            buffer.append((byte)6);
        } catch(BufferOverflowException e) {
            fail("should be able to add another byte");
        }

        try {
            buffer.append((byte)11);
            fail("should not be able to add another byte");

        } catch(BufferOverflowException e) {
        }

        ByteBuffer b =  buffer.toByteBuffer();
        assertEquals(10,b.limit());


        assertEquals(1,b.get());
        assertEquals(2,b.get());
        assertEquals(3,b.get());
        assertEquals(4,b.get());
        assertEquals(5,b.get());
        assertEquals(7,b.get());
        assertEquals(8,b.get());
        assertEquals(9,b.get());
        assertEquals(10,b.get());
        assertEquals(6,b.get());

        try {
            b.get();
            fail("should not be able to get another byte");
        } catch(BufferUnderflowException e) {

        }
    }
}
