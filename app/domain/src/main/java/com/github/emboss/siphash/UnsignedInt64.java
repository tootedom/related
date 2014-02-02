/*
Copyright (c) 2012 Martin Boßlet

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.github.emboss.siphash;

/**
 * https://github.com/emboss/siphash-java
 * @author <a href="mailto:Martin.Bosslet@googlemail.com">Martin Bosslet</a>
 */
class UnsignedInt64 {
    private UnsignedInt64() {}
    
    public static long binToInt(byte[] b) {
        return  binToIntOffset(b, 0);
    }
    
    public static long binToIntOffset(byte[] b, int off) {
        return ((long) b[off    ])       |
               ((long) b[off + 1]) << 8  |
               ((long) b[off + 2]) << 16 |
               ((long) b[off + 3]) << 24 |
               ((long) b[off + 4]) << 32 |
               ((long) b[off + 5]) << 40 |
               ((long) b[off + 6]) << 48 |
               ((long) b[off + 7]) << 56;
    }
    
    public static void intToBin(long l, byte[] b) {
        b[0] = (byte) ( l         & 0xff);
        b[1] = (byte) ((l >>> 8 ) & 0xff);
        b[2] = (byte) ((l >>> 16) & 0xff);
        b[3] = (byte) ((l >>> 24) & 0xff);
        b[4] = (byte) ((l >>> 32) & 0xff);
        b[5] = (byte) ((l >>> 40) & 0xff);
        b[6] = (byte) ((l >>> 48) & 0xff);
        b[7] = (byte) ((l >>> 56) & 0xff);
    }
    
    public static long rotateLeft(long l, int shift) {
        return (l << shift) | l >>> (64 - shift);
    }
}