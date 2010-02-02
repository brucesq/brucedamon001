/**
 * 
 */
package com.hunthawk.reader.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.hunthawk.framework.util.OrderedMap;

/**
 * @author BruceSun
 * 
 */
public class Constants {

	private static Map<String, Integer> BUSSINESSTYPES = new OrderedMap<String, Integer>();
	static {
		// 1. WAP վ�� 2. �ֻ���� 3. �ֳ��豸
		BUSSINESSTYPES.put("WAP", 1);
		BUSSINESSTYPES.put("PAD", 3);
		BUSSINESSTYPES.put("APP", 2);

	}

	private static Map<String, Integer> VERSIONTYPES = new OrderedMap<String, Integer>();
	static {
		VERSIONTYPES.put("3G", 3);
		VERSIONTYPES.put("WAP1.X", 1);
		VERSIONTYPES.put("WAP2.0", 2);

	}

	private static Map<String, Integer> PRODUCTSTATUS = new HashMap<String, Integer>();
	static {
		PRODUCTSTATUS.put("������", 0);
		PRODUCTSTATUS.put("������", 1);
		PRODUCTSTATUS.put("��ͣ", 2);
		PRODUCTSTATUS.put("����", 3);
	}

	// ��������,0��������ID����1����ID����2���յ��������5��������ID����6����ID����
	private static Map<String, Integer> ORDERTYPE = new OrderedMap<String, Integer>();

	static {
		ORDERTYPE.put("����Ž���", 0);
		ORDERTYPE.put("ID����", 1);
		ORDERTYPE.put("�Ƽ�ָ������", 2);
		ORDERTYPE.put("����ź͵��������", 3);
		ORDERTYPE.put("���������", 5);
		ORDERTYPE.put("ID����", 6);


		ORDERTYPE.put("����������", 20);
		ORDERTYPE.put("����������", 21);
		ORDERTYPE.put("����������", 22);
		ORDERTYPE.put("����������", 23);

		ORDERTYPE.put("�ղ�������", 30);
		ORDERTYPE.put("�ղ�������", 31);
		ORDERTYPE.put("�ղ�������", 32);
		ORDERTYPE.put("�ղ�������", 33);

		ORDERTYPE.put("����������", 40);
		ORDERTYPE.put("����������", 41);
		ORDERTYPE.put("����������", 42);
		ORDERTYPE.put("����������", 43);

		ORDERTYPE.put("����������", 50);
		ORDERTYPE.put("����������", 51);
		ORDERTYPE.put("����������", 52);
		ORDERTYPE.put("����������", 53);
		
		ORDERTYPE.put("������������", 60);
		ORDERTYPE.put("������������", 61);
		ORDERTYPE.put("������������", 62);
		ORDERTYPE.put("������������", 63);
		
		/**
		 * ���ͶƱ����
		 * @author penglei 2009.11.20
		 */
		ORDERTYPE.put("ͶƱ��������", 70);
		ORDERTYPE.put("ͶƱ��������", 71);
		ORDERTYPE.put("ͶƱ��������", 72);
		ORDERTYPE.put("ͶƱ��������", 73);
		
		ORDERTYPE.put("��������������(�����ö�)", 7);
	}

