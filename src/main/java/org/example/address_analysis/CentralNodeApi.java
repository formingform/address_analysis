package org.example.address_analysis;

import com.platon.contracts.ppos.BaseContract;
import com.platon.contracts.ppos.abi.Function;
import com.platon.contracts.ppos.dto.CallResponse;
import com.platon.contracts.ppos.dto.common.ErrorCode;
import com.platon.contracts.ppos.utils.EncoderUtils;
import com.platon.protocol.Web3j;
import com.platon.protocol.Web3jService;
import com.platon.protocol.core.DefaultBlockParameter;
import com.platon.protocol.core.RemoteCall;
import com.platon.protocol.core.methods.request.Transaction;
import com.platon.protocol.core.methods.response.PlatonCall;
import com.platon.protocol.http.HttpService;
import com.platon.protocol.websocket.WebSocketService;
import com.platon.tx.exceptions.ContractCallException;
import com.platon.utils.JSONUtil;
import com.platon.utils.Numeric;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.address_analysis.PlatonGetCodeRequest;
import org.example.address_analysis.PlatonGetCodeResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @Auther: Chendongming
 * @Date: 2019/8/31 11:00
 * @Description:
 */
@Slf4j
@Component
public class CentralNodeApi {

    @Resource
    private AddressAnalysisConfig addressAnalysisConfig;

    public static final int GET_RESTRICTING_BALANCE_FUNC_TYPE = 4102;
    private static final String BLANK_RES = "结果为空!";
    public final static String BATCH_ADDRESS_SPLITTER = ";";

    /**
     * rpc调用接口
     * @param web3j
     * @param function
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
	private CallResponse<String> rpc(Web3j web3j, Function function, String from, String to, BigInteger blockNumber) {
        CallResponse<String> br;
        try {
            br = new RemoteCall<>(() -> {
                PlatonCall ethCall = web3j.platonCall(
                        Transaction.createEthCallTransaction(from, to, EncoderUtils.functionEncoder(function)),
                        DefaultBlockParameter.valueOf(blockNumber))
                        .send();

                if(ethCall.hasError()) {
                    throw new RuntimeException(ethCall.getError().getMessage());
                }
                String value = ethCall.getValue();
                if("0x".equals(value)){
                    // 证明没数据,返回空响应
                    CallResponse<String> data = new CallResponse<>();
                    data.setData(null);
                    data.setErrMsg(null);
                    data.setCode(ErrorCode.SUCCESS);
                    return data;
                }
                String decodedValue = new String(Numeric.hexStringToByteArray(value));
                BaseContract.CallRet callRet = JSONUtil.parseObject(decodedValue, BaseContract.CallRet.class);
                if (callRet == null) {
                    throw new ContractCallException("Unable to convert response: " + decodedValue);
                }
                CallResponse<String> callResponse = new CallResponse<>();
                if (callRet.isStatusOk()) {
                    callResponse.setCode(callRet.getCode());
                    callResponse.setData(JSONUtil.toJSONString(callRet.getRet()));
                } else {
                    callResponse.setCode(callRet.getCode());
                    callResponse.setErrMsg(callRet.getRet().toString());
                }
                return callResponse;
            }).send();
        } catch (Exception e) {
        	log.error("get rpc error", e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return br;
    }

    private Web3jService web3jService;

    public void resetWeb3jService() {
        this.web3jService = null;
    }
    public Web3jService getWeb3jService() {
        if(web3jService!=null){
            return web3jService;
        }


        String web3jUrl = addressAnalysisConfig.getWeb3j();
        log.debug("config.web3j:{}, is ws:{}", web3jUrl, StringUtils.startsWithIgnoreCase(web3jUrl, "ws"));
        if (StringUtils.isNotEmpty(web3jUrl) && StringUtils.startsWithIgnoreCase(web3jUrl, "ws")) {
            WebSocketService wss = new WebSocketService(web3jUrl, true);
            try {
                wss.connect();
                web3jService = wss;
                log.debug("web3j initialized: {}", web3jService);
                return web3jService;
            } catch (Exception e) {
                log.error("Websocket:{}无法连通:", web3jUrl, e);
                log.error("Websocket无法连通", e);
                return null;
            }
        }  if (StringUtils.isNotEmpty(web3jUrl) && StringUtils.startsWithIgnoreCase(web3jUrl, "http")) {
            web3jService = new HttpService(web3jUrl);
            return web3jService;
        } else {
            log.warn("没有可用的rpc地址[{}]", web3jUrl);
            return null;
        }
    }

    /**
     * 从中心化节点，按块高拿地址的余额（中心化节点有platon_getBalance接口，但是这里复用了platon_getMultiBalance）
     * @param address
     * @param blockNumber
     * @return
     * @throws Exception
     */
    public BigDecimal getBalance(String address, Long blockNumber) throws Exception {
        List<String> addressSet = Arrays.asList(address);
        List<BigInteger> balanceList =  this.getMultiBalance(addressSet, blockNumber);
        return new BigDecimal(balanceList.get(0));
    }

    /**
     * 从中心化节点，按块高拿多个地址的余额（中心化节点有platon_getMultiBalance接口，支持多个地址）
     * @param addressSet
     * @param blockNumber
     * @return
     * @throws Exception
     */
    public List<BigInteger> getMultiBalance( List<String> addressSet, Long blockNumber) {
        //log.debug("异步从中心节点获取多地址的余额，区块:{}，地址：{}", blockNumber, JSONUtil.toJSONString(addressSet));
        for(;;) {
            Web3jService web3jService = getWeb3jService();
            try {
                GetMultiBalanceRequest request = new GetMultiBalanceRequest(web3jService, addressSet, DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)));
                CompletableFuture<GetMultiBalanceResponse> responseFuture = request.sendAsync();
                GetMultiBalanceResponse response = responseFuture.get();
                List<BigInteger> balanceList = response.getMultiBalance();
                //log.debug("异步从中心节点获取多地址的余额结果，区块:{}，地址：{}", blockNumber, JSONUtil.toJSONString(balanceList));
                return balanceList;
            }catch (Throwable e){
                log.error("查询地址余额出错", e);
                try {
                    //休息1秒钟
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                //重置web3jService
                resetWeb3jService();
            }
        }
    }


    private static Map<String, Boolean> contractMap  = new HashMap<>();
    public boolean isContract(String address, Long blockNumber) throws Exception {
        if(!contractMap.containsKey(address)) {
            Web3jService web3jService = getWeb3jService();
            //log.debug("查询地址是否是合约，区块:{}，地址：{}， web3j:{}", blockNumber, address, web3jService);

            PlatonGetCodeRequest request = new PlatonGetCodeRequest(web3jService, address, DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)));
            CompletableFuture<PlatonGetCodeResponse> responseFuture = request.sendAsync();
            PlatonGetCodeResponse response = responseFuture.get();
            Boolean isContract = response.isContract();
            log.debug("从中心节点查询地址是否是合约，区块:{}，地址：{}， 是合约：{}", blockNumber, address, isContract);
            contractMap.put(address, isContract);
        }
        return contractMap.get(address);
    }

}
