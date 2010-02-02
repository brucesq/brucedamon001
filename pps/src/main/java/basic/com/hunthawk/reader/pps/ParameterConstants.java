/**
 * 
 */
package com.hunthawk.reader.pps;

/**
 * @author BruceSun
 * 
 */
public class ParameterConstants {

	//
	public static final String PORTAL_PATH = "/s.do";
	// ҳ�漶�� ��ҳ �б�ҳ ��Դҳ ����ҳ
	public static final String PAGE = "pg";

	// ��ƷID
	public static final String PRODUCT_ID = "pd";
	// ҳ����ID
	public static final String PAGEGROUP_ID = "gd";
	// ��ĿID
	public static final String COLUMN_ID = "cd";
	// ģ��ID
	public static final String TEMPLATE_ID = "td";

	// ����ID
	public static final String AREA_ID = "ad";
	// �Ʒ�ID
	public static final String FEE_ID = "ed";
	// ���۰�ID
	public static final String FEE_BAG_ID = "fd";
	
	public static final String MONTH_FEE_BAG_ID = "pf";
	// ���۰�����ID
	public static final String FEE_BAG_RELATION_ID = "nd";
	// ��ԴID
	public static final String RESOURCE_ID = "rd";
	// �ƹ�����ID
	public static final String CHANNEL_ID = "fc";
	// ��ͨ��ڲ���
	public static final String UNICOM_PT = "PT";
	// �½�ID
	public static final String CHAPTER_ID = "zd";
	// ҳ��
	public static final String PAGE_NUMBER = "pn";
	//����ȫ��
	public static final String REMAINING_TEXT = "ret";
	// ÿҳ��ʾ����
	public static final String WORDAGE = "ps";
	// ͼ������
	public static final String BOOK_TYPE = "bt";
	//��Դ����
	public static final String RESOURCE_TYPE="rt";
	//��������  ��(���ߡ�������)
	public static final String SEARCH_TYPE="st";
	//�����ؼ���
	public static final String SEARCH_PARAM_VALUE="key";
	//����������������
	public static final String QUICK_SEARCH_LINK_NAME="qkey";
	
	//����ID
	public static final String AUTHOR_ID="au";

	// ��ǩID
	public static final String BOOK_MARK_ID = "mid";

	// ��ҳ
	public static final String PAGE_PRODUCT = "p";
	// ��Ŀҳ
	public static final String PAGE_COLUMN = "c";
	// ��Դҳ
	public static final String PAGE_RESOURCE = "r";
	// ����ҳ
	public static final String PAGE_DETAIL = "d";
	
	// ����ҳ
	public static final String PAGE_DOWNLOAD = "t";

	// ������ҳ�� �����ǩ\����ղأ�ɾ���ղص�
	public static final String COMMON_PAGE = "fn";
	public static final String BAG_FUNCTION_FLAG="BFF";
	// ����ղ�
	public static final String COMMON_PAGE_FAVORITE_ADD = "fa";
	// ɾ���ղ�
	public static final String COMMON_PAGE_FAVORITE_DEL = "fd";

	// �����ǩ
	public static final String COMMON_PAGE_BOOKMARK_ADD = "ma";
	// ɾ����ǩ
	public static final String COMMON_PAGE_BOOKMARK_DEL = "md";
	// ������
	public static final String COMMON_PAGE_BOOKBAG_ADD = "ba";
	// ɾ�����
	public static final String COMMON_PAGE_BOOKBAG_DEL = "bd";
	//�����Ʒ�ҳ��
	public static final String COMMON_PAGE_FEE = "fee";
	//ͨ������
	public static final String COMMON_PAGE_LINK = "cl";
	//�汾��Ϣ
	public static final String VERSION_TYPE = "vt";
	
	//��ID
	public static final String TOME_ID="tid";

	public static final String SHOW_FLAG="show";
	//�����Զ����� timer
	public static final String TIMER="T";
	//�����Ƿ��Զ�����
	public static final String AUTO_PLAY="auto";
	
//	// ��ǩ������ʶ (add or delete)
//	public static final String OPERATE_ACTION = "operate";
//	// ���
//	public static final String OPERATE_ACTION_ADD = "add";
//	// ɾ��
//	public static final String OPERATE_ACTION_DELETE = "del";

