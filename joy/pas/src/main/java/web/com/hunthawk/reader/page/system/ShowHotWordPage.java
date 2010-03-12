package com.hunthawk.reader.page.system;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.hunthawk.framework.tapestry.SearchPage;
import com.hunthawk.framework.tapestry.form.MapPropertySelectModel;
import com.hunthawk.framework.util.OrderedMap;
import com.hunthawk.reader.domain.resource.ResourceType;
import com.hunthawk.reader.domain.statistics.StatData;
import com.hunthawk.reader.service.resource.ResourceService;
import com.hunthawk.reader.service.system.HotWordService;

public abstract class ShowHotWordPage extends SearchPage {

	@InjectComponent("table")
	public abstract TableView getTableView();

	@InjectObject("spring:hotwordService")
	public abstract HotWordService getHotWordService();

	@InjectObject("spring:resourceService")
	public abstract ResourceService getResourceService();

	public abstract Integer getType();

	public abstract void setType(Integer type);

	public abstract String getContent();

	public abstract void setContent(String content);

	public abstract Integer getCreateTime();

	public abstract void setCreateTime(Integer date);

	public abstract void setResourceType(Integer type);

	@Persist("session")
	public abstract Integer getResourceType();

	public IPropertySelectionModel getResourceTypeList() {
		Map<String, Integer> types = new OrderedMap<String, Integer>();
		types.put("ͼ��", ResourceType.TYPE_BOOK);
		types.put("��ֽ", ResourceType.TYPE_NEWSPAPERS);
		types.put("��־", ResourceType.TYPE_MAGAZINE);
		types.put("����", ResourceType.TYPE_COMICS);

		return new MapPropertySelectModel(types, false, "");
	}

	public IPropertySelectionModel getTypeList() {

		Map<String, Integer> TYPE = new OrderedMap<String, Integer>();
		if (getResourceType() != null && getResourceType() != 0) {
			if (getResourceType() == ResourceType.TYPE_BOOK) {
				TYPE.put("��������", 11);
				TYPE.put("��������", 12);
				TYPE.put("�ؼ�������", 13);
				TYPE.put("����������", 15);
			} else if (getResourceType() == ResourceType.TYPE_NEWSPAPERS) {
				TYPE.put("��������", 21);
				TYPE.put("�ؼ�������", 23);
				TYPE.put("����������", 25);
			} else if (getResourceType() == ResourceType.TYPE_MAGAZINE) {
				TYPE.put("��������", 31);
				TYPE.put("�ؼ�������", 33);
				TYPE.put("����������", 35);
			} else if (getResourceType() == ResourceType.TYPE_COMICS) {
				TYPE.put("��������", 41);
				TYPE.put("��������", 42);
				TYPE.put("�ؼ�������", 43);
				TYPE.put("����������", 45);
			}
		} else {
			TYPE.put("��������", 11);
			TYPE.put("��������", 12);
			TYPE.put("�ؼ�������", 13);
			TYPE.put("����������", 15);
		}

		return new MapPropertySelectModel(TYPE, false, "ȫ��");
	}

	public IPropertySelectionModel getCreateTimeList() {
		Map<String, Integer> TIME = new OrderedMap<String, Integer>();
		TIME.put("��������", 0);
		TIME.put("������", 1);
		TIME.put("������", 2);
		TIME.put("������", 3);

		return new MapPropertySelectModel(TIME, false, "");
	}

	@SuppressWarnings("unchecked")
	public void setCheckboxSelected(boolean bSelected) {
		StatData keyWord = (StatData) getStatData();
		Set selectedKeyWord = getSelectedHotWords();
		if (bSelected) {
			selectedKeyWord.add(keyWord);
		} else {
			selectedKeyWord.remove(keyWord);
		}
		setSelectedHotWords(selectedKeyWord);
	}

	public boolean getCheckboxSelected() {
		return getSelectedHotWords().contains(getStatData());
	}

	@SuppressWarnings("unchecked")
	@Persist("session")
	@InitialValue("new java.util.HashSet()")
	public abstract Set getSelectedHotWords();

	@SuppressWarnings("unchecked")
	public abstract void setSelectedHotWords(Set set);

	public abstract Object getStatData();

	@Override
	protected void delete(Object object) {
	}

	public void search() {
	}

	public IBasicTableModel getTableModel() {
		return new IBasicTableModel() {
			public int getRowCount() {
				return getHotWordService().getHotWordResultCountByHQL(
						getContent(), getType(), getCreateTime()).intValue();
			}

			@SuppressWarnings("unchecked")
			public Iterator getCurrentPageRows(int nFirst, int nPageSize,
					ITableColumn objSortColumn, boolean bSortOrder) {
				int pageNo = nFirst / nPageSize;
				pageNo++;

				return getHotWordService().findHotWordByHQL(getContent(),
						getType(), getCreateTime(), pageNo, nPageSize)
						.iterator();
			}
		};
	}
}