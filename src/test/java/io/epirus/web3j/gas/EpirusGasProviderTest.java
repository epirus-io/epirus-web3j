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

import java.math.BigInteger;

import io.epirus.web3j.HttpMockedTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.web3j.protocol.Network;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class EpirusGasProviderTest extends HttpMockedTest {

    @Test
    public void testEpirusPlatformWorksAgainstMock() throws Exception {
        stubFor(
                get(urlPathMatching("/gas/price"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                "{\n"
                                                        + "  \"safeLow\": 3,\n"
                                                        + "  \"gasPrice\": 2,\n"
                                                        + "  \"high\": 1\n"
                                                        + "}")));

        withEnvironmentVariable("EPIRUS_LOGIN_TOKEN", "token")
                .execute(
                        () -> {
                            EpirusGasProvider provider =
                                    new EpirusGasProvider(
                                            Network.RINKEBY,
                                            String.format("%s/gas/price", wireMockServer.baseUrl()),
                                            GasPrice.High);
                            assertEquals(provider.getGasPrice(), BigInteger.ONE);
                        });
    }

    @Test
    @Disabled("requires the CLI to be logged in to pass")
    public void testEpirusPlatformGasProviderWorks() throws Exception {
        EpirusGasProvider provider = new EpirusGasProvider(Network.RINKEBY, GasPrice.High);
        assertEquals(provider.getGasPrice().compareTo(BigInteger.ONE), 1);
        assertNotEquals(provider.getGasPrice(), BigInteger.valueOf(4_100_000_000L));
    }
}
