//
// MIT License
//
// Copyright (c) 2020 IntellectualSites
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package com.intellectualsites.arkitektonika;

import org.jetbrains.annotations.NotNull;

/**
 * Schematic access &amp; deletion key pair
 */
public final class SchematicKeys {

    private final String accessKey;
    private final String deletionKey;

    public SchematicKeys(@NotNull final String accessKey, @NotNull final String deletionKey) {
        this.accessKey = accessKey;
        this.deletionKey = deletionKey;
    }

    /**
     * Get the key that is used to access the uploaded schematic
     *
     * @return Access key
     */
    @NotNull public String getAccessKey() {
        return this.accessKey;
    }

    /**
     * Get the key that is used to delete the schematic
     *
     * @return Deletion key
     */
    @NotNull public String getDeletionKey() {
        return this.deletionKey;
    }

}
