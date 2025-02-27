package org.example.address_analysis;

import org.example.address_analysis.db.entity.TransferItem;

import java.util.Map;
import java.util.Set;

public interface AddressAnalysisService {
    void initExchange();
    void initMajor();

    void processMajor(TransferItem transferItem);

    void processExchange(TransferItem transferItem);
    void saveExchange();
    void updateAddressGroup();
    void updateAddressGroup_old();
    Map<Integer, Set<String>> listExchangeCollectAddressMap();

    void fetchBalance();


}
