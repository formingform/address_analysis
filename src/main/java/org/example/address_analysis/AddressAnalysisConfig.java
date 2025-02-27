package org.example.address_analysis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;


@Component
@Data
@EqualsAndHashCode()
@ConfigurationProperties(prefix="analysis")
public class AddressAnalysisConfig {
    private long startId = 0;
    private Integer limit;
    private Integer fetchBalanceBatchSize;
    private String web3j;
    private Long recBlock;
    private Date recBlockDay;
}
