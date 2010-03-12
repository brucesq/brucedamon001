/**
 * 
 */
package com.hunthawk.framework.tapestry.component;

import java.util.Collection;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.contrib.table.components.TableActionPageChange;
import org.apache.tapestry.contrib.table.components.TableFormPages;
import org.apache.tapestry.contrib.table.components.TableView;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableModelSource;

/**
 * @author sunquanzhi
 * 
 */
public abstract class TablePages extends TableFormPages {

	public TablePages() {
		super();
	}

	@InitialValue("1")
	public abstract int getPageNum();

	public void onPage(IRequestCycle objCycle) {
		ITableModelSource objSource = getTableModelSource();
		objSource.storeTableAction(new TableActionPageChange(getPageNum()));
		// objSource.storeTableAction(new
		// TableActionPageSizeChange(Integer.parseInt(getPagesize())));
	}

	// @InitialValue("20")
	// public abstract String getPagesize();
	//	
	// public IPropertySelectionModel getPagesizeModel()
	// {
	// return new StringPropertySelectionModel(new String[]{"20","50","80"});
	// }
	public boolean isAdvancedModel() {
		try {
			TableView table = (TableView) getTableModelSource();
			Object objSourceValue = table.getSource();
			if (objSourceValue instanceof IBasicTableModel) {
				return true;
			}
			if (objSourceValue instanceof Object[])
				return true;
			else if (objSourceValue instanceof List)
				return true;
			else if (objSourceValue instanceof Collection)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getCount() {
		try {
			TableView table = (TableView) getTableModelSource();
			Object objSourceValue = table.getSource();
			if (objSourceValue instanceof IBasicTableModel) {
				return ((IBasicTableModel)objSourceValue).getRowCount();
			}
			if (objSourceValue instanceof Object[])
				return ((Object[])objSourceValue).length;
			else if (objSourceValue instanceof List)
				return ((List)objSourceValue).size();
			else if (objSourceValue instanceof Collection)
				return ((Collection)objSourceValue).size();;
		} catch (Exception e) {
			
		}
		return 0;

	}

}