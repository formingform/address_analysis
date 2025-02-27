package org.example.address_analysis.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "fmis_asset_exclude", autoResultMap = true)
public class FmisAssetExclude extends Model<FmisAssetExclude> implements Serializable  {
    @TableId(value= "id", type = IdType.AUTO)
    private Long id;
    private String address;
    private String adder;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
