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
package io.epirus.web3j.gas;

import java.io.IOException;
import java.math.BigInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.epirus.web3j.EpirusAccount;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.web3j.protocol.Network;
import org.web3j.tx.gas.DefaultGasProvider;

public class EpirusGasProvider extends DefaultGasProvider {
    private BigInteger gasPrice;
    private static final OkHttpClient client = new OkHttpClient();

    public EpirusGasProvider(Network network, GasPrice desiredGasPrice) throws IOException {
        this(
                network,
                String.format(
                        "https://%s.api.epirus.io/gas/price",
                        network.getNetworkName().toLowerCase()),
                desiredGasPrice);
    }

    protected EpirusGasProvider(Network network, String url, GasPrice desiredGasPrice)
            throws IOException {
        if (!network.equals(Network.RINKEBY) && !network.equals(Network.ROPSTEN)) {
            gasPrice = GAS_PRICE;
            return;
        }
        String authToken = EpirusAccount.getEpirusLoginToken();
        Request request =
                new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("Authorization", "Bearer " + authToken)
                        .build();

        Response response = client.newCall(request).execute();

        if (response.code() != 200) {
            gasPrice = GAS_PRICE;
            return;
        }

        ResponseBody body = response.body();
        if (body != null) {
            GasPriceOracleResult result =
                    new ObjectMapper().readValue(body.string(), GasPriceOracleResult.class);
            gasPrice = result.getDesiredGasPrice(desiredGasPrice);
        }
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        return gasPrice;
    }

    @Override
    public BigInteger getGasPrice() {
        return gasPrice;
    }
}
