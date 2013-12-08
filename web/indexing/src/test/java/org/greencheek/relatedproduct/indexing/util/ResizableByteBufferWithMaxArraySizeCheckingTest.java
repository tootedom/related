package org.greencheek.relatedproduct.indexing.util;

import org.junit.Test;

import java.nio.BufferOverflowException;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * The array bounds requires just over a 2gb array.
 * which is *2.  So the memory requirements for the test is large.
 * to run... set the system property -DRunLargeHeapTests=true
 */
public class ResizableByteBufferWithMaxArraySizeCheckingTest extends ResizableByteBufferTest {

    // Can only be run with a large large heap
    // -Xmx8096m -Xmn6144m
    @Test
    public void testIntegerWidthIsCaught() {
        assumeTrue(Boolean.parseBoolean(System.getProperty("RunLargeHeapTests","false")));
        ResizableByteBuffer buffer = new ResizableByteBufferWithMaxArraySizeChecking(1,Integer.MAX_VALUE);
        byte[] bigArray = new byte[Integer.MAX_VALUE-8];
        try {
            buffer.append(bigArray);
            bigArray=null;
        } catch(BufferOverflowException e) {
            fail();
        }

        try{
            buffer.append(new byte[]{1,2,3,4,5,6,7,8,9,10});
            fail("Should overflow");
        } catch (BufferOverflowException e) {

        }
    }

    @Override
    public ResizableByteBuffer getResizeableByteBuffer(int min, int max) {
        return new ResizableByteBufferWithMaxArraySizeChecking(min,max);
    }
}
