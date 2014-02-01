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

package org.greencheek.related.util.arrayindexing;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    /**
     * Calculate the next power of 2, greater than or equal to x.<p>
     * From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
     *
     * @param x Value to round up
     * @return The next power of 2 from x inclusive
     */
    public static int ceilingNextPowerOfTwo(final int x)
    {
        return 1 << (32 - Integer.numberOfLeadingZeros(x - 1));
    }


    private static final Unsafe THE_UNSAFE;
    private static final long CHAR_ARRAY_OFFSET;
    private static final long CHAR_ARRAY_SCALE;
    private static final long INT_ARRAY_OFFSET;
    private static final long INT_ARRAY_SCALE;
    private static final long stringOffset;
    private static final long stringCharsArrayOffset;


    static
    {
        try
        {
            final PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>()
            {
                public Unsafe run() throws Exception
                {
                    Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    return (Unsafe) theUnsafe.get(null);
                }
            };

            THE_UNSAFE = AccessController.doPrivileged(action);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load unsafe", e);
        }
        CHAR_ARRAY_OFFSET = THE_UNSAFE.arrayBaseOffset(char[].class);
        CHAR_ARRAY_SCALE = THE_UNSAFE.arrayIndexScale(char[].class);
        INT_ARRAY_OFFSET = THE_UNSAFE.arrayBaseOffset(int[].class);
        INT_ARRAY_SCALE = THE_UNSAFE.arrayIndexScale(int[].class);

        try {
            stringCharsArrayOffset = THE_UNSAFE.objectFieldOffset(String.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            throw new InstantiationError("Unable to manipulate string object");
        }

        Field declaredField = null;
        try {
            declaredField = String.class.getDeclaredField("offset");
        }
        // this will happen for jdk7 as these fields have been removed
        catch (NoSuchFieldException e) {
            declaredField = null;
        }
        if (declaredField != null) {
            stringOffset = THE_UNSAFE.objectFieldOffset(declaredField);
        } else {
            stringOffset = -1L;
        }

    }

    /**
     * Get a handle on the Unsafe instance, used for accessing low-level concurrency
     * and memory constructs.
     * @return The Unsafe
     */
    public static Unsafe getUnsafe()
    {
        return THE_UNSAFE;
    }

    public static long getCharArrayOffset() {
        return CHAR_ARRAY_OFFSET;
    }

    public static long getCharArrayScale() {
        return CHAR_ARRAY_SCALE;
    }

    public static long getIntArrayOffset() {
        return INT_ARRAY_OFFSET;
    }

    public static long getIntArrayScale() {
        return INT_ARRAY_SCALE;
    }

    public static void copyStringCharacterArray(String stringToCopy, char[] destination, int length,int destOffset) {
        char[] stringChars =(char[]) THE_UNSAFE.getObject(stringToCopy, stringCharsArrayOffset);
        if(stringOffset>-1) {
            getUnsafe().copyMemory(stringChars, Util.getCharArrayOffset()+(stringOffset*CHAR_ARRAY_SCALE), destination, Util.getCharArrayOffset()+(destOffset*CHAR_ARRAY_SCALE), length*CHAR_ARRAY_SCALE);
        } else {
            getUnsafe().copyMemory(stringChars, Util.getCharArrayOffset(), destination, Util.getCharArrayOffset()+(destOffset*CHAR_ARRAY_SCALE), length*CHAR_ARRAY_SCALE);
        }

    }
}
