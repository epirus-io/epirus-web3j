package io.epirus.web3j.gas;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class GasPriceOracleResult {

    @JsonProperty("gasPrice")
    public BigInteger gasPrice;
    @JsonProperty("safeLow")
    public BigInteger safeLow;
    @JsonProperty("high")
    public BigInteger high;

    public GasPriceOracleResult() {
    }

    public BigInteger getDesiredGasPrice(GasPrice desiredGasPrice) {
        switch (desiredGasPrice) {
            case High:
                return high;
            case SafeLow:
                return safeLow;
            case Default:
                return gasPrice;
        }
        throw new IllegalStateException("Desired gas price shuld be SafeLow, Default or High");
    }

    @Override
    public String toString() {
        return "GasPriceOracleResult{" +
                "gasPrice=" + gasPrice +
                ", safeLow=" + safeLow +
                ", high=" + high +
                '}';
    }
}
