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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EpirusAccount {

    private static final Path EPIRUS_CONFIG_PATH =
            Paths.get(System.getProperty("user.home"), ".epirus", ".config");

    public static String getEpirusLoginToken() throws IOException {
        return System.getenv().getOrDefault("EPIRUS_LOGIN_TOKEN", getConfigFileLoginToken());
    }

    private static String getConfigFileLoginToken() throws IOException {
        if (!EPIRUS_CONFIG_PATH.toFile().exists() || System.getenv("EPIRUS_LOGIN_TOKEN") != null) {
            return null;
        }
        String configContents = new String(Files.readAllBytes(EPIRUS_CONFIG_PATH));
        final ObjectNode node = new ObjectMapper().readValue(configContents, ObjectNode.class);
        if (node.has("loginToken")) {
            return node.get("loginToken").asText();
        }
        throw new IllegalStateException(
                "Epirus config file exists but does not contain a login token. Please log in to the Epirus platform using the CLI.");
    }
}
