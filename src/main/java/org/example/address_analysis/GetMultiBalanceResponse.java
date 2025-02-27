package org.example.address_analysis;

import com.platon.protocol.core.Response;
import com.platon.utils.Numeric;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class GetMultiBalanceResponse extends Response<List<String>> {
    public List<BigInteger> getMultiBalance() {
        List<String> result  = getResult();

        if  (result != null && !result.isEmpty()){
            List<BigInteger> balanceList = new ArrayList<BigInteger>();
            for (String str : result) {
                if(str == null || str.isEmpty() || StringUtils.equals(str, "null")) {
                    balanceList.add(null);
                }else{
                    balanceList.add(Numeric.decodeQuantity(str));
                }
            }
            return balanceList;
        }else{
            return null;
        }
    }
}
