/**
 * 
 */
package com.hunthawk.reader.page.inter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.reader.domain.inter.Remind;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.service.inter.RemindService;
import com.hunthawk.reader.service.system.SystemService;

/**
 * @author BruceSun
 * 
 */
@Restrict(roles = { "remind" }, mode = Restrict.Mode.ROLE)
public abstract class EditRemindPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:remindService")
	public abstract RemindService getRemindService();

	@InjectObject("spring:systemService")
	public abstract SystemService getSystemService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return Remind.class;
	}

	public void setAllFeeUser(boolean isFee){
		Remind remind = (Remind) getModel();
		if(isFee){
			remind.setAllFeeuser(1);
		}else{
			remind.setAllFeeuser(0);
		}
	}

	public  boolean getAllFeeUser(){
		Remind remind = (Remind) getModel();
		if(remind.getAllFeeuser() == null || remind.getAllFeeuser() == 0){
			return false;
		}else{
			return true;
		}
	}

	public  boolean getAllReservation(){
		Remind remind = (Remind) getModel();
		if(remind.getAllReservation() == null || remind.getAllReservation() == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public void setAllReservation(boolean isRes){
		Remind remind = (Remind) getModel();
		if(isRes){
			remind.setAllReservation(1);
		}else{
			remind.setAllReservation(0);
		}
	}

	public abstract IUploadFile getUploadFile();
	
	public abstract String getWarMessage();

    public abstract void setWarMessage(String message);

	public boolean isRightFormat(String fileName) {
		String patt = "\\.txt$";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(fileName);

		if (m.find()) {
			return true;
		} else {
			return false;
		}

	}
	
	public void onAjax(IRequestCycle cycle) {
		Object[] params = cycle.getListenerParameters();
		
		String contentId = (String)params[0];
		
		int allFeeCount = getRemindService().getAllFeeUserCount();
		
		int allReservation = getRemindService().getAllReservationCount();
		
		StringBuilder sb = new StringBuilder();
		sb.append("共");
		sb.append(allFeeCount);
		sb.append("位订购用户,");
		sb.append("共");
		sb.append(allReservation);
		sb.append("位预订用户");
		if(StringUtils.isNotEmpty(contentId)){
			sb.append(",共");
			sb.append(getRemindService().getReservationCount(contentId));
			
			sb.append("位预订");
			sb.append(contentId);
			sb.append("资源");
		}
		String[] backResults = new String[1];
		backResults[0] = sb.toString();
		
		cycle.setListenerParameters(backResults);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aspire.pams.framework.tapestry.EditPage#persist(java.lang.Object)
	 */
	@Override
	protected boolean persist(Object object) {

		List<String> fileMobiles = new ArrayList<String>();
		if (getUploadFile() != null) {
			String fileName = getUploadFile().getFileName().substring(
					getUploadFile().getFileName().lastIndexOf("\\") + 1);
			boolean isRightFormat = isRightFormat(getUploadFile().getFileName());
			if (!isRightFormat) {

				ValidationDelegate delegate = getDelegate();
				delegate.setFormComponent(null);
				delegate.record("文件格式不正确[目前只支持txt文件]，请重新选择", null);
				return false;
			}
			InputStream fis = getUploadFile().getStream();
			FileOutputStream fos = null;

			try {
				Variables var = getSystemService().getVariables("upload_dir");
				File dir = new File(var.getValue());

				uploadFile(fileName, dir, fis);
				File entryFile = new File(dir, fileName);
				List<String> lines = FileUtils.readLines(entryFile);
				for (String mobile : lines) {
					mobile = mobile.trim();
					if (mobile.length() == 11) {
						fileMobiles.add(mobile);
					}
				}

			} catch (Exception ioe) {
				ValidationDelegate delegate = getDelegate();
				delegate.setFormComponent(null);
				delegate.record("号码文件上传失败", null);
				return false;
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException ioe) {
					}
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ioe) {
					}
				}
			}

		}
		
		
		try {
			Remind board = (Remind) object;
			if (isModelNew()) {
				board.setCreateTime(new Date());
				board.setCreator(getUser().getId());
				board.setModifyTime(new Date());
				board.setModifier(getUser().getId());
				getRemindService().addRemind(board, fileMobiles);
			} else {
				board.setModifyTime(new Date());
				board.setModifier(getUser().getId());
				getRemindService().updateRemind(board, 0,
						fileMobiles);
			}
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent event) {
		if (getModel() == null) {
			setModel(new Remind());
		}

	}

	public void uploadFile(String fileName, File dir, InputStream is) {
		BufferedOutputStream dest = null;

		byte data[] = new byte[4916];

		try {

			File entryFile = new File(dir, fileName);
			int count;

			FileOutputStream fos = new FileOutputStream(entryFile);
			dest = new BufferedOutputStream(fos, 4916);
			while ((count = is.read(data, 0, 4916)) != -1) {
				dest.write(data, 0, count);

			}

			dest.flush();
			dest.close();
			fos.close();
			is.close();
		} catch (Exception e) {
			logger.error("实体资源文件上传异常:", e);
		} finally {

		}
	}

}
