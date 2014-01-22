package com.github.emboss.siphash;

/**
 * https://github.com/emboss/siphash-java
 * @author <a href="mailto:Martin.Bosslet@googlemail.com">Martin Bosslet</a>
 */
public class SipKey {
    private final byte[] key;
    private final long leftHalf;
    private final long rightHalf;
    
    public SipKey(byte[] key) {
        if (key == null || key.length != 16)
            throw new RuntimeException("SipHash key must be 16 bytes");
        this.key = key;
        leftHalf = createLeftHalf();
        rightHalf = createRightHalf();
    }
    
    long createLeftHalf() {
       return UnsignedInt64.binToIntOffset(key, 0); 
    }

    long getLeftHalf() {
        return leftHalf;
    }

    long getRightHalf() {
        return rightHalf;
    }

    long createRightHalf() {
        return UnsignedInt64.binToIntOffset(key, 8); 
    }
}
