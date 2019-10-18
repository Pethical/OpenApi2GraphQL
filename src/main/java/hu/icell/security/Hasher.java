/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */

package hu.icell.security;

public interface Hasher {
    String SHA256(String message);
    String SHA512(String message);
}
