/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.indexing.util;

import org.junit.Test;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
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




        try {
            buffer.append(new byte[]{1,2,3,4,5,6,7,8,9,10},4,6);
            fail("should not be able to add more that the max");
        } catch(BufferOverflowException e) {
            buffer.append(new byte[]{1,2,3,4,5,6,7,8,9,10},5,5);
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

    @Override
    public ResizableByteBuffer getResizeableByteBuffer(int min, int max) {
        return new ResizableByteBufferWithMaxArraySizeChecking(min,max);
    }
}
