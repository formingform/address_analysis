package org.example.address_analysis;

import com.platon.protocol.core.Response;
import org.apache.commons.lang3.StringUtils;

public class PlatonGetCodeResponse extends Response<String> {
    public boolean isContract() {
        String result  = getResult();
        return !StringUtils.equalsIgnoreCase(result, "0x");
    }
}
