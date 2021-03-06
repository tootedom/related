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
 */

package com.github.emboss.siphash;

/**
 * https://github.com/emboss/siphash-java
 * @author <a href="mailto:Martin.Bosslet@googlemail.com">Martin Bosslet</a>
 */
public class SipHash {
    public static long digest(SipKey key, byte[] data) {
        long m;
        State s = new State(key);
        int iter = data.length >> 3;
        
        for(int i=0; i < iter; i++) {
            m = UnsignedInt64.binToIntOffset(data, i << 3);
            s.processBlock(m);
        }
        
        m = lastBlock(data, iter);
        s.processBlock(m);
        s.finish();
        return s.digest();
    }
    
    private static long lastBlock(byte[] data, int iter) {
        long last = ((long) data.length) << 56;
        int off = iter << 3;

        switch (data.length & 7) {
            case 7: 
                last |= ((long) data[off + 6]) << 48;
            case 6:
                last |= ((long) data[off + 5]) << 40;
            case 5:
                last |= ((long) data[off + 4]) << 32;
            case 4:
                last |= ((long) data[off + 3]) << 24;
            case 3:
                last |= ((long) data[off + 2]) << 16;
            case 2:
                last |= ((long) data[off + 1]) << 8;
            case 1:
                last |= (long) data[off];
                break;
            case 0:
                break;
        }
        return last;
    }
    
    private static class State {
        private long v0;
        private long v1;
        private long v2;
        private long v3;
        
        public State(SipKey key) {
            v0 = 0x736f6d6570736575L;
            v1 = 0x646f72616e646f6dL;
            v2 = 0x6c7967656e657261L;
            v3 = 0x7465646279746573L;
            
            long k0 = key.getLeftHalf();
            long k1 = key.getRightHalf();
            
            v0 ^= k0;
            v1 ^= k1;
            v2 ^= k0;
            v3 ^= k1;
        }

        private void compress() {
            v0 += v1;
            v2 += v3;
            v1 = UnsignedInt64.rotateLeft(v1, 13);
            v3 = UnsignedInt64.rotateLeft(v3, 16);
            v1 ^= v0;
            v3 ^= v2;
            v0 = UnsignedInt64.rotateLeft(v0, 32);
            v2 += v1;
            v0 += v3;
            v1 = UnsignedInt64.rotateLeft(v1, 17);
            v3 = UnsignedInt64.rotateLeft(v3, 21);
            v1 ^= v2;
            v3 ^= v0;
            v2 = UnsignedInt64.rotateLeft(v2, 32);
        }
        
        private void compressTimes(int times) {
            for (int i=0; i < times; i++) {
                compress();
            }
        }
        
        public void processBlock(long m) {
            v3 ^= m;
            compressTimes(2);
            v0 ^= m;
        }
        
        public void finish() {
            v2 ^= 0xff;
            compressTimes(4);
        }
        
        public long digest() {
            return v0 ^ v1 ^ v2 ^ v3;
        }
    }  
}