	/**
	 * ����url�������������
	 * <ul>
	 * <li>1��������а�</li>
	 * <li>2���������а�</li>
	 * <li>3���ղ����а�</li>
	 * <li>4���������а�</li>
	 * <li>5���������а�</li>
	 * <li>6�����������а�</li>
	 * </ul>
	 * 
	 * @author penglei 2009-10-29 12:36:53
	 * yuzs 2009-11-05 �޸ģ�������� ���������У�
	 * 
	 */
	private static Map<Integer, Map<Integer, Integer>> URLORDER = new OrderedMap<Integer, Map<Integer, Integer>>();
	static {
		Map<Integer, Integer> downNumMap = new OrderedMap<Integer, Integer>(); // ���
		downNumMap.put(1, 2); // ���������

		Map<Integer, Integer> searchNumMap = new OrderedMap<Integer, Integer>(); // ����
		searchNumMap.put(1, 20); // ����������
		searchNumMap.put(2, 21); // ����������
		searchNumMap.put(3, 22); // ����������
		searchNumMap.put(4, 23); // ����������

		Map<Integer, Integer> favNumMap = new OrderedMap<Integer, Integer>();// �ղ�
		favNumMap.put(1, 30); // �ղ�������
		favNumMap.put(2, 31); // �ղ�������
		favNumMap.put(3, 32); // �ղ�������
		favNumMap.put(4, 33); // �ղ�������

		Map<Integer, Integer> orderNumMap = new OrderedMap<Integer, Integer>(); // ����
		orderNumMap.put(1, 40); // ����������
		orderNumMap.put(2, 41); // ����������
		orderNumMap.put(3, 42); // ����������
		orderNumMap.put(4, 43); // ����������

		Map<Integer, Integer> msgNumMap = new OrderedMap<Integer, Integer>(); // ����
		msgNumMap.put(1, 50); // ����������
		msgNumMap.put(2, 51); // ����������
		msgNumMap.put(3, 52); // ����������
		msgNumMap.put(4, 53); // ����������

		
		Map<Integer, Integer> rankingNumMap = new OrderedMap<Integer, Integer>(); // ����
		rankingNumMap.put(1, 60); // ������������
		rankingNumMap.put(2, 61); // ������������
		rankingNumMap.put(3, 62); // ������������
		rankingNumMap.put(4, 63); // ������������
		
		/**
		 * ���ͶƱ����
		 * @author penglei 2009.11.20
		 */
		Map<Integer, Integer> voteNumMap = new OrderedMap<Integer, Integer>(); // ͶƱ
		voteNumMap.put(1, 70); // ͶƱ��������
		voteNumMap.put(2, 71); // ͶƱ��������
		voteNumMap.put(3, 72); // ͶƱ��������
		voteNumMap.put(4, 73); // ͶƱ��������
		
		URLORDER.put(1, downNumMap);
		URLORDER.put(2, searchNumMap);
		URLORDER.put(3, favNumMap);
		URLORDER.put(4, orderNumMap);
		URLORDER.put(5, msgNumMap);
		URLORDER.put(6, rankingNumMap);
		URLORDER.put(7, voteNumMap);
	}

	public static Map<Integer, Map<Integer, Integer>> getUrlOrderMap() {
		return URLORDER;
	}

	// ��Ʒ״̬
	public static final int PRODUCTSTATUS_PUBLISH = 0;
	public static final int PRODUCTSTATUS_CHECK = 1;
	public static final int PRODUCTSTATUS_PAUSE = 2;
	public static final int PRODUCTSTATUS_OFFLINE = 3;

	// ��Ŀ״̬
	public static final int COLUMNSTATUS_PUBLISH = 1;
	public static final int COLUMNSTATUS_OFFLINE = 2;
	public static final int COLUMNSTATUS_DELETE = 3;

	// �Ʒ�����
	// ����
	public static final int FEE_TYPE_VIEW = 1;
	// VIP
	public static final int FEE_TYPE_VIP = 2;
	// ���ݿ���
	public static final int FEE_TYPE_CHOICE = 3;
	// �������
	public static final int FEE_TYPE_NORMAL = 4;
	// ���
	public static final int FEE_TYPE_FREE = 5;

	private static Map<String, Integer> FEETYPE = new OrderedMap<String, Integer>();
	static {
		FEETYPE.put("���", FEE_TYPE_FREE);
		FEETYPE.put("����(VIP)", FEE_TYPE_VIP);
		FEETYPE.put("����(���ݿ���)", FEE_TYPE_CHOICE);
		FEETYPE.put("����(����)", FEE_TYPE_NORMAL);
		FEETYPE.put("����", FEE_TYPE_VIEW);
	}

	public static Map<String, Integer> getFeeTypeMap() {
		return FEETYPE;
	}

	public static Map<String, Integer> getOrderTypeMap() {
		return ORDERTYPE;
	}

	// ��������
	// ����
	public static final int ORDER_TYPE_VIEW = 3;
	// ����
	public static final int ORDER_TYPE_MONTH = 2;
	// ���
	public static final int ORDER_TYPE_FREE = 1;
	// Memcached key�ָ���
	public static final String MEMCACHED_SLASH = ":";

