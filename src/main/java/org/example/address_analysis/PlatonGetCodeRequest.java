package org.example.address_analysis;

import com.platon.protocol.Web3jService;
import com.platon.protocol.core.DefaultBlockParameter;
import com.platon.protocol.core.Request;

import java.util.Arrays;

public class PlatonGetCodeRequest extends Request<Object, PlatonGetCodeResponse> {
    private static final String GetCode = "platon_getCode";

    public PlatonGetCodeRequest(Web3jService web3jService, String address, DefaultBlockParameter defaultBlockParameter) {
        super(
                GetCode,
                Arrays.asList(address, defaultBlockParameter.getValue()),
                web3jService,
                PlatonGetCodeResponse.class);
    }

}
