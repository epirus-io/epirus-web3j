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

import org.web3j.protocol.Network;
import org.web3j.protocol.http.HttpService;

public class EpirusHttpServiceProvider {

    public static HttpService getEpirusHttpService(final Network network) throws Exception {
        String loginToken = EpirusAccount.getEpirusLoginToken();
        if (loginToken == null) {
            throw new IllegalStateException(
                    "Could not read your Epirus login token. In order to use Web3j without a specified endpoint, you must use the Epirus CLI and log in to the Epirus Platform.");
        }
        return createHttpServiceWithToken(network, loginToken);
    }

    private static HttpService createHttpServiceWithToken(Network network, String token) {
        String epirusBaseUrl =
                System.getenv().getOrDefault("EPIRUS_APP_URL", "https://portal.epirus.io");
        String httpEndpoint =
                String.format("%s/api/rpc/%s/%s/", epirusBaseUrl, network.getNetworkName(), token);
        return new HttpService(httpEndpoint);
    }
}