	private static Map<String, Integer> CREDITS = new HashMap<String, Integer>();
	static {
		CREDITS.put("��", 1);
		CREDITS.put("��", 2);
		CREDITS.put("��", 3);
	}

	public static Map<String, Integer> getCredits() {
		return CREDITS;
	}

	public static final String[] STATUS = { "����", "����" };

	public static final int STATUS_WAITCHECK = 0;
	public static final int STATUS_PUBLISH = 1;

	public static final String[] PAGEGROUP_STATUS = { "������", "������", "��ͣ", "����" };

	// ҳ����״̬
	public static final int PAGEGROUPSTATUS_PUBLISH = 0;
	public static final int PAGEGROUPSTATUS_CHECK = 1;
	public static final int PAGEGROUPSTATUS_PAUSE = 2;
	public static final int PAGEGROUPSTATUS_OFFLINE = 3;

	// ����ĸ
	private static Map<String, String> INITIALLETTER = new TreeMap<String, String>();
	static {
		INITIALLETTER.put("A", "A");
		INITIALLETTER.put("B", "B");
		INITIALLETTER.put("C", "C");
		INITIALLETTER.put("D", "D");
		INITIALLETTER.put("E", "E");
		INITIALLETTER.put("F", "F");
		INITIALLETTER.put("G", "G");
		INITIALLETTER.put("H", "H");
		INITIALLETTER.put("I", "I");
		INITIALLETTER.put("J", "J");
		INITIALLETTER.put("K", "K");
		INITIALLETTER.put("L", "L");
		INITIALLETTER.put("M", "M");
		INITIALLETTER.put("N", "N");
		INITIALLETTER.put("O", "O");
		INITIALLETTER.put("P", "P");
		INITIALLETTER.put("Q", "Q");
		INITIALLETTER.put("R", "R");
		INITIALLETTER.put("S", "S");
		INITIALLETTER.put("T", "T");
		INITIALLETTER.put("U", "U");
		INITIALLETTER.put("V", "V");
		INITIALLETTER.put("W", "W");
		INITIALLETTER.put("X", "X");
		INITIALLETTER.put("Y", "Y");
		INITIALLETTER.put("Z", "Z");
	}

	private static final Map<String, Integer> AUTHORSTATUS = new HashMap<String, Integer>();
	static {
		AUTHORSTATUS.put("������", 0);
		AUTHORSTATUS.put("����", 1);
	}

	// ��Ȩ����Դ״̬
	private static final Map<String, Integer> REFERENSTATUS = new HashMap<String, Integer>();
	static {
		REFERENSTATUS.put("����", 0);
		REFERENSTATUS.put("��ͣ", 1);
		REFERENSTATUS.put("����", 2);
	}

	// ��Դ״̬
	private static final Map<String, Integer> RESOURCESTATUS = new HashMap<String, Integer>();
	static {
		RESOURCESTATUS.put("����", 0);
		RESOURCESTATUS.put("����", 1);
		RESOURCESTATUS.put("����", 2);
		RESOURCESTATUS.put("��ͣ", 3);
		RESOURCESTATUS.put("����", 4);
		RESOURCESTATUS.put("���", 5);
	}

	private static Map<String, String> AREA_CODE = new TreeMap<String, String>();
	static {
		AREA_CODE.put("ȫ��", "001");
		AREA_CODE.put("����", "010");
		AREA_CODE.put("����", "011");
		AREA_CODE.put("���", "013");
		AREA_CODE.put("ɽ��", "017");
		AREA_CODE.put("�ӱ�", "018");
		AREA_CODE.put("ɽ��", "019");
		AREA_CODE.put("����", "022");
		AREA_CODE.put("����", "030");
		AREA_CODE.put("�Ϻ�", "031");
		AREA_CODE.put("����", "034");
		AREA_CODE.put("�㽭", "036");
		AREA_CODE.put("����", "038");
		AREA_CODE.put("����", "050");
		AREA_CODE.put("�㶫", "051");
		AREA_CODE.put("����", "059");
		AREA_CODE.put("�ຣ", "070");
		AREA_CODE.put("����", "071");
		AREA_CODE.put("����", "074");
		AREA_CODE.put("����", "075");
		AREA_CODE.put("����", "076");
		AREA_CODE.put("����", "079");
		AREA_CODE.put("�Ĵ�", "081");
		AREA_CODE.put("����", "083");
		AREA_CODE.put("����", "084");
		AREA_CODE.put("����", "085");
		AREA_CODE.put("����", "086");
		AREA_CODE.put("����", "087");
		AREA_CODE.put("����", "088");
		AREA_CODE.put("�½�", "089");
		AREA_CODE.put("����", "090");
		AREA_CODE.put("����", "091");
		AREA_CODE.put("������", "097");

	}

