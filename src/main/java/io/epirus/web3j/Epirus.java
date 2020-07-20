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

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import org.web3j.protocol.Network;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.JsonRpc2_0Web3j;

public class Epirus {

    private static OkHttpClient buildHttpClient() {
        OkHttpClient.Builder builder = EpirusHttpServiceProvider.createOkHttpClientBuilder();
        return builder.connectTimeout(90, TimeUnit.SECONDS)
                .callTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .build();
    }

    public static Web3j buildWeb3j() throws Exception {
        return new JsonRpc2_0Web3j(
                EpirusHttpServiceProvider.getEpirusHttpService(Network.MAINNET, buildHttpClient()));
    }

    public static Web3j buildWeb3j(Network network) throws Exception {
        return new JsonRpc2_0Web3j(
                EpirusHttpServiceProvider.getEpirusHttpService(network, buildHttpClient()));
    }

    public static Web3j buildWeb3j(Network network, OkHttpClient client) throws Exception {
        return new JsonRpc2_0Web3j(EpirusHttpServiceProvider.getEpirusHttpService(network, client));
    }
}
