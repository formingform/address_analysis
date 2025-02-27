package org.example.address_analysis.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.example.address_analysis.db.typehandler.InitAddressTypeHandler;
import org.example.address_analysis.db.typehandler.RelatedAddressTypeHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 地址组表(address_group)实体类
 *
 * @author kancy
 * @since 2024-03-07 16:58:28
 * @description 由 Mybatisplus Code Generator 创建
 */
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "address_group", autoResultMap = true)
public class AddressGroup extends Model<AddressGroup> implements Serializable {
    private static final long serialVersionUID = 1L;

    //#从10000开始，1-9999保留兼容已有的fmis_asset_group的初始化数据
    // 不需要：静态分析，不会增加新的大户
    //public static AtomicInteger atomicInteger = new AtomicInteger(9);

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
	private Integer id;

    private String groupName;

    /**
     * 初始化地址
     */
    @TableField(typeHandler = InitAddressTypeHandler.class)
    private List<String> initialAddresses = new ArrayList<>();;
    /**
     * 标签多个逗号分隔
     */
    @TableField(typeHandler = RelatedAddressTypeHandler.class)
    private List<RelatedAddress> relatedAddresses;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(update = "now()")
	private Date updateTime;

    public void addInitAddress(String address) {
        initialAddresses.add(address);
    }

    public void addRelatedAddress(String address, Long blockNumber, Long blockTime) {
        if(relatedAddresses==null){
            relatedAddresses = new ArrayList<>();
        }
        relatedAddresses.add(new RelatedAddress(address, blockNumber, blockTime));
    }

    public List<RelatedAddress> getRelatedAddresses() {
        if(relatedAddresses==null){
            relatedAddresses = new ArrayList<>();
        }
        return relatedAddresses;
    }
}
