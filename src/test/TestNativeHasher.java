/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */

import hu.icell.security.NativeHasher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestNativeHasher {
    @Test
    void TestSHA256(){
        NativeHasher hasher = new NativeHasher();
        String hash = hasher.SHA256("test message");
        assertEquals(hash.toLowerCase(), "3f0a377ba0a4a460ecb616f6507ce0d8cfa3e704025d4fda3ed0c5ca05468728");
    }
}
