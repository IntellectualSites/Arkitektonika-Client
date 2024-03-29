/*
 * MIT License
 *
 * Copyright (c) 2023 IntellectualSites
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
package com.intellectualsites.arkitektonika.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception thrown when Arkitektonika fails to retrieve a requested resource
 */
public class ResourceRetrievalException extends RuntimeException {

    private final String resource;

    public ResourceRetrievalException(@NotNull final String resource, final int code, @NotNull final String message) {
        super(String.format("Failed to retrieve '%s'. Code: %d (%s)", resource, code, message));
        this.resource = resource;
    }

    public ResourceRetrievalException(@NotNull final String resource, @NotNull final Throwable cause) {
        super(String.format("Failed to retrieve '%s': %s", resource, cause.getMessage()), cause);
        this.resource = resource;
    }

    /**
     * Get the resource that couldn't be retrieved
     *
     * @return Failed resource
     */
    @NotNull public String getResource() {
        return this.resource;
    }

}
