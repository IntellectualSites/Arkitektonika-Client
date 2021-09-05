/*
 * MIT License
 *
 * Copyright (c) 2021 IntellectualSites
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.intellectualsites.arkitektonika;

import org.jetbrains.annotations.NotNull;

/**
 * Object that represents a downloaded schematic
 */
public final class Schematic {

    private final String key;
    private final byte[] content;

    public Schematic(@NotNull final String key, @NotNull final byte[] content) {
        this.key = key;
        this.content = content;
    }

    /**
     * Get the schematic key
     *
     * @return Schematic key
     */
    @NotNull public String getKey() {
        return this.key;
    }

    /**
     * Get the (raw) schematic content
     *
     * @return Schematic content
     */
    @NotNull public byte[] getContent() {
        return this.content;
    }

}
