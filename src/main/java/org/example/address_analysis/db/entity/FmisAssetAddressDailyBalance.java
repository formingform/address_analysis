package org.example.address_analysis.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "fmis_asset_address_daily_balance", autoResultMap = true)
public class FmisAssetAddressDailyBalance extends Model<FmisAssetAddressDailyBalance> implements Serializable  {
    private String address;
    private Date blockDay;
    private BigDecimal balance;
}
