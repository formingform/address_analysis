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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "fmis_asset_group", autoResultMap = true)
public class FmisAssetGroup extends Model<FmisAssetGroup> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value= "id", type = IdType.AUTO)
    private Long id;

    private String name;
    private Integer type;  //地址类型：0-大户，1-交易所
    private Long recBlock;
    private Long updateBlock;

    private BigDecimal balance;
    private Long proportion; //初始地址 / 新关联地址

    private Long addressCount;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    public static void main(String[] args){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.removeIf(idx -> {
            return idx % 2 == 1;
        });

        System.out.println(list);
    }
}
