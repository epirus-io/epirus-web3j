/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.epirus.web3j;

import java.util.Arrays;
import java.util.List;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.web3j.protocol.Network;
import org.web3j.protocol.http.HttpService;

import static okhttp3.ConnectionSpec.CLEARTEXT;

public class EpirusHttpServiceProvider {

    private static final CipherSuite[] INFURA_CIPHER_SUITES =
            new CipherSuite[] {
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256,

                // Note that the following cipher suites are all on HTTP/2's bad cipher suites list.
                // We'll
                // continue to include them until better suites are commonly available. For example,
                // none
                // of the better cipher suites listed above shipped with Android 4.4 or Java 7.
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384,
                CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA,
                CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA,

                // Additional INFURA CipherSuites
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
                CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256,
                CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256
            };

    private static final ConnectionSpec INFURA_CIPHER_SUITE_SPEC =
            new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .cipherSuites(INFURA_CIPHER_SUITES)
                    .build();

    private static final Logger log = LoggerFactory.getLogger(HttpService.class);

    public static OkHttpClient.Builder createOkHttpClientBuilder() {
        final OkHttpClient.Builder builder =
                new OkHttpClient.Builder().connectionSpecs(CONNECTION_SPEC_LIST);
        configureLogging(builder);
        return builder;
    }

    private static void configureLogging(OkHttpClient.Builder builder) {
        if (log.isDebugEnabled()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(log::debug);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
    }

    /** The list of {@link ConnectionSpec} instances used by the connection. */
    private static final List<ConnectionSpec> CONNECTION_SPEC_LIST =
            Arrays.asList(INFURA_CIPHER_SUITE_SPEC, CLEARTEXT);

    public static HttpService getEpirusHttpService(final Network network, OkHttpClient client)
            throws Exception {
        String loginToken = EpirusAccount.getEpirusLoginToken();
        if (loginToken == null) {
            throw new IllegalStateException(
                    "Could not read your Epirus login token. In order to use Web3j without a specified endpoint, you must use the Epirus CLI and log in to the Epirus Platform.");
        }
        return createHttpServiceWithToken(network, loginToken, client);
    }

    private static HttpService createHttpServiceWithToken(
            Network network, String token, OkHttpClient client) {
        String epirusBaseUrl =
                System.getenv().getOrDefault("EPIRUS_APP_URL", "https://portal.epirus.io");
        String httpEndpoint =
                String.format("%s/api/rpc/%s/%s/", epirusBaseUrl, network.getNetworkName(), token);

        return new HttpService(httpEndpoint, client);
    }
}
