package org.greencheek.related.indexing.util;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 07/12/2013
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class ResizableByteBufferNoBoundsCheckingTest extends ResizableByteBufferTest{
    @Override
    public ResizableByteBuffer getResizeableByteBuffer(int min, int max) {
        return new ResizableByteBufferNoBoundsChecking(min,max);
    }
}