	//�Ƿ��ǵ�һ�β�ѯ��ʶ 
	public static final String IS_FIRST_SEARCH="Y";

	public static final String PARAM_URL = "ul";

	public static final java.lang.String TAG_SCRIPT_PREFIX = "<%";

	public static final java.lang.String TAG_SCRIPT_SUFFIX = "%>";

	//�ö���ѯ����=0
	public static final int ROLL_RULE_PUSH = 0;
	//��ҳ��ѯ����=1
	public static final int ROLL_RULE_PAGE = 1;
	//�����ȡ��ѯ����=2
	public static final int ROLL_RULE_RANDOM = 2;
	//���һ��ʱ�������ȡ��ѯ����=3
	public static final int ROLL_RULE_RANDOM_INTERVAL = 3;
	//���һ��ʱ��_��ѯһ����¼=4
	public static final int ROLL_RULE_PAGE_INTERVAL = 4;
	//��Դ�ֻ�����Ĭ���ö�����
	public static final int TOP_COUNT=2;

	public static final String MEMCACHED_TAG = "reader_tag_";
	
	public static final String PRE_TAG_SUFFIX = "$#";
	  
	public static final String END_TAG_SUFFIX = "#";
	
	public static final String NEXT_CHAPTER_LINK="��һ��";
	public static final String PRE_CHAPTER_LINK="��һ��";
	public static final String NEXT_RESOURCE_LINK="��һ��";
	public static final String PRE_RESOURCE_LINK="��һ��";
	
	//�½�����Ĭ��ÿҳ��ʾ1000��
	public static final int CHAPTER_CONTENT_WORD_SET=1000;
	
	public static final String PAMS_NAVIGATOR="PAMS_NAVI";
	
	//Ĭ�������Ĵ�С(size)
	public static final int DEFAULT_INPUT_SIZE=15;
	//Ĭ������������(type)
	public static final String DEFAULT_INPUT_TYPE="text";
	//Ĭ������������(name)
	public static final String DEFAULT_INPUT_NAME="name";
	//���ض��ƻ�ȡ�������ı�־
	public static final String ORDER_FLAG="flag";
	
	//������ز���
	//�������ݹؼ���
	public static final String COMMENT_PARAM_VALUE="MSG";
	//�û��Զ���key
	public static final String CUSTOM_KEY_VALUE="CK";
	//���԰��
	public static final String COMMENT_PLATE="CP";
	//��������
	public static final String COMMENT_TARGET="CT";
	//����Ŀ�����ID
	public static final String COMMENT_TARGET_ID="CTI";
	
	
	//ͶƱ��ز���
	//ͶƱ����
	public static final String VOTE_VOTE_TYPE="VT";
	//ͶƱ��ID
	public static final String VOTE_ITEM_ID="II";
	//����ID (��Ʒ����Ŀ����Դ���û�����ID)
	public static final String VOTE_CONTENT_ID="CI";
	
	//����������ʾ�����ֽڳ���
	public static final int AUTHOR_NAME_BYTES=12;
	//��Դ������ʾ�ֽڳ���
	public static final int RESOURCE_NAME_BYTES=24;
	//�½�������ʾ�ֽڳ���
	public static final int CHAPTER_NAME_BYTES=54;
	//��Ŀ������ʾ�ֽڳ���
	public static final int COLUMN_NAME_BYTES=24;
	//��������ʹ�õ��滻ռλ��
	public static final String REPLACE_SYMBOL=".";
	//������ϲ����� ���֮��ķָ����Ŷ���
	public static final String DEFAULT_SPLIT="-";
	
	/**
	 * url����һ���ֶ�
	 * 
	 * @author penglei 2009-10-29 13:0:35
	 * 
	 */
	public static final String ORDER="od";
	
	/**
	 * url��������ֶ�
	 * 
	 * @author penglei 2009-10-29 13:0:35
	 * 
	 */
	public static final String ORDERSUB="ods";
	
	public static final String VERSION_SET="version_set";
}
