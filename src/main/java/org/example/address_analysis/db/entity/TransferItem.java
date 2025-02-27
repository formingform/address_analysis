package org.example.address_analysis.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.platon.utils.Numeric;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 转移记录(transfer_item)实体类
 *
 * @author kancy
 * @description 由 Mybatisplus Code Generator 创建
 * @since 2024-03-08 10:03:47
 */
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName("transfer_item")
public class TransferItem extends Model<TransferItem> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value= "id", type = IdType.AUTO)
    private Long id;
    /**
     * blockNumber
     */
    private Long blockNumber;
    /**
     * 区块时间
     */
    private Date blockTimestamp;
    /**
     * 交易哈希
     */
    private String txHash;
    /**
     * 转账发起者
     */
    private String txFrom;
    /**
     * 转账接收者
     */
    private String txTo;
    /**
     * 交易金额
     */
    private String value;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(update = "now()")
    private Date updateTime;


    public static void main(String[] args){
        String extraData = "0xd70186706c61746f6e88676f312e32312e30856c696e75780000000000000000d56af789e574cf700f5f78a6af8272312744bfad110549337d33d783e670a914654e4fc6d40bb0ba6301cb3eef5efeb2d86a3072227a5c487c6c04e091eb5dd300";
        byte[] extraDataBytes = Numeric.hexStringToByteArray(extraData);
        System.out.println("totalExtrData:" + extraDataBytes.length);

        byte[]realExtraDataBytes = Numeric.hexStringToByteArray(extraData.substring(0, 66));
        System.out.println("real extraData:" + extraData.substring(0, 66) + " byteSize: " + realExtraDataBytes.length);
        String signature = extraData.substring(66, extraData.length());

        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        System.out.println("signature:" + signature + " byteSize:" + signatureBytes.length);


    }
}
