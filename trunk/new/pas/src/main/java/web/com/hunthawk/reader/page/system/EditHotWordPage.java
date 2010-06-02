package com.hunthawk.reader.page.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.statistics.StatData;
import com.hunthawk.reader.service.system.HotWordService;


public abstract class EditHotWordPage extends EditPage  implements PageBeginRenderListener{

	public abstract Integer getCreateTime();
	public abstract void setCreateTime(Integer createTime);
	
	@InjectObject("spring:hotwordService")
	public abstract HotWordService getHotWordService();
	
	@Override
	public Class getModelClass() {
		return StatData.class;
	}
	
	@Override
	protected boolean persist(Object object) {
		try{
			StatData sd = (StatData)object;
			/*Integer dataType = 0;
			if(getCreateTime()!=null)
				dataType = getCreateTime();
			sd.setCreateTime(this.getDate(dataType));*/
			sd.setCreateTime(new Date());
			getHotWordService().addHoteWord(sd);
		}catch(Exception e)
		{
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event)
	{
		if(getModel() == null)
		{
			setModel(new StatData());
		}
		
	}

	public IPropertySelectionModel getTypeList() {
		Map<String, Integer> TYPE = new OrderedMap<String, Integer>();
		TYPE.put("书名搜索", 11);
		TYPE.put("作者搜索", 12);
		TYPE.put("关键字搜索", 13);
		
		return new MapPropertySelectModel(TYPE,false,"");
	}
	
	public IPropertySelectionModel getCreateTimeList(){
		Map<String, Integer> TIME = new OrderedMap<String, Integer>();
		TIME.put("总量排行", 0);
		TIME.put("日排行", 1);
		TIME.put("周排行", 2);
		TIME.put("月排行", 3);

		return new MapPropertySelectModel(TIME,false,"");
	}
	
	public Date getDate(int dateType){
		Calendar today = Calendar.getInstance();
		Date[] returnDate = new Date[2];
		int month = today.get(Calendar.MONTH);  // 得到当前月
		int currentYear = today.get(Calendar.YEAR);        // 得到当前年
		String[] formate = new String[]{"yyyy-MM-dd hh:mm:ss"};
		
		SimpleDateFormat dateFormate = new SimpleDateFormat(
		"yyyy-MM-dd  hh:mm:ss");
		
		if (dateType == 1){  // 日排行   
			returnDate[1] = DateUtils.truncate(today.getTime(), Calendar.DATE);
			returnDate[0] = DateUtils.addMilliseconds(returnDate[1], (int) ((-1) * (DateUtils.MILLIS_PER_DAY)));
			//Date startDate = DateUtils.addDays(returnDate[0], 1);
			//System.out.println("returnDate[1]--1--"+dateFormate.format(returnDate[1]));
			//System.out.println("returnDate[0]--1--"+dateFormate.format(returnDate[0]));
			System.out.println("---昨日排行--"+dateFormate.format(returnDate[0]));
			return returnDate[0];
		} else if (dateType == 2){  // 周排行		
			Date endDate = DateUtils.truncate(DateUtils.addDays(today.getTime(), (-1) * (today.get(Calendar.DAY_OF_WEEK) - 2)), Calendar.DATE);
			Date startDate = DateUtils.addDays(endDate, -6);
/*			
			returnDate[0] = startDate;
			returnDate[1] = endDate;
			System.out.println("returnDate[1]--2--"+dateFormate.format(returnDate[1]));
			System.out.println("returnDate[0]--2--"+dateFormate.format(returnDate[0]));*/
			System.out.println("---上周排行--"+dateFormate.format(startDate));
			return startDate;
		} else if (dateType == 3){  // 月排行
			try{
				if (month == 0){  // 当前月 1 月
					returnDate[0] = DateUtils.parseDate((currentYear - 1) + "-12-1 00:00:00", formate);
					//returnDate[1] = DateUtils.parseDate((currentYear - 1) + "-12-31 24:00:00", formate);	
					Date startDate = DateUtils.addDays(returnDate[0], 1);
					System.out.println("---上月排行--"+dateFormate.format(startDate));
					return startDate;
				} else {
					returnDate[0] = DateUtils.parseDate(currentYear + "-" + month + "-1 00:00:00", formate);
					//returnDate[1] = DateUtils.parseDate(currentYear + "-" + (month + 1) + "-1 00:00:00", formate);
					Date startDate = DateUtils.addDays(returnDate[0], 1);
					System.out.println("---上月排行--"+dateFormate.format(startDate));
					return startDate;
				}
			} catch (ParseException e){
				e.printStackTrace();
				return new Date();
			}
		}else{
			return new Date();
		}
	}
	
}
