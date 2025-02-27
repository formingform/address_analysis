package org.example.address_analysis.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
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
@TableName(value = "address_group_data", autoResultMap = true)
public class AddressGroupData extends Model<AddressGroupData> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId
	private Integer id;


    private String groupName;

    /**
     * 初始化地址
     */
    private String initialAddress;
}
