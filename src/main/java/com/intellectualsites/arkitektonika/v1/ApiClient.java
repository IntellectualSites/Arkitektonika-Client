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
package com.intellectualsites.arkitektonika.v1;

import com.intellectualsites.arkitektonika.ApiVersion;
import com.intellectualsites.arkitektonika.ResourceStatus;
import com.intellectualsites.arkitektonika.Schematic;
import com.intellectualsites.arkitektonika.SchematicKeys;
import com.intellectualsites.arkitektonika.exceptions.InvalidFormatException;
import com.intellectualsites.arkitektonika.exceptions.ResourceRetrievalException;
import com.intellectualsites.arkitektonika.exceptions.ResourceUploadException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public final class ApiClient implements com.intellectualsites.arkitektonika.ApiClient {

    public ApiClient(@NotNull final String url) {
        Unirest.config().defaultBaseUrl(url);
    }

    @NotNull @Override public ApiVersion getApiVersion() {
        return ApiVersion.V1_0_0;
    }

    @NotNull @Override public CompletableFuture<Boolean> checkCompatibility(@NotNull final ExecutorService service) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        service.execute(() -> Unirest.get("/").asJson()
            .ifFailure(failure -> {
                if (failure.getParsingError().isPresent()) {
                    future.completeExceptionally(new ResourceRetrievalException("/", failure.getParsingError().get()));
                } else {
                    future.completeExceptionally(new ResourceRetrievalException("/", failure.getStatus(), failure.getStatusText()));
                }
            })
            .ifSuccess(success -> {
                final JsonNode node = success.getBody();
                future.complete(node.getObject().has("version") && node.getObject().getString("version").startsWith("1."));
            }));
        return future;
    }

    @NotNull @Override public CompletableFuture<SchematicKeys> upload(@NotNull final InputStream inputStream,
        @NotNull final ExecutorService service) {
        final CompletableFuture<SchematicKeys> future = new CompletableFuture<>();
        service.execute(() -> Unirest.post("/upload").field("schematic", inputStream, "upload.schem").asJson()
            .ifFailure(failure -> {
                if (failure.getParsingError().isPresent()) {
                    future.completeExceptionally(new ResourceUploadException("/", failure.getParsingError().get()));
                } else if (failure.getStatus() == 400) {
                    future.completeExceptionally(new InvalidFormatException("/", 400, failure.getStatusText()));
                } else {
                    future.completeExceptionally(new ResourceUploadException("/", failure.getStatus(), failure.getStatusText(), "Other"));
                }
            })
            .ifSuccess(success -> {
               final JsonNode node = success.getBody();
               future.complete(new SchematicKeys(node.getObject().getString("download_key"), node.getObject().getString("delete_key")));
            }));
        return future;
    }

    @NotNull @Override public CompletableFuture<ResourceStatus> checkStatus(@NotNull final String key,
        @NotNull final ExecutorService service) {
        final CompletableFuture<ResourceStatus> future = new CompletableFuture<>();
        service.execute(() -> {
            final HttpResponse<?> response = Unirest.head("/download/{key}").routeParam("key", key).asEmpty();
            switch (response.getStatus()) {
                case 200:
                    future.complete(ResourceStatus.OK);
                    break;
                case 404:
                    future.complete(ResourceStatus.NON_EXISTENT);
                    break;
                case 410:
                    future.complete(ResourceStatus.DELETED);
                    break;
                default:
                    future.completeExceptionally(new ResourceRetrievalException("/download/" + key, response.getStatus(), response.getStatusText()));
                    break;
            }
        });
        return future;
    }

    @Override @NotNull public CompletableFuture<Boolean> delete(@NotNull String key,
        @NotNull final ExecutorService service) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        service.execute(() -> Unirest.delete("/delete/{key}").routeParam("key", key).asBytes()
            .ifFailure(failure -> {
                if (failure.getParsingError().isPresent()) {
                    future.completeExceptionally(new ResourceRetrievalException("/delete/" + key, failure.getParsingError().get()));
                } else {
                    future.completeExceptionally(new ResourceRetrievalException("/delete/" + key, failure.getStatus(), failure.getStatusText()));
                }
            }).ifSuccess(success -> future.complete(true)));
        return future;
    }

    @Override @NotNull public CompletableFuture<Schematic> download(@NotNull String key,
        @NotNull final ExecutorService service) {
        final CompletableFuture<Schematic> future = new CompletableFuture<>();
        service.execute(() -> Unirest.get("/download/{key}").routeParam("key", key).asBytes()
            .ifFailure(failure -> {
                if (failure.getParsingError().isPresent()) {
                    future.completeExceptionally(new ResourceRetrievalException("/download/" + key, failure.getParsingError().get()));
                } else {
                    future.completeExceptionally(new ResourceRetrievalException("/download/" + key, failure.getStatus(), failure.getStatusText()));
                }
            }).ifSuccess(success -> future.complete(new Schematic(key, success.getBody()))));
        return future;
    }

}
