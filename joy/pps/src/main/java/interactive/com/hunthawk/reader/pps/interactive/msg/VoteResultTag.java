package com.hunthawk.reader.pps.interactive.msg;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hunthawk.reader.domain.bussiness.TagTemplate;
import com.hunthawk.reader.domain.inter.VoteSubItem;
import com.hunthawk.reader.pps.DBVmInstance;
import com.hunthawk.reader.pps.ParameterConstants;
import com.hunthawk.reader.pps.RequestUtil;
import com.hunthawk.reader.pps.TagLogger;
import com.hunthawk.reader.pps.URLUtil;
import com.hunthawk.reader.pps.VoteResultLog;
import com.hunthawk.reader.pps.service.BussinessService;
import com.hunthawk.reader.pps.service.InteractiveService;
import com.hunthawk.tag.BaseTag;
import com.hunthawk.tag.TagUtil;

/**
 * 投票结果标签 标签名称：view_vote successTitle:成功显示文字
 * 
 * @author liuxh
 * 
 */
public class VoteResultTag extends BaseTag {

	private InteractiveService interactiveService;

	@Override
	public Map parseTag(HttpServletRequest request, String tagName) {
		// TODO Auto-generated method stub

		// 是否允许投票显示
		Integer isVote = Integer.parseInt(getParameter("isVote", "1"));
		Integer voteMaxNumber = Integer.parseInt(getParameter("voteMaxNumber",
				"10"));  //默认为10票
		// 得到参数值
		String successTitle = getParameter("successTitle",
				"投票成功,系统在3秒后将自动返回...");// 提示文字
		String strVoteType = request
				.getParameter(ParameterConstants.VOTE_VOTE_TYPE);
		Integer timer = getIntParameter("timer",1);
		String itemId = request.getParameter(ParameterConstants.VOTE_ITEM_ID);
		String cid = request.getParameter(ParameterConstants.VOTE_CONTENT_ID);
		if (strVoteType == null || StringUtils.isEmpty(strVoteType)) {
			TagLogger.debug(tagName, "投票类型为空", request.getQueryString(), null);
			return new HashMap();
		}
		if (itemId == null || StringUtils.isEmpty(itemId)) {
			TagLogger.debug(tagName, "投票项ID为空", request.getQueryString(), null);
			return new HashMap();
		}
		if (cid == null || StringUtils.isEmpty(cid)) {
			TagLogger.debug(tagName, "内容、栏目、产品或用户定制ID为空", request
					.getQueryString(), null);
			return new HashMap();
		}
		int voteType = Integer.parseInt(strVoteType);
		Long vote = 0L;
		int flag=1;//默认操作成功
		try {
			if (voteType == VoteSubItem.TYPE_PRODUCT) {// 产品
				vote = getInteractiveService(request).vote(voteType,
						Integer.parseInt(itemId), null, null, cid, null,
						RequestUtil.getMobile(), isVote, voteMaxNumber);
				// 记录产品投票
				VoteResultLog.logVote(VoteSubItem.TYPE_PRODUCT, cid,
						RequestUtil.getMobile(), Integer.parseInt(itemId));
			} else if (voteType == VoteSubItem.TYPE_COLUMN) {// 栏目
				vote = getInteractiveService(request).vote(voteType,
						Integer.parseInt(itemId), null, Integer.parseInt(cid),
						null, null, RequestUtil.getMobile(), isVote,
						voteMaxNumber);
				// 记录栏目投票
				VoteResultLog.logVote(VoteSubItem.TYPE_COLUMN, cid, RequestUtil
						.getMobile(), Integer.parseInt(itemId));
			} else if (voteType == VoteSubItem.TYPE_CONTENT) {// 内容
				vote = getInteractiveService(request).vote(voteType,
						Integer.parseInt(itemId), cid, null, null, null,
						RequestUtil.getMobile(), isVote, voteMaxNumber);
				// 记录内容投票
				VoteResultLog.logVote(VoteSubItem.TYPE_CONTENT, cid, RequestUtil
						.getMobile(), Integer.parseInt(itemId));
			} else if (voteType == VoteSubItem.TYPE_CUSTOM) {// 定制

				vote = getInteractiveService(request).vote(voteType,
						Integer.parseInt(itemId), null, null, null, cid,
						RequestUtil.getMobile(), isVote, voteMaxNumber);

				// 记录定制投票
				VoteResultLog.logVote(VoteSubItem.TYPE_CUSTOM, cid, RequestUtil
						.getMobile(), Integer.parseInt(itemId));
			}
		} catch (Exception ex) {
			successTitle = "投票失败：" + ex.getMessage();
			flag=0;
		}

		StringBuilder backUrl = new StringBuilder();
		backUrl.append(request.getContextPath());
		backUrl.append(ParameterConstants.PORTAL_PATH);
		backUrl.append("?");
		backUrl.append(URLUtil.removeParameter(request.getQueryString(),
				ParameterConstants.TEMPLATE_ID,
				ParameterConstants.VOTE_VOTE_TYPE,
				ParameterConstants.VOTE_ITEM_ID,
				ParameterConstants.VOTE_CONTENT_ID));
		Map velocityMap = new HashMap();
		com.hunthawk.tag.process.Refresh.pageRefresh(timer, backUrl.toString());// 3秒钟
		// 后返回
		velocityMap.put("successTitle", successTitle);
		/**
		 * modify by liuxh 09-11-12
		 * 添加操作标识  0.失败 1.成功
		 */
		velocityMap.put("flag", flag);
		/**
		 * end
		 */

		int tagTemplateId = this.getIntParameter("tmd", 0);
		TagTemplate tagTem = null;
		if (tagTemplateId > 0) {
			tagTem = getBussinessService(request).getTagTemplate(tagTemplateId);
		}

		Map resultMap = new HashMap();
		resultMap.put(TagUtil.makeTag(tagName), DBVmInstance.getInstance()
				.parseVM(velocityMap, this, tagTem));

		/*
		 * String content = VmInstance.getInstance().parseVM(velocityMap, this);
		 * Map resultMap = new HashMap();
		 * resultMap.put(TagUtil.makeTag(tagName), content);
		 */

		return resultMap;

	}

	private InteractiveService getInteractiveService(HttpServletRequest request) {
		if (interactiveService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			interactiveService = (InteractiveService) wac
					.getBean("interactiveService");
		}
		return interactiveService;
	}

	private BussinessService bussinessService;

	private BussinessService getBussinessService(HttpServletRequest request) {
		if (bussinessService == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			bussinessService = (BussinessService) wac
					.getBean("bussinessService");
		}
		return bussinessService;
	}

}
