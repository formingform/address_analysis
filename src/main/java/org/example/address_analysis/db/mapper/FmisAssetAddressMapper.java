package org.example.address_analysis.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.address_analysis.db.entity.FmisAssetAddress;

import java.util.List;

public interface FmisAssetAddressMapper  extends BaseMapper<FmisAssetAddress> {
    void updateBalanceBatch(List<FmisAssetAddress> updateAssetAddressBalanceList);
}
