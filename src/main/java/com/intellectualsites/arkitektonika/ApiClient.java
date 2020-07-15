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

import java.io.FileInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Arkitektonika REST client
 */
public interface ApiClient {

    /**
     * Get the API version supported by this client
     *
     * @return API version
     */
    @NotNull ApiVersion getApiVersion();

    /**
     * Check if the specified API address is
     * compatible with the client
     *
     * @param executorService Executor service used to complete the request
     * @return Future that completes with the result of the query
     */
    @NotNull CompletableFuture<Boolean> checkCompatibility(@NotNull final ExecutorService executorService);

    /**
     * Upload a schematic via an input stream
     *
     * @param inputStream Input stream to read schematic data from
     * @param executorService Executor service used to complete the request
     * @return Future that completes with the access and deletion keys of the
     *         uploaded resource, or fails with an exception
     */
    @NotNull CompletableFuture<SchematicKeys> upload(@NotNull final FileInputStream inputStream,
        @NotNull final ExecutorService executorService);

    /**
     * Check the status of a remote schematic
     *
     * @param key Schematic access key
     * @param executorService Executor service used to complete the request
     * @return Future that completes with the resource status
     */
    @NotNull CompletableFuture<ResourceStatus> checkStatus(@NotNull final String key,
        @NotNull final ExecutorService executorService);

    /**
     * Attempt to delete a schematic from the remote service
     *
     * @param key Deletion key
     * @param executorService Executor service used to complete the request
     * @return Future that completes with the result
     */
    @NotNull CompletableFuture<Boolean> delete(@NotNull final String key,
        @NotNull final ExecutorService executorService);

    /**
     * Attempt to download a schematic from the remote service
     *
     * @param key Download key
     * @param executorService Executor service used to complete the request
     * @return Future that completes with the result
     */
    @NotNull CompletableFuture<Schematic> download(@NotNull final String key,
        @NotNull final ExecutorService executorService);

}
