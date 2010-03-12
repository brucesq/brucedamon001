package com.hunthawk.reader.page.system;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;

import com.hunthawk.framework.annotation.Restrict;
import com.hunthawk.framework.hibernate.HibernateExpression;
import com.hunthawk.framework.tapestry.EditPage;
import com.hunthawk.framework.tapestry.form.ObjectPropertySelectionModel;
import com.hunthawk.reader.domain.resource.ResourceAuthor;
import com.hunthawk.reader.domain.system.KeyWord;
import com.hunthawk.reader.domain.system.KeyWordType;
import com.hunthawk.reader.domain.system.UserImpl;
import com.hunthawk.reader.domain.system.Variables;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.KeyWordService;
import com.hunthawk.reader.service.system.SystemService;

@Restrict(roles = { "keyword" }, mode = Restrict.Mode.ROLE)
public abstract class EditKeyWordPage extends EditPage implements
		PageBeginRenderListener {

	@InjectObject("spring:keywordService")
	public abstract KeyWordService getKeyWordService();

	@InjectObject("spring:systemServiceTarget")
	public abstract SystemService getSystemService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract IUploadFile getUploadFile();

	public abstract String getType();

	public abstract void setType(String type);

	public abstract KeyWordType getKeyWordType();
	public abstract void setKeyWordType(KeyWordType keyWordType);
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return KeyWord.class;
	}

	public boolean isCheck() {
		if (getType() == null)
			return true;
		if (getType().equals("true"))
			return true;
		else
			return false;

	}

	
	public IPropertySelectionModel getSearchTypeList() {
		List<KeyWordType> typeList = getKeyWordService().getKeyWordTypeList
						(1, Integer.MAX_VALUE, "id", false,
						new ArrayList<HibernateExpression>());
		ObjectPropertySelectionModel model = new ObjectPropertySelectionModel(
				typeList, KeyWordType.class, "getType", "getId",
				false, null);
		return model;
	}
	
	@Override
	protected boolean persist(Object object) {
		try {
			UserImpl user = (UserImpl) getUser();
			String errormessage = "";
			if (!isCheck()) {// 批量关键字上传
				if (getUploadFile() != null) {
					List<String> list = keyWords(getUploadFile());
					if (list.size() > 0 && list != null) { // 表明上传了批量文件
						for (int i = 0; i < list.size(); i++) {
							KeyWord wk = new KeyWord();
							wk.setCreateTime(new Date());
							wk.setCreator(user.getId());
							wk.setModifier(user.getId());
							wk.setModifyTime(new Date());
							wk.setKeyWord(list.get(i));
							wk.setType(getKeyWordType());
							errormessage += getKeyWordService().addKeyWord(wk);
						}
					}
				}
			} else { // 单个上传或者是维护
				KeyWord keyWord = (KeyWord) object;
				if (isModelNew()) {
					keyWord.setType(getKeyWordType());
					keyWord.setCreateTime(new Date());
					keyWord.setCreator(user.getId());
					keyWord.setModifier(user.getId());
					keyWord.setModifyTime(new Date());
					errormessage += getKeyWordService().addKeyWord(keyWord);
				} else {
					keyWord.setModifier(user.getId());
					keyWord.setModifyTime(new Date());
					errormessage += getKeyWordService().updateKeyWord(keyWord);
				}
			}
			if (!"".equals(errormessage) && errormessage != null)
				throw new Exception(errormessage + "已经存在了！");
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
			return false;
		}
		return true;
	}

	public void pageBeginRender(PageEvent arg0) {
		if (getModel() == null) {
			setModel(new KeyWord());
		}
	}

	public List keyWords(IUploadFile file) {

		String fileName = file.getFileName().substring(
				file.getFileName().lastIndexOf("\\") + 1);
		InputStream fis = file.getStream();
		FileOutputStream fos = null;
		List<String> list = new ArrayList<String>();
		try {
			String[] fileNames = fileName.split("\\.");
			if (!"txt".equals(fileNames[1]))
				throw new Exception("文件必须是.txt文件!请重新上传");

			Variables variables = getSystemService().getVariables("media_dir");
			String tName = variables.getValue() + File.separator + "keyWord";
			File dir = new File(tName);
			if (!dir.exists())
				dir.mkdirs();
			uploadFile(fileName, dir, fis);
			// ————————读取文件信息，把关键字放到一个List中————————————
			File keyWordFile = new File(dir + "/" + fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(keyWordFile)));
			String line;

			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			// —————————————没有错误后，删除这个关键字文件———————————————————
			if (keyWordFile.isFile() && keyWordFile.exists())
				getResourceService().deleteFile(keyWordFile.toString());
		} catch (Exception e) {
			getDelegate().setFormComponent(null);
			getDelegate().record(e.getMessage(), null);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					ioe.getMessage();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					ioe.getMessage();
				}
			}
		}
		return list;
	}

	public boolean checkLast(String line) {
		if (line.indexOf("$$$") == -1)// 已经结束
			return true;
		else
			return false;
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
			System.out.println("***:" + e.toString());

		} finally {

		}

	}

}
