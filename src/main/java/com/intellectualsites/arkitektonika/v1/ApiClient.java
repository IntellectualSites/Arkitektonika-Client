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
package com.intellectualsites.arkitektonika.v1;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.intellectualsites.arkitektonika.ApiVersion;
import com.intellectualsites.arkitektonika.ResourceStatus;
import com.intellectualsites.arkitektonika.Schematic;
import com.intellectualsites.arkitektonika.SchematicKeys;
import com.intellectualsites.arkitektonika.exceptions.InvalidFormatException;
import com.intellectualsites.arkitektonika.exceptions.ResourceRetrievalException;
import com.intellectualsites.arkitektonika.exceptions.ResourceUploadException;
import com.intellectualsites.http.ContentType;
import com.intellectualsites.http.EntityMapper;
import com.intellectualsites.http.HttpClient;
import com.intellectualsites.http.HttpResponse;
import com.intellectualsites.http.external.GsonMapper;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public final class ApiClient implements com.intellectualsites.arkitektonika.ApiClient {

    private final HttpClient httpClient;

    public ApiClient(@NotNull final String url) {
        this.httpClient = HttpClient.newBuilder().withBaseURL(url).withEntityMapper(EntityMapper.newInstance()
            .registerDeserializer(JsonObject.class, GsonMapper.deserializer(JsonObject.class, new GsonBuilder().create()))
            .registerSerializer(File.class, new SchematicSerializer())).build();
    }

    @NotNull @Override public ApiVersion getApiVersion() {
        return ApiVersion.V1_0_0;
    }

    @NotNull @Override public CompletableFuture<Boolean> checkCompatibility(@NotNull final ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> {
           final HttpResponse response =
               httpClient.get("/").onStatus(200, ignore -> {})
                              .onRemaining(r -> {
                                  throw new ResourceRetrievalException("/", r.getStatusCode(), r.getStatus());
                              }).execute();
           final JsonObject object = Objects.requireNonNull(response, "Failed to retrieve response")
               .getResponseEntity(JsonObject.class);
           return object.has("version") && object.get("version").getAsString().startsWith("1.");
        }, service);
    }

    @NotNull @Override public CompletableFuture<SchematicKeys> upload(@NotNull final File file,
        @NotNull final ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> {
           final HttpResponse response = httpClient.post("/upload").withInput(() -> file)
               .onStatus(400, httpResponse -> {
                    throw new InvalidFormatException("/upload", 400, httpResponse.getStatus());
                })
               .onStatus(200, httpResponse -> {})
               .onRemaining(httpResponse -> {
                   throw new ResourceUploadException("/upload", httpResponse.getStatusCode(), httpResponse.getStatus(), "Other");
               }).execute();
           final JsonObject object = Objects.requireNonNull(response, "Failed to get response").getResponseEntity(JsonObject.class);
           return new SchematicKeys(object.get("download_key").getAsString(), object.get("delete_key").getAsString());
        }, service);
    }

    @NotNull @Override public CompletableFuture<ResourceStatus> checkStatus(@NotNull final String key,
        @NotNull final ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> {
            final HttpResponse response = httpClient.head(String.format("/download/%s", key)).execute();
            if (response == null) {
                throw new ResourceRetrievalException(String.format("/download/%s", key), 0, "Could not fetch response");
            } else if (response.getStatusCode() == 200) {
                return ResourceStatus.OK;
            } else if (response.getStatusCode() == 404) {
                return ResourceStatus.NON_EXISTENT;
             } else if (response.getStatusCode() == 410) {
                return ResourceStatus.DELETED;
            } else {
                throw new ResourceRetrievalException(String.format("/download/%s", key), response.getStatusCode(), response.getStatus());
            }
        }, service);
    }

    @Override @NotNull public CompletableFuture<Boolean> delete(@NotNull String key,
        @NotNull final ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> {
            final HttpResponse response = httpClient.delete(String.format("/delete/%s", key))
                .onStatus(200, httpResponse -> {})
                .onRemaining(httpResponse -> {
                    throw new ResourceRetrievalException(String.format("/delete/%s", key), httpResponse.getStatusCode(), httpResponse.getStatus());
                }).execute();
            if (response == null) {
                throw new ResourceRetrievalException(String.format("/delete/%s", key), 0, "Could not fetch response");
            }
            return true;
        }, service);
    }

    @Override @NotNull public CompletableFuture<Schematic> download(@NotNull String key,
        @NotNull final ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> {
            final HttpResponse response = httpClient.get(String.format("/download/%s", key))
                .onStatus(200, httpResponse -> {})
                .onRemaining(httpResponse -> {
                    throw new ResourceRetrievalException(String.format("/download/%s", key), httpResponse.getStatusCode(), httpResponse.getStatus());
                }).execute();
            if (response == null) {
                throw new ResourceRetrievalException(String.format("/download/%s", key), 0, "Could not fetch response");
            }
            return new Schematic(key, response.getRawResponse());
        }, service);
    }


    private static final class SchematicSerializer implements EntityMapper.EntitySerializer<File> {

        private final String boundary = UUID.randomUUID().toString();

        @Override @NotNull public byte[] serialize(@NotNull final File file) {
            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream,
                     StandardCharsets.UTF_8), true)) {
                printWriter.append("--").append(this.boundary).append("\r\n");
                printWriter.append("Content-Disposition: form-data; name=\"schematic\"; filename=\"plot.schem\"\r\n");
                printWriter.append("Content-Type: application/octet-stream\r\n\r\n").flush();
                Files.copy(file.toPath(), byteArrayOutputStream);
                byteArrayOutputStream.flush();
                printWriter.append("\r\n").flush();
                printWriter.append("--").append(this.boundary).append("--\r\n").flush();
                return byteArrayOutputStream.toByteArray();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return new byte[0];
        }

        @Override public ContentType getContentType() {
            return ContentType.of(String.format("multipart/form-data; boundary=%s", this.boundary));
        }

    }

}
