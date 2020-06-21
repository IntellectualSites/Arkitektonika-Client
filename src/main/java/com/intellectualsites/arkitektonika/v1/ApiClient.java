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
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public final class ApiClient implements com.intellectualsites.arkitektonika.ApiClient {

    public ApiClient(@NotNull final String url) {
        Unirest.config().defaultBaseUrl(url);
    }

    @Override public ApiVersion getApiVersion() {
        return ApiVersion.V1_0_0;
    }

    @Override public CompletableFuture<Boolean> checkCompatibility(@NotNull final ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> {
            final HttpResponse<JsonNode> response = Unirest.get("/").asJson();
            if (response.isSuccess()) {
                final JsonNode node = response.getBody();
                return node.getObject().has("version") &&
                       node.getObject().getString("version").startsWith("1.");
            }
            return false;
        }, service);
    }

}
