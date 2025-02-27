package org.example.address_analysis.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "fmis_asset_address", autoResultMap = true)
public class FmisAssetAddress  extends Model<FmisAssetAddress> implements Serializable{
    /**
     *
     */
    @TableId(value= "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属资产对象ID
     */
    private Integer groupId;
    /**
     * 资产地址
     */
    private String address;
    /**
     * 地址类型：0-初始地址，1-关联地址
     */
    private Integer type;
    /**
     * 标签
     */
    private String tag;
    /**
     * 地址余额
     */
    private BigDecimal balance;
    /**
     * 当前余额对应的区块
     */
    private Long balanceBlock;
    private Date balanceBlockDay;
    /**
     * 此地址被识别出来时的区块号
     */
    private Long recBlock;
    /**
     * 此地址被识别出来时的区块时间
     */
    private Date recBlockTime;

    /**
     * 流通占比
     */
    private BigDecimal proportion;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
