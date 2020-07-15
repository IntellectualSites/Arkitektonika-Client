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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Arkitektonika client class
 *
 * @author Alexander Söderberg
 * @version 1.0
 * @since 2020-06-21
 */
@SuppressWarnings("unused") public class Arkitektonika {

    /**
     * Factory used to construct new {@link ApiClient} instances
     */
    private static final ClientFactory clientFactory = new ClientFactory();

    private final ApiClient client;
    private final ExecutorService executorService;

    private Arkitektonika(@NotNull final String url, @NotNull final ApiVersion apiVersion,
        @NotNull final ExecutorService executorService) {
        this.client = clientFactory.getClient(apiVersion, url);
        this.executorService = executorService;
    }

    /**
     * Create a new {@link Arkitektonika} {@link Builder builder}
     *
     * @return New builder
     */
    @NotNull public static Builder builder() {
        return new Builder();
    }

    /**
     * Get the internal {@link ApiClient} instance. It is not recommended
     * to interact with this client directly.
     *
     * @return Internal client
     */
    @NotNull public ApiClient getClient() {
        return this.client;
    }

    /**
     * Check if the specified address is compatible with the
     * Arkitektonika client
     *
     * @return Future that completes with the result
     */
    @NotNull public CompletableFuture<Boolean> isCompatible() {
        return this.client.checkCompatibility(this.executorService);
    }

    /**
     * Upload a schematic via an input stream and return
     * the generated access keys
     *
     * @param stream Stream containing the schematic
     * @return Future that completes with the generated keys
     */
    @NotNull public CompletableFuture<SchematicKeys> upload(@NotNull final FileInputStream stream) {
        return this.client.upload(stream, this.executorService);
    }

    /**
     * Upload the schematic that is contained in a given file
     * and return the generated access keys
     *
     * @param file Schematic File
     * @return Future that completes with the generated keys
     * @throws FileNotFoundException If the file does not exist
     */
    @NotNull public CompletableFuture<SchematicKeys> upload(@NotNull final File file) throws
        FileNotFoundException {
        return this.upload(new FileInputStream(file));
    }

    /**
     * Check the status of a remote schematic
     *
     * @param key Schematic access key
     * @return Future that completes with the resource status
     */
    @NotNull public CompletableFuture<ResourceStatus> checkStatus(@NotNull final String key) {
        return this.client.checkStatus(key, this.executorService);
    }

    /**
     * Attempt to delete a schematic from the remote service
     *
     * @param key Deletion key
     * @return Future that completes with the result
     */
    @NotNull public CompletableFuture<Boolean> delete(@NotNull final String key) {
        return this.client.delete(key, this.executorService);
    }

    /**
     * Attempt to download a schematic from the remote service
     *
     * @param key Download key
     * @return Future that completes with the result
     */
    @NotNull public CompletableFuture<Schematic> download(@NotNull final String key) {
        return this.client.download(key, this.executorService);
    }


    /**
     * Builder class for {@link Arkitektonika} instances.
     *
     * @see Arkitektonika#builder() to get a new builder instance
     */
    public static final class Builder {

        private String url;
        private ApiVersion version = ApiVersion.V1_0_0;
        private ExecutorService executorService = Executors.newCachedThreadPool();

        private Builder() {
        }

        /**
         * Specify the base URL that the client will interact with
         *
         * @param url Arkitektonika URL
         * @return The builder instance
         */
        @NotNull public Builder withUrl(@NotNull final String url) {
            this.url = Objects.requireNonNull(url);
            return this;
        }

        /**
         * Specify the API version that the remote Arkitektonika instance
         * uses
         *
         * @param version API version
         * @return The builder instance
         */
        @NotNull public Builder withVersion(@NotNull final ApiVersion version) {
            this.version = Objects.requireNonNull(version);
            return this;
        }

        /**
         * Specify the executor service that should be used by the client
         *
         * @param executorService Executor service
         * @return The builder instance
         */
        @NotNull public Builder withExecutorService(
            @NotNull final ExecutorService executorService) {
            this.executorService = Objects.requireNonNull(executorService);
            return this;
        }

        /**
         * Initialize the Arkitektonika instance. This will if no URL
         * has been specified
         *
         * @return Created Arkitektonika instance
         */
        @NotNull public Arkitektonika build() {
            if (this.url == null) {
                throw new NullPointerException("No URL was provided");
            }
            return new Arkitektonika(this.url, this.version, this.executorService);
        }

    }

}
