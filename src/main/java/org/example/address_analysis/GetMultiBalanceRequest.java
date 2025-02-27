package org.example.address_analysis;

import com.platon.protocol.Web3jService;
import com.platon.protocol.core.DefaultBlockParameter;
import com.platon.protocol.core.Request;

import java.util.Arrays;
import java.util.List;

public class GetMultiBalanceRequest extends Request<Object, GetMultiBalanceResponse> {

    private static final String GetMultiBalance = "platon_getMultiBalance";
    public GetMultiBalanceRequest(Web3jService web3jService, List<String> addressList, DefaultBlockParameter defaultBlockParameter) {
        super(
                GetMultiBalance,
                Arrays.asList(addressList, defaultBlockParameter.getValue()),
                web3jService,
                GetMultiBalanceResponse.class);
    }
}
