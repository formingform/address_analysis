package org.example.address_analysis.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.address_analysis.db.entity.FmisAssetAddressDailyBalance;

import java.util.List;

public interface FmisAssetAddressDailyBalanceMapper extends BaseMapper<FmisAssetAddressDailyBalance> {
    void saveOrUpdateOnDuplicatedBatch(List<FmisAssetAddressDailyBalance> dailyBalanceAssetList);
}
