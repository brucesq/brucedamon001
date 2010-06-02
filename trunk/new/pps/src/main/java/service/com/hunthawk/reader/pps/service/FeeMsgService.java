package com.hunthawk.reader.pps.service;

import java.util.Date;

import com.hunthawk.reader.domain.custom.FeeBackMessage;
import com.hunthawk.reader.domain.custom.SearchBalanceBackMessage;
import com.hunthawk.reader.domain.custom.SearchOrderBackMessage;
import com.hunthawk.reader.domain.custom.UserOrderBackMessage;


public interface FeeMsgService {
	/**
	 *	客户定购支付（ACIP02001）
	 *  发送http请求并处理返回
	 * @param service_code 请求服务编码
	 * @param svcnum  用户号码；对于管理类的请求可能没有手机号码的默认填写000000，用户查询类请求必须不为空  
	 * @param svcid
	 * @param clid SP标识(宽带公司 0001、视频公司 0002、音频公司 0003)
	 * @param recordsn 受理流水号
	 * @param resource  定购资源描述
 	 * @param pay_type 支付类型（1、按次 2、包月）
	 * @param pay_money 支付金额
	 * @param cp_name  cp 名称
	 * @param pro_channel 产品所属频道
	 * @param pro_no 产品编码
	 * @param pro_name 产品名称
	 * @param view_url 产品的查看链接地址（当用户直接在定购列表点击“查看”时的外链地址）
	 * @param cancel_url 产品的目录的退订链接地址（当用户直接在定购列表点击“退订”），用户如果是购买产品，该选项可省略
	 * @param invalidate_date 包月服务的失效时间，非包月业务可省略该选项，格式为yyyy-MM-dd hh24:mi:ss  
	 * @return  返回封装的返回信息
	 */
	public FeeBackMessage sendFeeMessage(String service_code,String svcnum,String svcid,String clid,String recordsn, String resource,Integer pay_type,Integer pay_money,String cp_name,
			String pro_channel,String pro_no,String pro_name,String view_url,String cancel_url,Date invalidate_date);
	
	/***
	 * 客户订单查询（ACIP01002）
	 *@param service_code 请求服务编码
	 * @param svcnum  用户号码；对于管理类的请求可能没有手机号码的默认填写000000，用户查询类请求必须不为空  
	 * @param svcid
	 * @param clid SP标识(宽带公司 0001、视频公司 0002、音频公司 0003)
	 * @param recordsn 受理流水号
	 * @param pro_no SP产品编码
	 * @param begin_date  ，格式为yyyy-MM-dd hh24:mi:ss  
	 * @param end_date	，格式为yyyy-MM-dd hh24:mi:ss  
	 * @return
	 */
	public SearchOrderBackMessage searchOrderMessage(String service_code,String svcnum,String svcid,String clid,String recordsn,String pro_no,String begin_date,String end_date);
	
	
	/***
	 * 客户帐号余额查询（ACIP01001）
	 *@param service_code 请求服务编码
	 * @param svcnum  用户号码；对于管理类的请求可能没有手机号码的默认填写000000，用户查询类请求必须不为空  
	 * @param svcid
	 * @param clid SP标识(宽带公司 0001、视频公司 0002、音频公司 0003)
	 * @param recordsn 受理流水号
	 * @return
	 */
	public SearchBalanceBackMessage searchBalanceMessage(String service_code,String svcnum,String svcid,String clid,String recordsn);
	
	/***
	 * 客户包月退订成功通知（ACIP02002）
	 * @param service_code 请求服务编码
	 * @param svcnum  用户号码；对于管理类的请求可能没有手机号码的默认填写000000，用户查询类请求必须不为空  
	 * @param svcid
	 * @param clid SP标识(宽带公司 0001、视频公司 0002、音频公司 0003)
	 * @param recordsn 受理流水号
	 * @param sequence_id 计费平台受理流水号
	 * @return
	 */
	public boolean unsubscribeMessage(String service_code,String svcnum,String svcid,String clid,String recordsn,String sequence_id);
	
	
	
	/***
	 * 客户订单查询（ACIP01002）
	 * @param service_code 请求服务编码
	 * @param svcnum  用户号码；对于管理类的请求可能没有手机号码的默认填写000000，用户查询类请求必须不为空  
	 * @param svcid
	 * @param clid SP标识(宽带公司 0001、视频公司 0002、音频公司 0003)
	 * @param recordsn 受理流水号
	 * @param pro_no SP产品编码
	 * @param state 0 所有订单，1 当前有效订单
	 * @param begin_date 订单产生日期区间段查询(大于等于)起始时间，格式 yyyy-MM-dd HH:mm:ss
	 * @param end_date 订单产生日期区间段查询(小于等于)结束时间，格式 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public UserOrderBackMessage userOrderMessage(String service_code,String svcnum,String clid,String recordsn,String pro_no,String state,String begin_date,String end_date);
	
	
}