	// ��Դ�ܴ���
	private static Map<String, Integer> RESOURCETYPE = new HashMap<String, Integer>();
	static {
		RESOURCETYPE.put("ͼ��", 1);
		RESOURCETYPE.put("��ֽ", 2);
		RESOURCETYPE.put("��־", 3);
		RESOURCETYPE.put("����", 4);
		RESOURCETYPE.put("����", 5);
		RESOURCETYPE.put("��Ƶ", 6);
		RESOURCETYPE.put("��Ѷ", 7);
	}

	// ��Դ�Ƿ�ȫ��
	private static Map<String, Integer> ISFINISHED = new HashMap<String, Integer>();
	static {
		ISFINISHED.put("ȫ��", 1);
		ISFINISHED.put("����", 2);
	}

	// ��Դ�Ƿ�(ר�У����棬�׷�������)
	private static Map<String, Integer> YESNO = new HashMap<String, Integer>();
	static {
		YESNO.put("��", 2);
		YESNO.put("��", 1);
	}

	// ��Դ�Ƽ�ָ��
	private static Map<String, Integer> RESOURCEEXP = new HashMap<String, Integer>();
	static {
		RESOURCEEXP.put("1", 1);
		RESOURCEEXP.put("2", 2);
		RESOURCEEXP.put("3", 3);
		RESOURCEEXP.put("4", 4);
		RESOURCEEXP.put("5", 5);
	}

	// ҵ������
	public static Map<String, Integer> getBussinessTypes() {
		return BUSSINESSTYPES;
	}

	// �汾����
	public static Map<String, Integer> getVersionTypes() {
		return VERSIONTYPES;
	}

	public static String getBussinessTypeName(Integer id) {
		for (Map.Entry<String, Integer> entry : BUSSINESSTYPES.entrySet()) {
			if (entry.getValue().equals(id)) {
				return entry.getKey();
			}
		}
		return "";
	}

	// ��Ʒ״̬
	public static Map<String, Integer> getProductStatus() {
		return PRODUCTSTATUS;
	}

	public static String getProductStatusName(Integer status) {
		for (Map.Entry<String, Integer> entry : Constants.getProductStatus()
				.entrySet()) {
			if (entry.getValue().equals(status))
				return entry.getKey();
		}
		return "";
	}

	// ����ĸ
	public static Map<String, String> getInitialLetter() {
		return INITIALLETTER;
	}

	// ����״̬
	public static Map<String, Integer> getAuthorStatus() {
		return AUTHORSTATUS;
	}

	// ��Ȩ״̬
	public static Map<String, Integer> getReferenStatus() {
		return REFERENSTATUS;
	}

	// ��Դ״̬
	public static Map<String, Integer> getResourceStatus() {
		return RESOURCESTATUS;
	}

	public static Map<String, String> getAreaCode() {
		return AREA_CODE;
	}

	// ��Դ����
	public static Map<String, Integer> getResourceType() {
		return RESOURCETYPE;
	}

	// ��Դ�Ƿ�ȫ��
	public static Map<String, Integer> getResourceFinished() {
		return ISFINISHED;
	}

	// ��Դ�Ƿ�
	public static Map<String, Integer> getResourceYesNo() {
		return YESNO;
	}

	// ��Դ�Ƽ�ָ��
	public static Map<String, Integer> getRESOURCEEXP() {
		return RESOURCEEXP;
	}

	public static String getAreaName(String aid) {
		for (Map.Entry<String, String> entry : AREA_CODE.entrySet()) {
			if (entry.getValue().equals(aid)) {
				return entry.getKey();
			}
		}
		return "";
	}
	
	public static final java.lang.String SPLIT_DOC_TAG = "#news page split tag#";
}
