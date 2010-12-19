/** package declaration */
package renault.swo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import renault.swo.bo.BooleanParam;
import renault.swo.bo.MultiTemplateDistributionDire;
import renault.swo.bo.PlantSite;
import renault.swo.bo.PublishedTemplateType;
import renault.swo.bo.SWordUser;
import renault.swo.bo.TemplateFormat;
import renault.swo.bo.TemplateOrientation;
import renault.swo.bo.WorkSheetTemplate;
import renault.swo.exception.MessageErrorUnknownException;
import renault.swo.global.Constants;
import renault.swo.util.DBUtil;

/**
 * The Class WorkSheetTemapleDAO.
 * 
 * @author Vijayaraja .
 * @authorId X.
 * @version 1.0.0.
 * @company XXXXX.
 */
public class WorkSheetTemapleDAO {

	/**
	 * Instantiates a new work sheet temaple dao.
	 */
	public WorkSheetTemapleDAO() {
		// TODO Auto-generated constructor stub
	}

	/** The logger. */
	private static final Logger LOGGER = Logger
			.getLogger(WorkSheetTemapleDAO.class);

	/** The Constant IS_WORKSHEET_EXISTS_ALL. */
	private static final String IS_WORKSHEET_EXISTS_ALL = "select 1 and "
			+ " (select 1 from T_WORKSHEET_TEMPLATES TEMAPLATE WHERE TEMAPLATE.COL_WORKSHEET_TEMPLATE_ID = ?) "
			+ " IS NOT NULL AS RETURN_CODE";

	/** The Constant IS_WORKSHEET_EXISTS_BY_SPECIFIC. */
	private static final String IS_WORKSHEET_EXISTS_BY_SPECIFIC = "select 1 and "
			+ " (select 1 from T_WORKSHEET_TEMPLATES TEMAPLATE WHERE TEMAPLATE.COL_WORKSHEET_TEMPLATE_ID = ? AND TEMAPLATE.COL_IS_PUBLISHED = ? ) "
			+ " IS NOT NULL AS RETURN_CODE";

	/** The Constant IS_WORKSHEET_NAME_EXISTS_BY_SPECIFIC. */
	private static final String IS_WORKSHEET_NAME_EXISTS_BY_SPECIFIC = "select 1 and "
			+ " ( select 1 from T_WORKSHEET_TEMPLATES TEMAPLATE WHERE TEMAPLATE.COL_NAME = ? AND TEMAPLATE.COL_IS_PUBLISHED = ? LIMIT 1 ) "
			+ " IS NOT NULL AS RETURN_CODE";

	/** The Constant GET_WORKSHEET_NAME_BY_ID. */
	private static final String GET_WORKSHEET_NAME_BY_ID = "SELECT TEMAPLATE.COL_NAME from T_WORKSHEET_TEMPLATES TEMAPLATE "
			+ "WHERE TEMAPLATE.COL_WORKSHEET_TEMPLATE_ID = ? AND TEMAPLATE.COL_IS_PUBLISHED = ?  ";

	/** The Constant GET_WORKSHEET_ID_BY_NAME. */
	private static final String GET_WORKSHEET_ID_BY_NAME = "SELECT TEMAPLATE.COL_WORKSHEET_TEMPLATE_ID from "
			+ "T_WORKSHEET_TEMPLATES TEMAPLATE WHERE TEMAPLATE.COL_NAME = ? AND TEMAPLATE.COL_IS_PUBLISHED = ? ";

	/** The Constant INSERT_WORKSHEET_TEMPALTE. */
	private static final String INSERT_WORKSHEET_TEMPALTE = "	INSERT INTO T_WORKSHEET_TEMPLATES (								"
			+ "				T_WORKSHEET_TEMPLATES.COL_NAME,                     "
			+ "				T_WORKSHEET_TEMPLATES.COL_VERSION,                  "
			+ "				T_WORKSHEET_TEMPLATES.COL_IS_PUBLISHED,             "
			+ "				T_WORKSHEET_TEMPLATES.COL_FORMAT,                   "
			+ "				T_WORKSHEET_TEMPLATES.COL_HEIGHT,                   "
			+ "				T_WORKSHEET_TEMPLATES.COL_WIDTH,                    "
			+ "				T_WORKSHEET_TEMPLATES.COL_ORIENTATION,              "
			+ "				T_WORKSHEET_TEMPLATES.COL_AUTHOR,                   "
			+ "				T_WORKSHEET_TEMPLATES.COL_XML_DESCRIPTION,          "
			+ "				T_WORKSHEET_TEMPLATES.COL_NO_COLUMN,                "
			+ "				T_WORKSHEET_TEMPLATES.COL_NO_ROW,                   "
			+ "				T_WORKSHEET_TEMPLATES.COL_TOP_MARGIN,               "
			+ "				T_WORKSHEET_TEMPLATES.COL_LEFT_MARGIN,              "
			+ "				T_WORKSHEET_TEMPLATES.COL_VERTICAL_SPACING,         "
			+ "				T_WORKSHEET_TEMPLATES.COL_HORIZONTAL_SPACING,       "
			+ "				T_WORKSHEET_TEMPLATES.COL_PROPAGATION_TYPE,         "
			+ "				T_WORKSHEET_TEMPLATES.COL_PLANT_SITE_ID,            "
			+ "				T_WORKSHEET_TEMPLATES.COL_TEMPLATE_TYPE,            "
			+ "				T_WORKSHEET_TEMPLATES.COL_UPDATE_TIME)              "
			+ "	VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW())                    ";

	/** The Constant UPDATE_WORKSHEET_TEMPLATE. */
	private static final String UPDATE_WORKSHEET_TEMPLATE = "	UPDATE T_WORKSHEET_TEMPLATES SET 						 "
			+ "	T_WORKSHEET_TEMPLATES.COL_NAME = ?,                      "
			+ "	T_WORKSHEET_TEMPLATES.COL_VERSION = ?,                   "
			+ "	T_WORKSHEET_TEMPLATES.COL_IS_PUBLISHED = ?,              "
			+ "	T_WORKSHEET_TEMPLATES.COL_FORMAT = ?,                    "
			+ "	T_WORKSHEET_TEMPLATES.COL_HEIGHT = ?,                    "
			+ "	T_WORKSHEET_TEMPLATES.COL_WIDTH = ?,                     "
			+ "	T_WORKSHEET_TEMPLATES.COL_ORIENTATION = ?,               "
			+ "	T_WORKSHEET_TEMPLATES.COL_AUTHOR = ?,                    "
			+ "	T_WORKSHEET_TEMPLATES.COL_XML_DESCRIPTION = ?,           "
			+ "	T_WORKSHEET_TEMPLATES.COL_NO_COLUMN = ?,                 "
			+ "	T_WORKSHEET_TEMPLATES.COL_NO_ROW = ?,                    "
			+ "	T_WORKSHEET_TEMPLATES.COL_TOP_MARGIN = ?,                "
			+ "	T_WORKSHEET_TEMPLATES.COL_LEFT_MARGIN = ?,               "
			+ "	T_WORKSHEET_TEMPLATES.COL_VERTICAL_SPACING = ?,          "
			+ "	T_WORKSHEET_TEMPLATES.COL_HORIZONTAL_SPACING = ?,        "
			+ "	T_WORKSHEET_TEMPLATES.COL_PROPAGATION_TYPE = ?,          "
			+ "	T_WORKSHEET_TEMPLATES.COL_PLANT_SITE_ID = ?,             "
			+ "	T_WORKSHEET_TEMPLATES.COL_TEMPLATE_TYPE = ?,             "
			+ "	T_WORKSHEET_TEMPLATES.COL_UPDATE_TIME = NOW(),           "
			+ "	T_WORKSHEET_TEMPLATES.COL_PREVIEW = ?                    "
			+ "	WHERE T_WORKSHEET_TEMPLATES.COL_WORKSHEET_TEMPLATE_ID =  ?   ";

	/** The Constant GET_WORKSHEET_TEMAPLTE_ID. */
	private static final String GET_WORKSHEET_TEMAPLTE_ID = "SELECT T_WORKSHEET_TEMPLATES.COL_WORKSHEET_TEMPLATE_ID FROM T_WORKSHEET_TEMPLATES "
			+ " WHERE T_WORKSHEET_TEMPLATES.COL_NAME = ? AND T_WORKSHEET_TEMPLATES.COL_VERSION = ? ";

	/** The Constant INSERT_MACRO_OBJECTS_USAGE_STAT. */
	private static final String INSERT_MACRO_OBJECTS_USAGE_STAT = "INSERT INTO T_MACRO_OBJECTS_USAGE_STAT "
			+ "(COL_WORKSHEET_TEMPLATE_ID,COL_MACRO_OBJECT_ID) VALUES (?,?)";

	/** The Constant INSERT_DUBPLICATE_MACRO_OBJECT_USAGE_STAT. */
	private static final String INSERT_DUBPLICATE_MACRO_OBJECT_USAGE_STAT = " INSERT INTO T_MACRO_OBJECTS_USAGE_STAT (COL_WORKSHEET_TEMPLATE_ID,COL_MACRO_OBJECT_ID) "
			+ " SELECT ?,COL_MACRO_OBJECT_ID "
			+ " FROM T_MACRO_OBJECTS_USAGE_STAT WHERE COL_WORKSHEET_TEMPLATE_ID = ? ";

	/** The Constant DELETE_MACRO_OBJECTS_USAGE_STAT. */
	private static final String DELETE_MACRO_OBJECTS_USAGE_STAT = " DELETE FROM T_MACRO_OBJECTS_USAGE_STAT "
			+ " WHERE COL_WORKSHEET_TEMPLATE_ID = ?  ";

	/** The Constant INSERT_REFERENCED_WORKSHEET_TEMPLATE. */
	private static final String INSERT_REFERENCED_WORKSHEET_TEMPLATE = " INSERT INTO T_REFERENCED_WORKSHEET_TEMPLATES "
			+ " (COL_DRAFT_MULTIPLE_WORKSHEET_TEMPLATE_ID,COL_PUBLISHED_SINGLE_WORKSHEET_TEMPLATE_ID) VALUES (?,?) ";

	/** The Constant INSERT_DUBLICATE_REFERENCED_WORKSHEET_TEMPLATE. */
	private static final String INSERT_DUBLICATE_REFERENCED_WORKSHEET_TEMPLATE = " INSERT INTO T_REFERENCED_WORKSHEET_TEMPLATES "
			+ " ( COL_DRAFT_MULTIPLE_WORKSHEET_TEMPLATE_ID,COL_PUBLISHED_SINGLE_WORKSHEET_TEMPLATE_ID ) "
			+ " SELECT ?,COL_PUBLISHED_SINGLE_WORKSHEET_TEMPLATE_ID FROM  T_REFERENCED_WORKSHEET_TEMPLATES WHERE "
			+ " COL_DRAFT_MULTIPLE_WORKSHEET_TEMPLATE_ID = ? ";

	/** The Constant DELETE_REFERENCED_WORKSHEET_TEMPLATE. */
	private static final String DELETE_REFERENCED_WORKSHEET_TEMPLATE = " DELETE FROM T_REFERENCED_WORKSHEET_TEMPLATES "
			+ " WHERE COL_DRAFT_MULTIPLE_WORKSHEET_TEMPLATE_ID = ? ";

	/** The Constant IS_LOCAL_MANAGER_HAVE_ACCESS_RIGHTS. */
	private static final String IS_LOCAL_MANAGER_HAVE_ACCESS_RIGHTS = "  SELECT 1 AND (																									   "
			+ "  SELECT 1                                                                                                          "
			+ "  FROM (SELECT DRAFT_TEMPLATE.COL_IS_PUBLISHED,                                                                     "
			+ "  			PUBLISHED_TEMPLATE.COL_PLANT_SITE_ID PUBLISHED_SITE_ID,                                                "
			+ "  			DRAFT_TEMPLATE.COL_PLANT_SITE_ID DRAFT_SITE_ID                                                         "
			+ "  		FROM   T_WORKSHEET_TEMPLATES DRAFT_TEMPLATE LEFT OUTER JOIN                                                "
			+ "     	 	    T_WORKSHEET_TEMPLATE_PUBLICATIONS PUBLISHED_TEMPLATE                                               "
			+ "  		    ON (DRAFT_TEMPLATE.COL_IS_PUBLISHED = 1 AND                                                            "
			+ "  		       PUBLISHED_TEMPLATE.COL_PUBLISHED_WORKSHEET_TEMPLATE_ID = DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID)  "
			+ "  		WHERE  DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID = ?)                                                       "
			+ "  	TEMPLATE_DETAIL,                                                                                               "
			+ "  	T_USERS USERS,                                                                                                 "
			+ "  	T_USERS_PARAMETERS_BY_APPLICATION PARAM                                                                        "
			+ "  WHERE  USERS.COL_USER_ID   = ? AND                                                                                "
			+ "     	PARAM.COL_USER_ID = USERS.COL_USER_ID AND                                                                  "
			+ "   	PARAM.COL_PARAM_NAME = 'PLANT_SITE' AND                                                                        "
			+ "  	((TEMPLATE_DETAIL.DRAFT_SITE_ID = PARAM.COL_PARAM_VALUE AND                                                    "
			+ "  	 TEMPLATE_DETAIL.COL_IS_PUBLISHED = 0) OR                                                                      "
			+ "  	(TEMPLATE_DETAIL.PUBLISHED_SITE_ID = PARAM.COL_PARAM_VALUE AND                                                 "
			+ "  	 TEMPLATE_DETAIL.COL_IS_PUBLISHED = 1)) LIMIT 1)                                                               "
			+ "  IS NOT NULL AS RETURN_CODE                                                                                        ";

	/** The Constant DELETE_PRIVATE_DRAFT_WORKSHEET_TEMPLATES. */
	private static final String DELETE_PRIVATE_DRAFT_WORKSHEET_TEMPLATES = "    DELETE              					 "
			+ "	FROM                                                                                                         "
			+ "	    T_WORKSHEET_TEMPLATES					                                                                 "
			+ "	WHERE                                                                                                        "
			+ " T_WORKSHEET_TEMPLATES.COL_PLANT_SITE_ID  =                                                                 	 "
			+ " (SELECT COL_PARAM_VALUE FROM T_USERS_PARAMETERS_BY_APPLICATION WHERE COL_PARAM_NAME = 'PLANT_SITE' AND COL_USER_ID = ?)"
			+ "  AND                                                                                                       	 "
			+ "  T_WORKSHEET_TEMPLATES.COL_WORKSHEET_TEMPLATE_ID = ?                                                  		 ";

	/** The Constant IS_VIEWER_HAVE_ACCESS_RIGHTS. */
	private static final String IS_VIEWER_HAVE_ACCESS_RIGHTS = " SELECT 1 AND (		            "
			+ " 	SELECT 	1																	"
			+ " 	FROM                                                                        "
			+ " 		T_WORKSHEET_TEMPLATE_PUBLICATIONS PUBLISHED_TEMPLATE,                   "
			+ " 		T_USERS USERS,                                                          "
			+ " 		T_USERS_PARAMETERS_BY_APPLICATION PARAM                                 "
			+ " 	WHERE                                                                       "
			+ "  		PUBLISHED_TEMPLATE.COL_PUBLISHED_WORKSHEET_TEMPLATE_ID = ?              "
			+ " 	AND                                                                         "
			+ " 		PARAM.COL_USER_ID = USERS.COL_USER_ID                                   "
			+ " 	AND                                                                         "
			+ " 		PARAM.COL_PARAM_NAME = 'PLANT_SITE'                                     "
			+ " 	AND                                                                         "
			+ " 		USERS.COL_USER_ID = ?	                                                "
			+ " 	AND                                                                         "
			+ " 		PARAM.COL_PARAM_VALUE = PUBLISHED_TEMPLATE.COL_PLANT_SITE_ID            "
			+ " 	LIMIT 1  )                                                                  "
			+ "  IS NOT NULL AS RETURN_CODE											            "; 

	/** The Constant GET_WORKSHEET_TEMAPLTE_BY_ID. */
	private static final String GET_WORKSHEET_TEMAPLTE_BY_ID = "   SELECT 	COL_WORKSHEET_TEMPLATE_ID,					"
			+ "   		COL_NAME,                                       "
			+ "   		COL_VERSION,                                    "
			+ "   		COL_TEMPLATE_TYPE,                              "
			+ "   		COL_IS_PUBLISHED,                               "
			+ "   		COL_FORMAT,                                     "
			+ "   		COL_HEIGHT,                                     "
			+ "   		COL_WIDTH,                                      "
			+ "   		COL_ORIENTATION,                                "
			+ "   		COL_AUTHOR,                                     "
			+ "   		COL_UPDATE_TIME,                                "
			+ "   		COL_XML_DESCRIPTION,                            "
			+ "   		COL_NO_COLUMN,                                  "
			+ "   		COL_NO_ROW,                                     "
			+ "   		COL_TOP_MARGIN,                                 "
			+ "   		COL_LEFT_MARGIN,                                "
			+ "   		COL_VERTICAL_SPACING,                           "
			+ "   		COL_HORIZONTAL_SPACING,                         "
			+ "   		COL_PROPAGATION_TYPE,                           "
			+ "   		COL_PLANT_SITE_ID                               "
			+ "   FROM                                                  "
			+ "   	T_WORKSHEET_TEMPLATES                               "
			+ "   WHERE                                                 "
			+ "   	COL_WORKSHEET_TEMPLATE_ID = ?                       ";
	
	/** The Constant GET_WORKSHEET_TEMAPLTE_BY_ID_PREIVIEW_ON. */
	private static final String GET_WORKSHEET_TEMAPLTE_BY_ID_PREIVIEW_ON = "   SELECT 	COL_WORKSHEET_TEMPLATE_ID,					"
			+ "   		COL_NAME,                                       "
			+ "   		COL_VERSION,                                    "
			+ "   		COL_TEMPLATE_TYPE,                              "
			+ "   		COL_IS_PUBLISHED,                               "
			+ "   		COL_FORMAT,                                     "
			+ "   		COL_HEIGHT,                                     "
			+ "   		COL_WIDTH,                                      "
			+ "   		COL_ORIENTATION,                                "
			+ "   		COL_AUTHOR,                                     "
			+ "   		COL_UPDATE_TIME,                                "
			+ "   		COL_XML_DESCRIPTION,                            "
			+ "   		COL_NO_COLUMN,                                  "
			+ "   		COL_NO_ROW,                                     "
			+ "   		COL_TOP_MARGIN,                                 "
			+ "   		COL_LEFT_MARGIN,                                "
			+ "   		COL_VERTICAL_SPACING,                           "
			+ "   		COL_HORIZONTAL_SPACING,                         "
			+ "   		COL_PROPAGATION_TYPE,                           "
			+ "   		COL_PLANT_SITE_ID,                              "
			+ "			COL_PREVIEW										"	
			+ "   FROM                                                  "
			+ "   	T_WORKSHEET_TEMPLATES                               "
			+ "   WHERE                                                 "
			+ "   	COL_WORKSHEET_TEMPLATE_ID = ?                       ";

	/** The Constant GET_WORKSHEET_TEMAPLTE_BY_ID_AND_USER_ID. */
	private static final String GET_WORKSHEET_TEMAPLTE_BY_ID_AND_USER_ID = "   SELECT 	COL_WORKSHEET_TEMPLATE_ID,					"
			+ "   		COL_NAME,                                       "
			+ "   		COL_VERSION,                                    "
			+ "   		COL_TEMPLATE_TYPE,                              "
			+ "   		COL_IS_PUBLISHED,                               "
			+ "   		COL_FORMAT,                                     "
			+ "   		COL_HEIGHT,                                     "
			+ "   		COL_WIDTH,                                      "
			+ "   		COL_ORIENTATION,                                "
			+ "   		COL_AUTHOR,                                     "
			+ "   		COL_UPDATE_TIME,                                "
			+ "   		COL_XML_DESCRIPTION,                            "
			+ "   		COL_NO_COLUMN,                                  "
			+ "   		COL_NO_ROW,                                     "
			+ "   		COL_TOP_MARGIN,                                 "
			+ "   		COL_LEFT_MARGIN,                                "
			+ "   		COL_VERTICAL_SPACING,                           "
			+ "   		COL_HORIZONTAL_SPACING,                         "
			+ "   		COL_PROPAGATION_TYPE,                           "
			+ "   		COL_PLANT_SITE_ID                               "
			+ "   FROM                                                  "
			+ "   	T_WORKSHEET_TEMPLATES                               "
			+ "   WHERE                                                 "
			+ "   	COL_WORKSHEET_TEMPLATE_ID = ?                       ";

	/** The Constant RENAME_WORKSHEET. */
	private static final String RENAME_WORKSHEET = "UPDATE T_WORKSHEET_TEMPLATES TEMPLATE SET TEMPLATE.COL_NAME = ? "
			+ " WHERE COL_WORKSHEET_TEMPLATE_ID = ? ";

	/** The Constant IS_WORKSHEET_TEMPLATE_IS_MULTIPLE. */
	private static final String IS_WORKSHEET_TEMPLATE_IS_MULTIPLE = "	SELECT 1 AND 													"
			+ "	(SELECT 1 FROM                                                  "
			+ "		T_WORKSHEET_TEMPLATES TEMPLATE                              "
			+ "	WHERE TEMPLATE.COL_WORKSHEET_TEMPLATE_ID = ? AND                "
			+ "	( (TEMPLATE.COL_NO_COLUMN >= 1 AND TEMPLATE.COL_NO_ROW >=2)     "
			+ "		OR                                                          "
			+ "	(TEMPLATE.COL_NO_COLUMN >= 2 AND TEMPLATE.COL_NO_ROW >=1)       "
			+ "	)) IS NOT NULL AS RETURN_CODE                                   ";

	/** The Constant GET_WORKSHEET_TEMPLATE_VERSION. */
	private static final String GET_WORKSHEET_TEMPLATE_VERSION = " SELECT IFNULL((SELECT MAX(COL_VERSION) FROM T_WORKSHEET_TEMPLATES "
			+ "WHERE COL_NAME = ? AND COL_IS_PUBLISHED = true GROUP BY COL_NAME ),-1) RETURN_VALUE ";

	/** The Constant SET_CLONABLE_FALSE_T_WORKSHEET_PUBLICATION. */
	private static final String SET_CLONABLE_FALSE_T_WORKSHEET_PUBLICATION = "UPDATE T_WORKSHEET_TEMPLATE_PUBLICATIONS TEMPLATE SET TEMPLATE.COL_IS_CLONAMBLE = FALSE "
			+ " WHERE TEMPLATE.COl_PUBLISHED_WORKSHEET_TEMPLATE_ID = "
			+ " (SELECT COL_WORKSHEET_TEMPLATE_ID "
			+ " FROM T_WORKSHEET_TEMPLATES "
			+ " WHERE COL_NAME = ? AND COL_IS_PUBLISHED = TRUE AND COL_VERSION = ? ) ";

	/** The Constant DELETE_PREV_VERSION. */
	private static final String DELETE_PREV_VERSION = "DELETE FROM T_WORKSHEET_TEMPLATE_PUBLICATIONS	"
			+ "	WHERE T_WORKSHEET_TEMPLATE_PUBLICATIONS.COL_PUBLISHED_WORKSHEET_TEMPLATE_ID =           "
			+ "		(SELECT COL_WORKSHEET_TEMPLATE_ID                                                   "
			+ "			FROM T_WORKSHEET_TEMPLATES                                                      "
			+ "	 		WHERE COL_NAME = ?							                                    "
			+ "	 		AND COL_IS_PUBLISHED = TRUE AND COL_VERSION = ? )                               "
			+ "AND T_WORKSHEET_TEMPLATE_PUBLICATIONS.COL_USAGE_INDICATOR = 0 AND                        "
			+ "	(SELECT 1 AND                                                                           "
			+ "	(SELECT FALSE                                                                           "
			+ "   FROM T_REFERENCED_WORKSHEET_TEMPLATES                                                 "
			+ "	WHERE T_REFERENCED_WORKSHEET_TEMPLATES.COL_PUBLISHED_SINGLE_WORKSHEET_TEMPLATE_ID       "
			+ "	= T_WORKSHEET_TEMPLATE_PUBLICATIONS.COL_PUBLISHED_WORKSHEET_TEMPLATE_ID) IS NULL)       ";

	/** The Constant ASSOCIATION_TEMPLATE_PUBLICATIONS. */
	private static final String ASSOCIATION_TEMPLATE_PUBLICATIONS = " INSERT INTO T_WORKSHEET_TEMPLATE_PUBLICATIONS 			"
			+ " (																					"
			+ "		SELECT ? ,T_PLANT_SITES.COL_PLANT_SITE_ID ID,0 ,? 								"
			+ "		FROM T_PLANT_SITES																"
			+ "		WHERE T_PLANT_SITES.COL_PLANT_SITE_ID IN(										";

	/** The Constant ASSOCIATE_REFERENCED_IMAGE. */
	private static final String ASSOCIATE_REFERENCED_IMAGE = " INSERT INTO T_REFERENCED_IMAGE (COL_NAME,COL_PUBLISHED_WORKSHEEET_TEMPLATE_ID) VALUES ";

	/** The Constant INSERT_TEMPLATE_PUBLICTION. */
	private static final String INSERT_TEMPLATE_PUBLICTION = " INSERT INTO T_WORKSHEET_TEMPLATE_PUBLICATIONS "
			+ " (COL_PUBLISHED_WORKSHEET_TEMPLATE_ID,COL_PLANT_SITE_ID,COL_IS_CLONAMBLE,COL_USAGE_INDICATOR) "
			+ " VALUES ";

	/** The Constant SELECT_WORKSHEET_ID_AND_PLANT_ID_ONE. */
	private static final String SELECT_WORKSHEET_ID_AND_PLANT_ID_ONE = " SELECT WS.COL_WORKSHEET_TEMPLATE_ID,WS.COL_PLANT_SITE_ID FROM T_WORKSHEET_TEMPLATES WS, "
			+ " (SELECT COL_WORKSHEET_TEMPLATE_ID PUBIDS FROM T_WORKSHEET_TEMPLATES WHERE COL_WORKSHEET_TEMPLATE_ID "
			+ " IN ( ";

	/** The Constant SELECT_WORKSHEET_ID_AND_PLANT_ID_TWO. */
	private static final String SELECT_WORKSHEET_ID_AND_PLANT_ID_TWO = " )) INPUT_WS "
			+ " WHERE WS.COL_IS_PUBLISHED = TRUE AND WS.COL_PLANT_SITE_ID IS NOT NULL "
			+ " AND INPUT_WS.PUBIDS = WS.COL_WORKSHEET_TEMPLATE_ID ";

	/** The Constant UPDATE_WORKSHEET_PUBLICTIONS. */
	private static final String UPDATE_WORKSHEET_PUBLICTIONS = " UPDATE T_WORKSHEET_TEMPLATE_PUBLICATIONS SET COL_IS_CLONAMBLE = ? WHERE "
			+ " COL_PUBLISHED_WORKSHEET_TEMPLATE_ID IN( ";

	/** The Constant DELETE_WORKSHEET_PUBLICTIONS. */
	private static final String DELETE_WORKSHEET_PUBLICTIONS = " DELETE FROM T_WORKSHEET_TEMPLATE_PUBLICATIONS WHERE COL_PUBLISHED_WORKSHEET_TEMPLATE_ID IN( ";

	/** The Constant USER_HAS_DUBLICATE_RIGHTS. */
	private static final String USER_HAS_DUBLICATE_RIGHTS = " SELECT 1 AND ( "
			+ " SELECT 1 FROM T_WORKSHEET_TEMPLATE_PUBLICATIONS						"
			+ " WHERE COL_IS_CLONAMBLE = TRUE AND									"
			+ " COL_PUBLISHED_WORKSHEET_TEMPLATE_ID = ? AND COL_PLANT_SITE_ID = ?	"
			+ " LIMIT 1) IS NOT NULL AS RETURN_CODE									";

	/** The Constant DUBLICATE_UPDATE_WORKSHEET_PUBLICATIONS. */
	private static final String DUBLICATE_UPDATE_WORKSHEET_PUBLICATIONS = " UPDATE T_WORKSHEET_TEMPLATE_PUBLICATIONS SET COL_IS_CLONAMBLE = FALSE "
			+ " WHERE COL_PUBLISHED_WORKSHEET_TEMPLATE_ID = ? AND COL_PLANT_SITE_ID = ? ";

	/** The Constant PUBLISHED_WORKSHEET_COUNT. */
	private static final String PUBLISHED_WORKSHEET_COUNT = " SELECT "
			+ " FOUND_ROWS() ";

	
	private static final String PUBLISHED_PLANT_SITE_IDS = "SELECT COL_PLANT_SITE_ID FROM T_WORKSHEET_TEMPLATE_PUBLICATIONS WHERE COL_PUBLISHED_WORKSHEET_TEMPLATE_ID = ? ";
	
	private static final String PUBLISHED_PLANT_SITE_IDS_COUNT = "SELECT COUNT(*) FROM T_WORKSHEET_TEMPLATE_PUBLICATIONS WHERE COL_PUBLISHED_WORKSHEET_TEMPLATE_ID = ? ";
	
	private static final String PUBLISHED_WORKSHEET_TEMPLATE_ID = "SELECT COL_PUBLISHED_SINGLE_WORKSHEET_TEMPLATE_ID FROM T_REFERENCED_WORKSHEET_TEMPLATES WHERE COL_DRAFT_MULTIPLE_WORKSHEET_TEMPLATE_ID = ? LIMIT 1 ";
	
	/**
	 * Checks if is work sheet temaplte exists.
	 * 
	 * @param workSheetTemplateId the work sheet template id
	 * 
	 * @return true, if is work sheet temaplte exists
	 * 
	 * @throws Exception the exception
	 */
	public static boolean isWorkSheetTemaplteExists(int workSheetTemplateId)
			throws Exception {
		LOGGER.info("WorkSheetTemapleDAO.IS_WORKSHEET_EXISTS_ALL :: "
				+ WorkSheetTemapleDAO.IS_WORKSHEET_EXISTS_ALL);
		boolean isExists = false;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(WorkSheetTemapleDAO.IS_WORKSHEET_EXISTS_ALL);
			pstmt.setInt(1, workSheetTemplateId);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				isExists = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.info("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getMessage());

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		LOGGER.info("isWorkSheetTemaplteExists " + isExists);
		return isExists;
	}

	/**
	 * Checks if is work sheet temaplte exists by specific.
	 * 
	 * @param workSheetTemplateId the work sheet template id
	 * @param status the status
	 * 
	 * @return true, if is work sheet temaplte exists by specific
	 * 
	 * @throws Exception the exception
	 */
	public static boolean isWorkSheetTemaplteExistsBySpecific(
			int workSheetTemplateId, String status) throws Exception {
		LOGGER.info("WorkSheetTemapleDAO.IS_WORKSHEET_EXISTS_BY_SPECIFIC :: "
				+ WorkSheetTemapleDAO.IS_WORKSHEET_EXISTS_BY_SPECIFIC);
		boolean isExists = false;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(WorkSheetTemapleDAO.IS_WORKSHEET_EXISTS_BY_SPECIFIC);
			pstmt.setInt(1, workSheetTemplateId);

			if (Constants.STATUS_DRAFT.equals(status)) {
				pstmt.setBoolean(2, false);
			} else if (Constants.STATUS_PUBLISH.equals(status)) {
				pstmt.setBoolean(2, true);
			}

			rst = pstmt.executeQuery();
			if (rst.next()) {
				isExists = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.info("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getMessage());

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		LOGGER.info("isWorkSheetTemaplteExists " + isExists);
		return isExists;
	}

	/**
	 * Gets the work sheet template.
	 * 
	 * @param id the id
	 * 
	 * @return the work sheet template
	 * 
	 * @throws Exception the exception
	 */
	public static WorkSheetTemplate getWorkSheetTemplate(int id,BooleanParam withPreview)
			throws Exception {

		LOGGER.info("request getWorkSheetTemplate value " + id + ",BooleanParam "+withPreview.getValue());
		LOGGER.info("WorkSheetTemapleDAO.GET_WORKSHEET_TEMAPLTE_BY_ID :: "
				+ WorkSheetTemapleDAO.GET_WORKSHEET_TEMAPLTE_BY_ID);

		int workSheetTemplateId = id;
		ResultSet rst = null;
		PreparedStatement pstmt = null;

		WorkSheetTemplate workSheetTemplate = null;
		PlantSite plantSite = new PlantSite();
		Connection connection = null;

		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(withPreview.getValue() == 1 ? WorkSheetTemapleDAO.GET_WORKSHEET_TEMAPLTE_BY_ID_PREIVIEW_ON
							: WorkSheetTemapleDAO.GET_WORKSHEET_TEMAPLTE_BY_ID);
			pstmt.setInt(1, workSheetTemplateId);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				workSheetTemplate = new WorkSheetTemplate();
				workSheetTemplate
						.setId(rst.getInt("COL_WORKSHEET_TEMPLATE_ID"));
				workSheetTemplate.setName(rst.getString("COL_NAME"));
				workSheetTemplate.setVersion(rst.getInt("COL_VERSION"));
				workSheetTemplate.setType(PublishedTemplateType.fromValue(rst.getInt("COL_TEMPLATE_TYPE")));
				workSheetTemplate.setPublished(rst
						.getBoolean("COL_IS_PUBLISHED"));
				workSheetTemplate.setFormat(TemplateFormat.fromValue(rst.getInt("COL_FORMAT")));
				workSheetTemplate.setHeight(rst.getInt("COL_HEIGHT"));
				workSheetTemplate.setWidth(rst.getInt("COL_WIDTH"));
				workSheetTemplate.setOrientation(TemplateOrientation.fromValue(rst.getInt("COL_ORIENTATION")));
				workSheetTemplate.setAuthor(rst.getString("COL_AUTHOR"));
				Calendar cal = Calendar.getInstance();
				Timestamp timeStamp = rst.getTimestamp("COL_UPDATE_TIME");
				cal.setTime(timeStamp);
				workSheetTemplate.setUpdateTime(cal);
				workSheetTemplate.setXmlDescription(rst
						.getBytes("COL_XML_DESCRIPTION"));
				workSheetTemplate
						.setNumberOfColumn(rst.getInt("COL_NO_COLUMN"));
				workSheetTemplate.setNumberOfRow(rst.getInt("COL_NO_ROW"));
				workSheetTemplate.setTopMargin(rst.getInt("COL_TOP_MARGIN"));
				workSheetTemplate.setLeftMargin(rst.getInt("COL_LEFT_MARGIN"));
				workSheetTemplate.setVerticalSpacing(rst
						.getInt("COL_VERTICAL_SPACING"));
				workSheetTemplate.setHorizontalSpacing(rst
						.getInt("COL_HORIZONTAL_SPACING"));
				workSheetTemplate.setPropagationType(MultiTemplateDistributionDire.fromValue(rst
						.getInt("COL_PROPAGATION_TYPE")) );				
				if (withPreview.getValue() == 1) {
					workSheetTemplate.setPreview(rst.getBytes("COL_PREVIEW"));
				}				
				int plantSiteId = rst.getInt("COL_PLANT_SITE_ID");
				plantSite.setId(plantSiteId == 0 ? -1 : plantSiteId);
				workSheetTemplate.setPlantSite(plantSite);
			}
			setPlantSitesAndPublicWorkSheetId(workSheetTemplate, connection);
		} catch (SQLException e) {
			LOGGER.info("SQLException " + e.getMessage());
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			LOGGER.info("Exception " + e.getMessage());
			throw new MessageErrorUnknownException();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}

		return workSheetTemplate;
	}

	/**
	 * Gets the work sheet template.
	 * 
	 * @param templateId the template id
	 * @param userId the user id
	 * 
	 * @return the work sheet template
	 * 
	 * @throws Exception the exception
	 */
	public static WorkSheetTemplate getWorkSheetTemplate(int templateId,
			int userId) throws Exception {

		LOGGER.info("request workSheetTemplateId value " + templateId);
		LOGGER.info("request User Id value " + userId);
		LOGGER.info("WorkSheetTemapleDAO.GET_WORKSHEET_TEMAPLTE_BY_ID :: "
				+ WorkSheetTemapleDAO.GET_WORKSHEET_TEMAPLTE_BY_ID_AND_USER_ID);

		int requestWorkSheetTemplateId = templateId;

		ResultSet rst = null;
		PreparedStatement pstmt = null;

		WorkSheetTemplate workSheetTemplate = null;
		PlantSite plantSite = new PlantSite();
		Connection connection = null;

		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(WorkSheetTemapleDAO.GET_WORKSHEET_TEMAPLTE_BY_ID_AND_USER_ID);
			pstmt.setInt(1, requestWorkSheetTemplateId);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				workSheetTemplate = new WorkSheetTemplate();
				workSheetTemplate
						.setId(rst.getInt("COL_WORKSHEET_TEMPLATE_ID"));
				workSheetTemplate.setName(rst.getString("COL_NAME"));
				workSheetTemplate.setVersion(rst.getInt("COL_VERSION"));
				workSheetTemplate.setType(PublishedTemplateType.fromValue(rst.getInt("COL_TEMPLATE_TYPE")));
				workSheetTemplate.setPublished(rst
						.getBoolean("COL_IS_PUBLISHED"));
				workSheetTemplate.setFormat(TemplateFormat.fromValue(rst.getInt("COL_FORMAT")));
				workSheetTemplate.setHeight(rst.getInt("COL_HEIGHT"));
				workSheetTemplate.setWidth(rst.getInt("COL_WIDTH"));
				workSheetTemplate.setOrientation(TemplateOrientation.fromValue(rst.getInt("COL_ORIENTATION")));
				workSheetTemplate.setAuthor(rst.getString("COL_AUTHOR"));
				Calendar cal = Calendar.getInstance();
				Timestamp timeStamp = rst.getTimestamp("COL_UPDATE_TIME");
				cal.setTime(timeStamp);
				workSheetTemplate.setUpdateTime(cal);
				workSheetTemplate.setUpdateTime(cal);
				workSheetTemplate.setXmlDescription(rst
						.getBytes("COL_XML_DESCRIPTION"));
				workSheetTemplate
						.setNumberOfColumn(rst.getInt("COL_NO_COLUMN"));
				workSheetTemplate.setNumberOfRow(rst.getInt("COL_NO_ROW"));
				workSheetTemplate.setTopMargin(rst.getInt("COL_TOP_MARGIN"));
				workSheetTemplate.setLeftMargin(rst.getInt("COL_LEFT_MARGIN"));
				workSheetTemplate.setVerticalSpacing(rst
						.getInt("COL_VERTICAL_SPACING"));
				workSheetTemplate.setHorizontalSpacing(rst
						.getInt("COL_HORIZONTAL_SPACING"));
				workSheetTemplate.setPropagationType(MultiTemplateDistributionDire.fromValue(rst
						.getInt("COL_PROPAGATION_TYPE")));
				int plantSiteId = rst.getInt("COL_PLANT_SITE_ID");
				plantSite.setId(plantSiteId == 0 ? -1 : plantSiteId);
				workSheetTemplate.setPlantSite(plantSite);
			}
		} catch (SQLException e) {
			LOGGER.info("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getMessage());

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}

		return workSheetTemplate;
	}

	/**
	 * Checks if is user has rights in template.
	 * 
	 * @param user the user
	 * @param templateId the template id
	 * 
	 * @return true, if is user has rights in template
	 * 
	 * @throws Exception the exception
	 */
	public static boolean isUserHasRightsInTemplate(SWordUser user,
			int templateId) throws Exception {
		SWordUser swordUser = user;
		int requestWorkSheetTemplateId = templateId;
		boolean isUserHasRights = false;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(swordUser.getProfile().getValue() == Constants.LOCAL_MANAGER ? WorkSheetTemapleDAO.IS_LOCAL_MANAGER_HAVE_ACCESS_RIGHTS
							: WorkSheetTemapleDAO.IS_VIEWER_HAVE_ACCESS_RIGHTS);
			pstmt.setInt(1, requestWorkSheetTemplateId);
			pstmt.setInt(2, swordUser.getId());			
			rst = pstmt.executeQuery();
			if (rst.next()) {
				isUserHasRights = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.info("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getMessage());

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		LOGGER.info("isUserHasRights " + isUserHasRights);
		return isUserHasRights;
	}

	/**
	 * Checks if is work sheet temaplte name exists by specific.
	 * 
	 * @param status the status
	 * @param templateName the template name
	 * 
	 * @return true, if is work sheet temaplte name exists by specific
	 * 
	 * @throws Exception the exception
	 */
	public static boolean isWorkSheetTemaplteNameExistsBySpecific(
			String status, String templateName) throws Exception {
		LOGGER
				.info("WorkSheetTemapleDAO.IS_WORKSHEET_NAME_EXISTS_BY_SPECIFIC :: "
						+ WorkSheetTemapleDAO.IS_WORKSHEET_NAME_EXISTS_BY_SPECIFIC);
		boolean isExists = false;
		String toBeRename = templateName;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(WorkSheetTemapleDAO.IS_WORKSHEET_NAME_EXISTS_BY_SPECIFIC);

			pstmt.setString(1, toBeRename);

			if (Constants.STATUS_DRAFT.equals(status)) {
				pstmt.setBoolean(2, false);
			} else if (Constants.STATUS_PUBLISH.equals(status)) {
				pstmt.setBoolean(2, true);
			}

			rst = pstmt.executeQuery();
			if (rst.next()) {
				isExists = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.info("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getMessage());

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		LOGGER.info("isWorkSheetTemaplteExists " + isExists);
		return isExists;
	}

	/**
	 * Gets the work sheet temaplte name.
	 * 
	 * @param status the status
	 * @param id the id
	 * 
	 * @return the work sheet temaplte name
	 * 
	 * @throws Exception the exception
	 */
	public static String getWorkSheetTemaplteName(String status, int id)
			throws Exception {
		LOGGER.info("WorkSheetTemapleDAO.GET_WORKSHEET_NAME_BY_ID :: "
				+ WorkSheetTemapleDAO.GET_WORKSHEET_NAME_BY_ID);
		boolean isExists = false;
		int workSheetTemplateId = id;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		String templateName = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(WorkSheetTemapleDAO.GET_WORKSHEET_NAME_BY_ID);

			pstmt.setInt(1, workSheetTemplateId);

			if (Constants.STATUS_DRAFT.equals(status)) {
				pstmt.setBoolean(2, false);
			} else if (Constants.STATUS_PUBLISH.equals(status)) {
				pstmt.setBoolean(2, true);
			}

			rst = pstmt.executeQuery();
			if (rst.next()) {
				templateName = rst.getString("TEMAPLATE.COL_NAME");
			}
		} catch (SQLException e) {
			LOGGER.info("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.info("Exception " + e.getMessage());

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		LOGGER.info("isWorkSheetTemaplteExists " + isExists);
		return templateName;
	}

	/**
	 * Rename draft worksheet template.
	 * 
	 * @param templateName the template name
	 * @param templateId the template id
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public static boolean renameDraftWorksheetTemplate(String templateName,
			int templateId) throws Exception {

		String reNameValue = templateName;
		int workSheetTemplateId = templateId;

		LOGGER.info("WorkSheetTemapleDAO.RENAME_WORKSHEET :: "
				+ WorkSheetTemapleDAO.RENAME_WORKSHEET);
		boolean isRenamed = false;

		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			connection.setAutoCommit(false);
			int updatedRow = DBUtil.getInstance().executeUpdate(
					WorkSheetTemapleDAO.RENAME_WORKSHEET,
					new Object[] { reNameValue, workSheetTemplateId },
					connection);
			isRenamed = updatedRow > 0;
			connection.commit();
		} catch (Exception e) {
			DBUtil.getInstance().getConnection().rollback();
			LOGGER.info("Exception " + e.getMessage());
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		return isRenamed;
	}

	/**
	 * Delete all work sheet templates.
	 * 
	 * @param workSheetTemplateIds the work sheet template ids
	 * @param userId the user id
	 * @param connection the connection
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public static boolean deleteAllWorkSheetTemplates(
			int[] workSheetTemplateIds, int userId, Connection connection)
			throws Exception {
		LOGGER.info("WorkSheetTemapleDAO.DELETE_ALL_WORKSHEET_TEMPLATES ");
		String deleteWorkSheetTemplate = null;
		boolean isWorkSheetDeleted = false;
		boolean isAllTemplateIdExists = true;
		Statement st = null;
		try {
			for (int i = 0; i < workSheetTemplateIds.length; i++) {
				if (workSheetTemplateIds[i] > 0) {
					boolean isWorkSheetTemplateExists = WorkSheetTemapleDAO
							.isWorkSheetTemaplteExists(workSheetTemplateIds[i]);
					if (!isWorkSheetTemplateExists) {
						isAllTemplateIdExists = false;
						break;
					}
				}
			}
			if (isAllTemplateIdExists) {
				deleteWorkSheetTemplate = deleteWorkSheetTemplateQuery(workSheetTemplateIds);

				st = connection.createStatement();
				LOGGER
						.info("WorkSheetTemapleDAO.DELETE_ALL_WORKSHEET_TEMPLATES_QUERY "
								+ deleteWorkSheetTemplate);
				int noOfRowDeleted = st.executeUpdate(deleteWorkSheetTemplate);
				if (noOfRowDeleted > 0) {
					isWorkSheetDeleted = true;
				}
			}
		} catch (Exception e) {
			LOGGER.info("SQLException " + e.getMessage());
		} finally {
			/*
			 * Do not Close the Connection.
			 */
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception uncatched) {
				LOGGER.info("SQLException " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		LOGGER.info("isWorkSheetDeleted " + isWorkSheetDeleted);
		return isWorkSheetDeleted;
	}

	/**
	 * Delete all work sheet templates.
	 * 
	 * @param workSheetTemplateIds the work sheet template ids
	 * @param userId the user id
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public static boolean deleteAllWorkSheetTemplates(
			int[] workSheetTemplateIds, int userId) throws Exception {
		boolean isWorkSheetDeleted = false;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			connection.setAutoCommit(false);
			isWorkSheetDeleted = WorkSheetTemapleDAO
					.deleteAllWorkSheetTemplates(workSheetTemplateIds, userId,
							connection);
			connection.commit();
		} catch (Exception e) {
			LOGGER.info("SQLException " + e.getMessage());
			connection.rollback();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception uncatched) {
				LOGGER.info("SQLException " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}

		return isWorkSheetDeleted;

	}

	/**
	 * Delete private draft work sheet templates.
	 * 
	 * @param workSheetTemplateIds the work sheet template ids
	 * @param userId the user id
	 * @param conn the conn
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public static boolean deletePrivateDraftWorkSheetTemplates(
			int[] workSheetTemplateIds, int userId, Connection conn)
			throws Exception {

		LOGGER
				.info("WorkSheetTemapleDAO.DELETE_PRIVATE_DRAFT_WORKSHEET_TEMPLATES "
						+ WorkSheetTemapleDAO.DELETE_PRIVATE_DRAFT_WORKSHEET_TEMPLATES);
		boolean isWorkSheetDeleted = false;
		boolean isAllTemplateIdExists = true;
		int rowsDeleted = 0;
		PreparedStatement pst = null;
		Connection connection = conn;
		try {
			/* To check whether the WorkSheet Id Exist */

			for (int i = 0; i < workSheetTemplateIds.length; i++) {
				if (workSheetTemplateIds[i] > 0) {
					boolean isWorkSheetTemplateExists = WorkSheetTemapleDAO
							.isWorkSheetTemaplteExists(workSheetTemplateIds[i]);
					if (!isWorkSheetTemplateExists) {
						isAllTemplateIdExists = false;
						break;
					}
				}
			}
			if (isAllTemplateIdExists) {
				// connection = DBUtil.getInstance().getConnection();
				// connection.setAutoCommit(false);
				pst = connection
						.prepareStatement(WorkSheetTemapleDAO.DELETE_PRIVATE_DRAFT_WORKSHEET_TEMPLATES);
				pst.setInt(1, userId);
				for (int i = 0; i < workSheetTemplateIds.length; i++) {
					if (workSheetTemplateIds[i] > 0) {
						pst
								.setInt(2, (workSheetTemplateIds[i]));
					}
					int noOfRowsDeleted = pst.executeUpdate();
					if (noOfRowsDeleted > 0) {
						rowsDeleted++;
					} else {
						LOGGER.info(" Id not having Permission to delete "
								+ workSheetTemplateIds[i]);
						connection.rollback();
						rowsDeleted = 0;
						break;
					}
				}
				if (rowsDeleted > 0) {
					isWorkSheetDeleted = true;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.info("SQLException " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		LOGGER.info("isWorkSheetDeleted " + isWorkSheetDeleted);
		return isWorkSheetDeleted;
	}

	/**
	 * Delete private draft work sheet templates.
	 * 
	 * @param workSheetTemplateIds the work sheet template ids
	 * @param userId the user id
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public static boolean deletePrivateDraftWorkSheetTemplates(
			int[] workSheetTemplateIds, int userId) throws Exception {
		boolean isWorkSheetDeleted = false;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			connection.setAutoCommit(false);
			isWorkSheetDeleted = WorkSheetTemapleDAO
					.deletePrivateDraftWorkSheetTemplates(workSheetTemplateIds,
							userId, connection);
			connection.commit();
		} catch (Exception e) {
			LOGGER.info("SQLException " + e.getMessage());
			DBUtil.getInstance().getConnection().rollback();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception uncatched) {
				LOGGER.info("SQLException " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}

		return isWorkSheetDeleted;

	}

	/**
	 * Delete work sheet template query.
	 * 
	 * @param workSheetTemplateIds the work sheet template ids
	 * 
	 * @return the string
	 */
	public static String deleteWorkSheetTemplateQuery(
			int[] workSheetTemplateIds) {

		StringBuffer deleteWorkSheetTemplate = new StringBuffer();
		deleteWorkSheetTemplate.append("DELETE FROM T_WORKSHEET_TEMPLATES ");
		deleteWorkSheetTemplate.append(" WHERE ");
		deleteWorkSheetTemplate
				.append(" T_WORKSHEET_TEMPLATES.COL_WORKSHEET_TEMPLATE_ID IN (");

		for (int i = 0; i < workSheetTemplateIds.length; i++) {
			if (i != 0) {
				deleteWorkSheetTemplate.append(",");
			}
			deleteWorkSheetTemplate.append(workSheetTemplateIds[i]);
		}
		deleteWorkSheetTemplate.append(")");
		return deleteWorkSheetTemplate.toString();
	}



	/**
	 * Creates the work sheet template.
	 * 
	 * @param forceDeletion the force deletion
	 * @param workSheetTemplate the work sheet template
	 * @param macroObjectList the macro object list
	 * @param publishedWorksheetTemplateId the published worksheet template id
	 * @param userId the user id
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	public static int createWorkSheetTemplate(boolean forceDeletion,
			WorkSheetTemplate workSheetTemplate, int[] macroObjectList,
			int publishedWorksheetTemplateId, int userId) throws Exception {
		boolean wForceDeletion = forceDeletion;
		int[] wMacroObjectsList = macroObjectList;
		WorkSheetTemplate wWorkSheetTemplate = workSheetTemplate;
		int wPublishedWorksheetTemplateId = publishedWorksheetTemplateId;
		int wUserId = userId;
		PreparedStatement pstmt = null;
		int newWorksheetTemplateId = -1;
		Connection conn = null;
		try {
			conn = DBUtil.getInstance().getConnection();
			conn.setAutoCommit(false);
			if (wForceDeletion) {
				int existingWorkSheetID = DBUtil.getInstance().getRowCount(
						GET_WORKSHEET_ID_BY_NAME,
						new Object[] { wWorkSheetTemplate.getName(), false },
						conn);
				deleteAllWorkSheetTemplates(new int[] { 
						existingWorkSheetID }, wUserId, conn);
			}
			pstmt = conn.prepareStatement(INSERT_WORKSHEET_TEMPALTE);
			int plantSiteId = wWorkSheetTemplate.getPlantSite() == null ? -1
					: wWorkSheetTemplate.getPlantSite().getId() == 0 ? -1
							: wWorkSheetTemplate.getPlantSite().getId();
			pstmt.setString(1, wWorkSheetTemplate.getName());
			pstmt.setInt(2, wWorkSheetTemplate.getVersion());
			pstmt.setBoolean(3, wWorkSheetTemplate.isPublished());
			pstmt.setInt(4, wWorkSheetTemplate.getFormat().getValue());
			pstmt.setInt(5, wWorkSheetTemplate.getWidth());
			pstmt.setInt(6, wWorkSheetTemplate.getHeight());
			pstmt.setInt(7, wWorkSheetTemplate.getOrientation().getValue());
			pstmt.setString(8, wWorkSheetTemplate.getAuthor());
			pstmt.setBytes(9, wWorkSheetTemplate.getXmlDescription());
			pstmt.setInt(10, wWorkSheetTemplate.getNumberOfColumn());
			pstmt.setInt(11, wWorkSheetTemplate.getNumberOfRow());
			pstmt.setInt(12, wWorkSheetTemplate.getTopMargin());
			pstmt.setInt(13, wWorkSheetTemplate.getLeftMargin());
			pstmt.setInt(14, wWorkSheetTemplate.getVerticalSpacing());
			pstmt.setInt(15, wWorkSheetTemplate.getHorizontalSpacing());
			pstmt.setInt(16, wWorkSheetTemplate.getPropagationType().getValue());
			pstmt.setString(17, plantSiteId == -1 ? null : String
					.valueOf(plantSiteId));
			pstmt.setInt(18, wWorkSheetTemplate.getType().getValue());
			pstmt.executeUpdate();
			newWorksheetTemplateId = Integer.parseInt(DBUtil.getInstance()
					.getValue(
							GET_WORKSHEET_TEMAPLTE_ID,
							new Object[] { wWorkSheetTemplate.getName(),
									wWorkSheetTemplate.getVersion() }, conn));
			if (wMacroObjectsList != null && wMacroObjectsList.length > 0) {
				Object[] parameters = new Object[2];
				for (int i = 0; i < wMacroObjectsList.length; i++) {
					parameters[0] = newWorksheetTemplateId;
					parameters[1] = wMacroObjectsList[i];
					DBUtil.getInstance().executeUpdate(
							INSERT_MACRO_OBJECTS_USAGE_STAT, parameters, conn);
				}
			}
			if (wPublishedWorksheetTemplateId != -1) {
				DBUtil.getInstance().executeUpdate(
						INSERT_REFERENCED_WORKSHEET_TEMPLATE,
						new Object[] { newWorksheetTemplateId,
								wPublishedWorksheetTemplateId }, conn);
			}
			conn.commit();
		} catch (SQLException e) {
			LOGGER.error("Exception " + e.getMessage());
			conn.rollback();
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());
			conn.rollback();
			throw new MessageErrorUnknownException();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		return newWorksheetTemplateId;
	}

	/**
	 * Update work sheet template.
	 * 
	 * @param workSheetTemplate the work sheet template
	 * @param macroObjectList the macro object list
	 * @param publishedWorksheetTemplateId the published worksheet template id
	 * @param IPN the iPN
	 * 
	 * @return true, if successful
	 * 
	 * @throws MessageErrorUnknownException the message error unknown exception
	 * @throws Exception the exception
	 */
	public static boolean updateWorkSheetTemplate(
			WorkSheetTemplate workSheetTemplate, int[] macroObjectList,
			int publishedWorksheetTemplateId, String IPN)
			throws MessageErrorUnknownException, Exception {
		int[] wMacroObjectsList = macroObjectList;
		WorkSheetTemplate wWorkSheetTemplate = workSheetTemplate;
		int wPublishedWorksheetTemplateId = publishedWorksheetTemplateId;
		String wIPN = IPN;
		PreparedStatement pstmt = null;
		int updatedRows = 0;
		Connection conn = null;
		boolean isWorkSheetUpdated = true;
		try {
			conn = DBUtil.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			updatedRows = DBUtil.getInstance().executeUpdate(
					DELETE_MACRO_OBJECTS_USAGE_STAT,
					new Object[] { wWorkSheetTemplate.getId() }, conn);
			
			LOGGER.debug("Delete ROWS DELETE_MACRO_OBJECTS_USAGE_STAT : "
					+ updatedRows);
			
			DBUtil.getInstance().executeUpdate(
					WorkSheetTemapleDAO.DELETE_REFERENCED_WORKSHEET_TEMPLATE,
					new Object[] { workSheetTemplate.getId() }, conn);

			pstmt = conn
					.prepareStatement(WorkSheetTemapleDAO.UPDATE_WORKSHEET_TEMPLATE);

			int plantSiteId = wWorkSheetTemplate.getPlantSite() == null ? -1
					: wWorkSheetTemplate.getPlantSite().getId() == 0 ? -1
							: wWorkSheetTemplate.getPlantSite().getId();

			pstmt.setString(1, wWorkSheetTemplate.getName());
			pstmt.setInt(2, wWorkSheetTemplate.getVersion());
			pstmt.setBoolean(3, wWorkSheetTemplate.isPublished());
			pstmt.setInt(4, wWorkSheetTemplate.getFormat().getValue());
			pstmt.setInt(5, wWorkSheetTemplate.getWidth());
			pstmt.setInt(6, wWorkSheetTemplate.getHeight());
			pstmt.setInt(7, wWorkSheetTemplate.getOrientation().getValue());
			pstmt.setString(8, wIPN);
			pstmt.setBytes(9, wWorkSheetTemplate.getXmlDescription());
			pstmt.setInt(10, wWorkSheetTemplate.getNumberOfColumn());
			pstmt.setInt(11, wWorkSheetTemplate.getNumberOfRow());
			pstmt.setInt(12, wWorkSheetTemplate.getTopMargin());
			pstmt.setInt(13, wWorkSheetTemplate.getLeftMargin());
			pstmt.setInt(14, wWorkSheetTemplate.getVerticalSpacing());
			pstmt.setInt(15, wWorkSheetTemplate.getHorizontalSpacing());
			pstmt.setInt(16, wWorkSheetTemplate.getPropagationType().getValue());
			pstmt.setString(17, plantSiteId == -1 ? null : String
					.valueOf(plantSiteId));
			pstmt.setInt(18, wWorkSheetTemplate.getType().getValue());
			pstmt.setBytes(19, workSheetTemplate.getPreview());
			pstmt.setInt(20, wWorkSheetTemplate.getId());

			updatedRows = pstmt.executeUpdate();

			if (wPublishedWorksheetTemplateId != -1) {
				DBUtil.getInstance().executeUpdate(
						INSERT_REFERENCED_WORKSHEET_TEMPLATE,
						new Object[] { wWorkSheetTemplate.getId(),
								wPublishedWorksheetTemplateId }, conn);
			}
			
			if (macroObjectList != null) {
				Object[] parameters = new Object[2];
				for (int i = 0; i < wMacroObjectsList.length; i++) {
					parameters[0] = wWorkSheetTemplate.getId();
					parameters[1] = wMacroObjectsList[i];
					DBUtil.getInstance().executeUpdate(
							INSERT_MACRO_OBJECTS_USAGE_STAT, parameters, conn);
				}
			}
			
			conn.commit();
		} catch (SQLException e) {
			LOGGER.error("Exception " + e.getMessage());
			isWorkSheetUpdated = false;
			conn.rollback();
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());
			isWorkSheetUpdated = false;
			conn.rollback();
			throw new MessageErrorUnknownException();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		return isWorkSheetUpdated;
	}

	/**
	 * Checks if is work sheet as mulitple.
	 * 
	 * @param id the id
	 * 
	 * @return true, if is work sheet as mulitple
	 * 
	 * @throws MessageErrorUnknownException the message error unknown exception
	 */
	public static boolean isWorkSheetAsMulitple(int id)
			throws MessageErrorUnknownException {
		LOGGER
				.debug("WorkSheetTemapleDAO.IS_WORKSHEET_TEMPLATE_IS_MULTIPLE :: "
						+ WorkSheetTemapleDAO.IS_WORKSHEET_TEMPLATE_IS_MULTIPLE);
		LOGGER.debug("Input Values :: " + id);
		boolean isWorkSheetAsMulitple = false;
		int workSheetTemplateId = id;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(WorkSheetTemapleDAO.IS_WORKSHEET_TEMPLATE_IS_MULTIPLE);
			pstmt.setInt(1, workSheetTemplateId);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				isWorkSheetAsMulitple = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e.getMessage());
			throw new MessageErrorUnknownException();

		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());
			throw new MessageErrorUnknownException();

		} finally {

			try {
				if (rst != null) {
					rst.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("SQLException " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}

		}
		LOGGER.info("isWorkSheetAsMulitple " + isWorkSheetAsMulitple);
		return isWorkSheetAsMulitple;
	}

	/**
	 * Publish worksheet template.
	 * 
	 * @param toBePublishTemplate the to be publish template
	 * @param publishPlantSiteIds the publish plant site ids
	 * @param isCloneablePublish the is cloneable publish
	 * @param publishImageNames the publish image names
	 * @param requestUserId the request user id
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public static boolean publishWorksheetTemplate(
			WorkSheetTemplate toBePublishTemplate, int[] publishPlantSiteIds,
			BooleanParam isCloneablePublish, String[] publishImageNames,
			int requestUserId) throws Exception {

		WorkSheetTemplate workSheetTemplate = toBePublishTemplate;
		int[] plantSiteIdList = publishPlantSiteIds;
		BooleanParam isCloneable = isCloneablePublish;
		String[] imageNameList = publishImageNames;

		Connection connection = null;
		ResultSet rs = null;
		Statement st = null;
		boolean isRowUpdated = false;

		try {
			connection = DBUtil.getInstance().getConnection();
			connection.setAutoCommit(false);
			boolean isDraftLocal = workSheetTemplate != null
					&& workSheetTemplate.isPublished() == false
					&& workSheetTemplate.getPlantSite() != null
					&& workSheetTemplate.getPlantSite().getId() != -1;

			if (isDraftLocal) {
				workSheetTemplate.setCloneable(false);
			}
			workSheetTemplate.setPublished(true);

			int prevoiusVersion = DBUtil.getInstance().getRowCount(
					WorkSheetTemapleDAO.GET_WORKSHEET_TEMPLATE_VERSION,
					new Object[] { workSheetTemplate.getName() }, connection);

			LOGGER.debug("Previous Version " + prevoiusVersion);

			workSheetTemplate.setVersion(prevoiusVersion + 1);

			int updatedRows = DBUtil
					.getInstance()
					.executeUpdate(
							WorkSheetTemapleDAO.SET_CLONABLE_FALSE_T_WORKSHEET_PUBLICATION,
							new Object[] { workSheetTemplate.getName(),
									prevoiusVersion }, connection);

			LOGGER.debug("updatedRows " + updatedRows);

			int deletedRows = DBUtil.getInstance()
					.executeUpdate(
							WorkSheetTemapleDAO.DELETE_PREV_VERSION,
							new Object[] { workSheetTemplate.getName(),
									prevoiusVersion }, connection);

			LOGGER.debug("deletedRows " + deletedRows);

			int publishedRows = updateWorkSheetTemplate(workSheetTemplate,
					connection);
			if (plantSiteIdList != null && plantSiteIdList.length != 0) {
				createAssociateWorkSheetTemplatePublication(workSheetTemplate
						.getId(), plantSiteIdList, isCloneable, connection);
			}
			if (imageNameList != null && imageNameList.length != 0) {
				createAssociateWorkSheetTemplateRefernceImages(
						workSheetTemplate.getId(), imageNameList, connection);
			}

			isRowUpdated = publishedRows > 0;
			connection.commit();
		} catch (SQLException e) {
			isRowUpdated = false;
			connection.rollback();
			LOGGER.error(Constants.ERROR + e.getMessage());
			throw new MessageErrorUnknownException(e.getMessage());
		} catch (Exception e) {
			isRowUpdated = false;
			connection.rollback();
			LOGGER.error(Constants.ERROR + e.getMessage());
			throw new MessageErrorUnknownException(String.valueOf(Constants.MSG_ERROR_UNKNOWN));
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (Exception e) {
				LOGGER.error(Constants.ERROR + e.getMessage());
				throw new MessageErrorUnknownException(String.valueOf(Constants.MSG_ERROR_UNKNOWN));
			}
		}
		return isRowUpdated;
	}

	/**
	 * Creates the associate work sheet template refernce images.
	 * 
	 * @param id the id
	 * @param imageNameList the image name list
	 * @param connection the connection
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	private static int createAssociateWorkSheetTemplateRefernceImages(int id,
			String[] imageNameList, Connection connection) throws Exception {
		StringBuffer query = new StringBuffer(
				WorkSheetTemapleDAO.ASSOCIATE_REFERENCED_IMAGE);
		String[] imageNames = imageNameList;
		Connection connec = connection;
		int worksheetTemplateID = id;
		for (int i = 0; i < imageNames.length; i++) {
			query.append("( '" + imageNames[i] + "'," + worksheetTemplateID
					+ ") ");
			if (i < imageNames.length - 1) {
				query.append(",");
			}
		}
		int insertedRows = DBUtil.getInstance().executeUpdate(query.toString(),
				connec);
		return insertedRows;
	}

	/**
	 * Creates the associate work sheet template publication.
	 * 
	 * @param id the id
	 * @param plantSiteIdList the plant site id list
	 * @param isCloneable the is cloneable
	 * @param connection the connection
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	private static int createAssociateWorkSheetTemplatePublication(int id,
			int[] plantSiteIdList, BooleanParam isCloneable, Connection connection)
			throws Exception {
		StringBuffer query = new StringBuffer(
				WorkSheetTemapleDAO.ASSOCIATION_TEMPLATE_PUBLICATIONS);
		int[] plantSiteIds = plantSiteIdList;
		int worksheetTemplateID = id;
		BooleanParam isCloneablePub = isCloneable;
		Connection connec = connection;
		for (int i = 0; i < plantSiteIds.length; i++) {
			if (i != 0) {
				query.append(",");
			}
			query.append("'" + plantSiteIds[i] + "'");
		}
		query.append("))");
		int insertedRows = DBUtil.getInstance().executeUpdate(query.toString(),
				new Object[] { worksheetTemplateID, isCloneablePub.getValue() },
				connec);
		return insertedRows;
	}

	/**
	 * Update work sheet template.
	 * 
	 * @param workSheetTemplate the work sheet template
	 * @param connec the connec
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	private static int updateWorkSheetTemplate(
			WorkSheetTemplate workSheetTemplate, Connection connec)
			throws Exception {
		PreparedStatement pstmt = null;
		Connection connection = connec;
		WorkSheetTemplate wWorkSheetTemplate = workSheetTemplate;
		int plantSiteId = workSheetTemplate.getPlantSite() == null ? -1
				: workSheetTemplate.getPlantSite().getId() == 0 ? -1
						: workSheetTemplate.getPlantSite().getId();
		pstmt = connection
				.prepareStatement(WorkSheetTemapleDAO.UPDATE_WORKSHEET_TEMPLATE);
		pstmt.setString(1, wWorkSheetTemplate.getName());
		pstmt.setInt(2, wWorkSheetTemplate.getVersion());
		pstmt.setBoolean(3, wWorkSheetTemplate.isPublished());
		pstmt.setInt(4, wWorkSheetTemplate.getFormat().getValue());
		pstmt.setInt(5, wWorkSheetTemplate.getWidth());
		pstmt.setInt(6, wWorkSheetTemplate.getHeight());
		pstmt.setInt(7, wWorkSheetTemplate.getOrientation().getValue());
		pstmt.setString(8, wWorkSheetTemplate.getAuthor());
		pstmt.setBytes(9, wWorkSheetTemplate.getXmlDescription());
		pstmt.setInt(10, wWorkSheetTemplate.getNumberOfColumn());
		pstmt.setInt(11, wWorkSheetTemplate.getNumberOfRow());
		pstmt.setInt(12, wWorkSheetTemplate.getTopMargin());
		pstmt.setInt(13, wWorkSheetTemplate.getLeftMargin());
		pstmt.setInt(14, wWorkSheetTemplate.getVerticalSpacing());
		pstmt.setInt(15, wWorkSheetTemplate.getHorizontalSpacing());
		pstmt.setInt(16, wWorkSheetTemplate.getPropagationType().getValue());
		// pstmt.setInt(17, plantSiteId == -1 ? null : plantSiteId);
		pstmt.setString(17, plantSiteId == -1 ? null : String
				.valueOf(plantSiteId));
		pstmt.setInt(18, wWorkSheetTemplate.getType().getValue());
		pstmt.setInt(19, wWorkSheetTemplate.getId());
		int updatedRows = pstmt.executeUpdate();
		pstmt.close();
		return updatedRows;
	}

	/**
	 * Manage publishing.
	 * 
	 * @param workSheetTemplates the work sheet templates
	 * @param newWorksheetCloneableIds the new worksheet cloneable ids
	 * @param worksheetnotCloneableIds the worksheetnot cloneable ids
	 * @param user the user
	 * 
	 * @return true, if successful
	 * 
	 * @throws Exception the exception
	 */
	public static boolean managePublishing(
			WorkSheetTemplate[] workSheetTemplates,
			int[] newWorksheetCloneableIds, int[] worksheetnotCloneableIds,
			SWordUser user) throws Exception {

		WorkSheetTemplate[] publishWorksheetTemaplates = workSheetTemplates;
		int[] newWorksheetTemplateCloneableIdList = newWorksheetCloneableIds;
		int[] worksheetTemplateNotCloneableIdList = worksheetnotCloneableIds;
		SWordUser swordUser = user;

		Connection connection = null;
		ResultSet rs = null;
		Statement st = null;
		boolean isRowUpdated = false;

		try {
			connection = DBUtil.getInstance().getConnection();
			connection.setAutoCommit(false);

			int insertedRows = 0;

			if (publishWorksheetTemaplates != null
					&& publishWorksheetTemaplates.length != 0) {
				Map<Integer, Integer> worksheetIdAndPlantSiteIds = getPrivateWorksheetIdsAndPlantSiteIds(
						publishWorksheetTemaplates, swordUser, connection);
				insertedRows = insertTemplatePublications(
						publishWorksheetTemaplates, worksheetIdAndPlantSiteIds,swordUser,
						connection);
				LOGGER.debug("insertTemplatePublications " + insertedRows);
			}

			isRowUpdated = insertedRows > 0;

			if (isRowUpdated) {
				int rowsAffected = updateWorksheetTemplatePulications(
						newWorksheetTemplateCloneableIdList, true, connection);
				LOGGER
						.debug("updateWorksheetTemplatePulications - Clonable - true "
								+ rowsAffected);

				rowsAffected = updateWorksheetTemplatePulications(
						worksheetTemplateNotCloneableIdList, false, connection);
				LOGGER
						.debug("updateWorksheetTemplatePulications - Clonable - false "
								+ rowsAffected);

				rowsAffected = deleteWorksheetTemplatePulications(
						publishWorksheetTemaplates, connection);

				LOGGER.debug("deleteWorksheetTemplatePulications "
						+ rowsAffected);
			}
			connection.commit();
		} catch (SQLException e) {
			isRowUpdated = false;
			connection.rollback();
			LOGGER.error(Constants.ERROR + e.getMessage());
			throw new MessageErrorUnknownException(e.getMessage());
		} catch (Exception e) {
			isRowUpdated = false;
			connection.rollback();
			LOGGER.error(Constants.ERROR + e.getMessage());
			throw new MessageErrorUnknownException(String.valueOf(Constants.MSG_ERROR_UNKNOWN));
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (Exception e) {
				LOGGER.error(Constants.ERROR + e.getMessage());
				throw new MessageErrorUnknownException(String.valueOf(Constants.MSG_ERROR_UNKNOWN));
			}
		}
		return isRowUpdated;
	}

	/**
	 * Delete worksheet template pulications.
	 * 
	 * @param workSheetTemplates the work sheet templates
	 * @param connec the connec
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	private static int deleteWorksheetTemplatePulications(
			WorkSheetTemplate[] workSheetTemplates, Connection connec)
			throws Exception {
		WorkSheetTemplate[] publishWorksheetTemaplates = workSheetTemplates;
		Connection connection = connec;
		WorkSheetTemplate workSheetTemplate = null;

		StringBuffer query = new StringBuffer(
				WorkSheetTemapleDAO.DELETE_WORKSHEET_PUBLICTIONS);
		StringBuffer ids = new StringBuffer("");
		boolean isPlantSite = false;
		for (int i = 0; i < publishWorksheetTemaplates.length; i++) {
			workSheetTemplate = publishWorksheetTemaplates[i];
			isPlantSite = workSheetTemplate.getPlantSite() != null
					&& workSheetTemplate.getPlantSite().getId() != -1;
			if (!isPlantSite) {
				ids.append("" + workSheetTemplate.getId() + " ");
			}
		}
		String deleteIds = ids.toString().trim().replace(' ', ',');
		query.append(deleteIds + " )");
		int affectRows = 0;
		if (!deleteIds.equals("")) {
			affectRows = DBUtil.getInstance().executeUpdate(query.toString(),
					connection);
		}
		return affectRows;
	}

	/**
	 * Update worksheet template pulications.
	 * 
	 * @param worksheetCloneableIds the worksheet cloneable ids
	 * @param connec the connec
	 * @param isClonable the is clonable
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	private static int updateWorksheetTemplatePulications(
			int[] worksheetCloneableIds, boolean isClonable, Connection connec)
			throws Exception {
		int[] worksheetTemplateIdList = worksheetCloneableIds;
		Connection connection = connec;
		boolean isClonableValue = isClonable;
		StringBuffer query = new StringBuffer(
				WorkSheetTemapleDAO.UPDATE_WORKSHEET_PUBLICTIONS);
		int affectRows = 0;
		if (worksheetTemplateIdList != null
				&& worksheetTemplateIdList.length != 0) {
			for (int i = 0; i < worksheetTemplateIdList.length; i++) {
				if (i != 0) {
					query.append(",");
				}
				query.append("" + worksheetTemplateIdList[i] + "");
			}
			query.append(")");
			affectRows = DBUtil.getInstance().executeUpdate(query.toString(),
					new Object[] { isClonableValue ? 1 : 0 }, connection);
		}
		return affectRows;
	}

	/**
	 * Gets the private worksheet ids and plant site ids. This service checks
	 * that a private worksheet template is published only on corresponding
	 * plant site.
	 * 
	 * @param workSheetTemplates the work sheet templates
	 * @param connec the connec
	 * @param user the user
	 * 
	 * @return the private worksheet ids and plant site ids
	 * 
	 * @throws Exception the exception
	 */
	private static Map<Integer, Integer> getPrivateWorksheetIdsAndPlantSiteIds(
			WorkSheetTemplate[] workSheetTemplates, SWordUser user,
			Connection connec) throws Exception {

		WorkSheetTemplate[] publishWorksheetTemaplates = workSheetTemplates;
		SWordUser swordUser = user;

		Connection connection = connec;
		ResultSet rs = null;
		Statement st = null;

		Map<Integer, Integer> worksheetIdAndPlantIds = new HashMap<Integer, Integer>();
		WorkSheetTemplate workSheetTemplate = null;
		StringBuffer query = new StringBuffer(
				WorkSheetTemapleDAO.SELECT_WORKSHEET_ID_AND_PLANT_ID_ONE);
		for (int i = 0; i < publishWorksheetTemaplates.length; i++) {
			workSheetTemplate = publishWorksheetTemaplates[i];
			if (i != 0) {
				query.append(",");
			}
			query.append("" + workSheetTemplate.getId() + "");
		}
		query.append(WorkSheetTemapleDAO.SELECT_WORKSHEET_ID_AND_PLANT_ID_TWO);
		if (Constants.LOCAL_MANAGER == swordUser.getProfile().getValue()) {
			query.append(" AND WS.COL_PLANT_SITE_ID = "
					+ swordUser.getPlantSite().getId());
		}
		st = connection.createStatement();
		rs = st.executeQuery(query.toString());

		while (rs.next()) {
			worksheetIdAndPlantIds.put(rs
					.getInt("WS.COL_WORKSHEET_TEMPLATE_ID"), rs
					.getInt("WS.COL_PLANT_SITE_ID"));
		}

		st.close();
		rs.close();

		return worksheetIdAndPlantIds;
	}

	/**
	 * Insert template publications. All new publications on plant site set in
	 * the Worksheet template object
	 * 
	 * @param workSheetTemplates the work sheet templates
	 * @param connec the connection
	 * @param idAndPlantSiteIds the id and plant site ids
	 * @param user the user
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	private static int insertTemplatePublications(
			WorkSheetTemplate[] workSheetTemplates,
			Map<Integer, Integer> idAndPlantSiteIds,SWordUser user, Connection connec)
			throws Exception {
		WorkSheetTemplate[] publishWorksheetTemaplates = workSheetTemplates;
		Connection connection = connec;
		SWordUser swordUser = user;

		StringBuffer query = new StringBuffer(
				WorkSheetTemapleDAO.INSERT_TEMPLATE_PUBLICTION);

		StringBuffer modifyQuery = new StringBuffer("");

		WorkSheetTemplate workSheetTemplate = null;
		Map<Integer, Integer> worksheetIdAndPlantSiteIds = idAndPlantSiteIds;

		boolean validatePantSite = true;
		boolean isPlantSite = false;
		int insertedRecords = 0;

		for (int i = 0; i < publishWorksheetTemaplates.length; i++) {
			workSheetTemplate = publishWorksheetTemaplates[i];
			if (worksheetIdAndPlantSiteIds.get(workSheetTemplate.getId()) != null
					&& workSheetTemplate.getPlantSite() != null
					&& worksheetIdAndPlantSiteIds
							.get(workSheetTemplate.getId()) != workSheetTemplate
							.getPlantSite().getId()) {
				validatePantSite = false;
				break;
			}
			isPlantSite = workSheetTemplate.getPlantSite() != null
					&& workSheetTemplate.getPlantSite().getId() != -1;
			if (swordUser.getProfile().getValue() == Constants.LOCAL_MANAGER) {
				validatePantSite = workSheetTemplate.getPlantSite().getId() == user
						.getPlantSite().getId();
			}
			if (isPlantSite) {
				modifyQuery.append("( " + workSheetTemplate.getId() + ","
						+ workSheetTemplate.getPlantSite().getId()
						+ ",false,0 ) ");
			}
			if (isPlantSite && i < publishWorksheetTemaplates.length - 1) {
				modifyQuery.append(",");
			}
		}

		String insertValues = modifyQuery.toString().trim();
		insertValues = insertValues.endsWith(",") ? insertValues.substring(0,
				insertValues.length() - 1) : insertValues;
		query.append(insertValues);

		if (validatePantSite) {
			insertedRecords = DBUtil.getInstance().executeUpdate(
					query.toString(), connection);
		}

		return insertedRecords;
	}

	/**
	 * Checks if is user has dublicate.
	 * 
	 * @param workSheetTemplate the work sheet template
	 * @param user the user
	 * 
	 * @return true, if is user has dublicate
	 * 
	 * @throws Exception the exception
	 */
	public static boolean isUserHasDublicate(
			WorkSheetTemplate workSheetTemplate, SWordUser user)
			throws Exception {
		boolean isUserHasRights = false;
		WorkSheetTemplate toBedublicateWorksheet = workSheetTemplate;
		SWordUser requestedUser = user;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(WorkSheetTemapleDAO.USER_HAS_DUBLICATE_RIGHTS);
			pstmt.setInt(1, toBedublicateWorksheet.getId());
			pstmt.setInt(2, requestedUser.getPlantSite().getId());
			rst = pstmt.executeQuery();
			if (rst.next()) {
				isUserHasRights = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.info(Constants.ERROR + e.getMessage());
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			LOGGER.info(Constants.ERROR + e.getMessage());
			throw new MessageErrorUnknownException();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error(Constants.ERROR + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		LOGGER.info("isWorkSheetTemaplteExists " + isUserHasRights);
		return isUserHasRights;
	}

	/**
	 * Duplicate worksheet template.
	 * 
	 * @param workSheetTemplate the work sheet template
	 * @param templateName the template name
	 * @param user the user
	 * 
	 * @return the int
	 * 
	 * @throws Exception the exception
	 */
	public static int duplicateWorksheetTemplate(
			WorkSheetTemplate workSheetTemplate, String templateName,
			SWordUser user) throws Exception {
		WorkSheetTemplate toBedublicateWorksheet = workSheetTemplate;
		String newName = templateName;
		SWordUser requestedUser = user;
		PreparedStatement pstmt = null;
		int newWorksheetTemplateId = -1;
		Connection conn = null;
		try {
			conn = DBUtil.getInstance().getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(INSERT_WORKSHEET_TEMPALTE);

			PlantSite userPlantSite = requestedUser.getPlantSite();
			int plantSiteId = userPlantSite == null ? -1
					: userPlantSite != null && userPlantSite.getId() == 0 ? -1
							: userPlantSite.getId();

			pstmt.setString(1, newName);
			pstmt.setInt(2, -1);
			pstmt.setBoolean(3, false);
			pstmt.setInt(4, toBedublicateWorksheet.getFormat().getValue());
			pstmt.setInt(5, toBedublicateWorksheet.getWidth());
			pstmt.setInt(6, toBedublicateWorksheet.getHeight());
			pstmt.setInt(7, toBedublicateWorksheet.getOrientation().getValue());
			pstmt.setString(8, requestedUser.getIPN());
			pstmt.setBytes(9, toBedublicateWorksheet.getXmlDescription());
			pstmt.setInt(10, toBedublicateWorksheet.getNumberOfColumn());
			pstmt.setInt(11, toBedublicateWorksheet.getNumberOfRow());
			pstmt.setInt(12, toBedublicateWorksheet.getTopMargin());
			pstmt.setInt(13, toBedublicateWorksheet.getLeftMargin());
			pstmt.setInt(14, toBedublicateWorksheet.getVerticalSpacing());
			pstmt.setInt(15, toBedublicateWorksheet.getHorizontalSpacing());
			pstmt.setInt(16, toBedublicateWorksheet.getPropagationType().getValue());
			pstmt.setString(17, plantSiteId == -1 ? null : String
					.valueOf(plantSiteId));
			pstmt.setInt(18, toBedublicateWorksheet.getType().getValue());
			pstmt.executeUpdate();
			newWorksheetTemplateId = Integer
					.parseInt(DBUtil.getInstance()
							.getValue(
									GET_WORKSHEET_TEMAPLTE_ID,
									new Object[] {
											newName,
											-1 }, conn));
			int rowsAffected = DBUtil
					.getInstance()
					.executeUpdate(
							WorkSheetTemapleDAO.INSERT_DUBPLICATE_MACRO_OBJECT_USAGE_STAT,
							new Object[] { newWorksheetTemplateId,
									toBedublicateWorksheet.getId() }, conn);

			LOGGER
					.debug("Affected Rows INSERT_DUBPLICATE_MACRO_OBJECT_USAGE_STAT"
							+ rowsAffected);

			rowsAffected = DBUtil.getInstance().executeUpdate(
					INSERT_DUBLICATE_REFERENCED_WORKSHEET_TEMPLATE,
					new Object[] { newWorksheetTemplateId,
							toBedublicateWorksheet.getId() }, conn);

			LOGGER
					.debug("Affected Rows INSERT_DUBLICATE_REFERENCED_WORKSHEET_TEMPLATE"
							+ rowsAffected);

			if (requestedUser.getProfile().getValue() ==
					Constants.LOCAL_MANAGER) {
				rowsAffected = DBUtil.getInstance().executeUpdate(
						DUBLICATE_UPDATE_WORKSHEET_PUBLICATIONS,
						new Object[] { toBedublicateWorksheet.getId(),
								plantSiteId }, conn);
				LOGGER
						.debug("Affected Rows DUBLICATE_UPDATE_WORKSHEET_PUBLICATIONS"
								+ rowsAffected);
			}
			conn.commit();
		} catch (SQLException e) {
			LOGGER.error("Exception " + e.getMessage());
			conn.rollback();
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());
			conn.rollback();
			throw new MessageErrorUnknownException();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		return newWorksheetTemplateId;
	}

	/**
	 * Gets the list of published worksheet templates.
	 * 
	 * @param plantSiteTrigram the plant site trigram
	 * @param plantSiteCountryCode the plant site country code
	 * 
	 * @return the list of published worksheet templates
	 * 
	 * @throws MessageErrorUnknownException the message error unknown exception
	 */
	public static WorkSheetTemplate[] getListOfPublishedWorksheetTemplates(
			String plantSiteTrigram, String plantSiteCountryCode,BooleanParam isPreview)
			throws MessageErrorUnknownException {

		LOGGER
				.info(" WorkSheetTemplate DAO <<getListOfPublishedWorksheetTemplates>> ");

		Connection connection = null;
		Statement st = null;
		ResultSet rst = null;
		int rowCount = 0;
		int count = 0;
		WorkSheetTemplate[] workSheetTemplates = null;
		WorkSheetTemplate workSheetTemplate = null;
		PlantSite plantSite = new PlantSite();
		String publishedWorkSheetQuery = null;

		try {
			connection = DBUtil.getInstance().getConnection();
			st = connection.createStatement();
			LOGGER.debug("  List Of Published Worksheet Template 		   : "
					+ publishedWorkSheetQuery);
			publishedWorkSheetQuery = getPublishedWorksheetQuery(
					plantSiteTrigram, plantSiteCountryCode,isPreview);
			rst = st.executeQuery(publishedWorkSheetQuery);
			rowCount = DBUtil.getInstance().getRowCount(
					WorkSheetTemapleDAO.PUBLISHED_WORKSHEET_COUNT, connection);
			LOGGER.debug("  Filtered Count Of Published Worksheet Template : "
					+ rowCount);
			if (rowCount > 0) {
				workSheetTemplates = new WorkSheetTemplate[rowCount];
			}
			count = 0;
			while (rst.next()) {
				workSheetTemplate = workSheetTemplateInstance(workSheetTemplate);
				workSheetTemplate
						.setId(rst.getInt("COL_WORKSHEET_TEMPLATE_ID"));
				workSheetTemplate.setName(rst.getString("COL_NAME"));
				workSheetTemplate.setVersion(rst.getInt("COL_VERSION"));
				workSheetTemplate.setType(PublishedTemplateType.fromValue(rst.getInt("COL_TEMPLATE_TYPE")));
				workSheetTemplate.setPublished(rst
						.getBoolean("COL_IS_PUBLISHED"));
				workSheetTemplate.setFormat(TemplateFormat.fromValue(rst.getInt("COL_FORMAT")));
				workSheetTemplate.setHeight(rst.getInt("COL_HEIGHT"));
				workSheetTemplate.setWidth(rst.getInt("COL_WIDTH"));
				workSheetTemplate.setOrientation(TemplateOrientation.fromValue(rst.getInt("COL_ORIENTATION")));
				workSheetTemplate.setAuthor(rst.getString("COL_AUTHOR"));
				Calendar cal = Calendar.getInstance();
				Timestamp timeStamp = rst.getTimestamp("COL_UPDATE_TIME");
				cal.setTime(timeStamp);
				workSheetTemplate.setUpdateTime(cal);
				workSheetTemplate
						.setNumberOfColumn(rst.getInt("COL_NO_COLUMN"));
				workSheetTemplate.setNumberOfRow(rst.getInt("COL_NO_ROW"));
				workSheetTemplate.setTopMargin(rst.getInt("COL_TOP_MARGIN"));
				workSheetTemplate.setLeftMargin(rst.getInt("COL_LEFT_MARGIN"));
				workSheetTemplate.setVerticalSpacing(rst
						.getInt("COL_VERTICAL_SPACING"));
				workSheetTemplate.setHorizontalSpacing(rst
						.getInt("COL_HORIZONTAL_SPACING"));
				workSheetTemplate.setPropagationType(MultiTemplateDistributionDire.fromValue(rst
						.getInt("COL_PROPAGATION_TYPE")) );
				workSheetTemplate.setPlantSite(initializePlatSiteObject(plantSite));
				int plantSiteId = rst.getInt("COL_PLANT_SITE_ID");
				workSheetTemplate.getPlantSite().setId(plantSiteId == 0 ? -1 : plantSiteId);
				if(isPreview.getValue() == 1)
				{
					workSheetTemplate.setPreview(rst.getBytes("COL_PREVIEW"));
				}
				setPlantSitesAndPublicWorkSheetId(workSheetTemplate, connection);
				workSheetTemplates[count++] = workSheetTemplate;
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e.getMessage());
			throw new MessageErrorUnknownException(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(" Exception " + e.getMessage());
			throw new MessageErrorUnknownException(String.valueOf(Constants.MSG_ERROR_UNKNOWN));
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (rst != null) {
					rst.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (Exception e) {
				LOGGER.error(" SQL Exception " + e.getMessage());
				throw new MessageErrorUnknownException(String.valueOf(Constants.MSG_ERROR_UNKNOWN));
			}
		}
		return workSheetTemplates;
	}

	/**
	 * Gets the all list of worksheet templates.
	 * 
	 * @param published the published
	 * @param clonable the clonable
	 * @param plantSiteId the plant site id
	 * @param typeId the type id
	 * @param height the height
	 * @param width the width
	 * @param isLastVersion the is last version
	 * @param userId the user id
	 * @param user the user
	 * 
	 * @return the all list of worksheet templates
	 * 
	 * @throws MessageErrorUnknownException the message error unknown exception
	 */
	public static WorkSheetTemplate[] getAllListOfWorksheetTemplates(
			BooleanParam published, BooleanParam clonable, int plantSiteId, int typeId,
			int height, int width, BooleanParam isLastVersion, BooleanParam withPreview,int userId,
			int user) throws MessageErrorUnknownException {

		LOGGER
				.info(" WorkSheetTemplate DAO <<getAllListOfWorksheetTemplates>> ");

		Connection connection = null;
		Statement st = null;
		ResultSet rst = null;
		int rowCount = 0;
		int count = 0;
		WorkSheetTemplate[] workSheetTemplates = null;
		WorkSheetTemplate workSheetTemplate = null;
		PlantSite plantSite = new PlantSite();
		String publishedWorkSheetQuery = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			st = connection.createStatement();
			if (user == Constants.CENTRAL_MANAGER) {
				publishedWorkSheetQuery = getAllWorkSheetTemplates(published,
						clonable, plantSiteId, typeId, height, width,
						isLastVersion);
			} else {
				publishedWorkSheetQuery = getDraftPublishedListWorkSheetTemplates(
						published, clonable, plantSiteId, typeId, height,
						width, isLastVersion, userId, user);
			}
			LOGGER.debug(" List Of Worksheet Templates 		 : "
					+ publishedWorkSheetQuery);
			rst = st.executeQuery(publishedWorkSheetQuery);
			rowCount = DBUtil.getInstance().getRowCount(
					WorkSheetTemapleDAO.PUBLISHED_WORKSHEET_COUNT, connection);
			LOGGER.debug(" Filtered Count Of Published Worksheet Template : "
					+ rowCount);
			if (rowCount > 0) {
				workSheetTemplates = new WorkSheetTemplate[rowCount];
			}
			count = 0;
			while (rst.next()) {
				workSheetTemplate = workSheetTemplateInstance(workSheetTemplate);
				workSheetTemplate
						.setId(rst.getInt("COL_WORKSHEET_TEMPLATE_ID"));
				workSheetTemplate.setName(rst.getString("COL_NAME"));
				if ((isLastVersion.getValue() == 1 || isLastVersion.getValue() == 0) && isLastVersion.getValue() != -1){
					workSheetTemplate.setVersion(rst
							.getInt("MAX(DRAFT_TEMPLATE.COL_VERSION)"));
				} else {
					workSheetTemplate.setVersion(rst.getInt("COL_VERSION"));
				}
				workSheetTemplate.setType(PublishedTemplateType.fromValue(rst.getInt("COL_TEMPLATE_TYPE")));
				workSheetTemplate.setPublished(rst
						.getBoolean("COL_IS_PUBLISHED"));
				workSheetTemplate.setFormat(TemplateFormat.fromValue(rst.getInt("COL_FORMAT")));
				workSheetTemplate.setHeight(rst.getInt("COL_HEIGHT"));
				workSheetTemplate.setWidth(rst.getInt("COL_WIDTH"));
				workSheetTemplate.setOrientation(TemplateOrientation.fromValue(rst.getInt("COL_ORIENTATION")));
				workSheetTemplate.setAuthor(rst.getString("COL_AUTHOR"));
				Calendar cal = Calendar.getInstance();
				Timestamp timeStamp = rst.getTimestamp("COL_UPDATE_TIME");
				cal.setTime(timeStamp);
				workSheetTemplate.setUpdateTime(cal);
				workSheetTemplate
						.setNumberOfColumn(rst.getInt("COL_NO_COLUMN"));
				workSheetTemplate.setNumberOfRow(rst.getInt("COL_NO_ROW"));
				workSheetTemplate.setTopMargin(rst.getInt("COL_TOP_MARGIN"));
				workSheetTemplate.setLeftMargin(rst.getInt("COL_LEFT_MARGIN"));
				workSheetTemplate.setVerticalSpacing(rst
						.getInt("COL_VERTICAL_SPACING"));
				workSheetTemplate.setHorizontalSpacing(rst
						.getInt("COL_HORIZONTAL_SPACING"));
				workSheetTemplate.setPropagationType(MultiTemplateDistributionDire.fromValue(rst
						.getInt("COL_PROPAGATION_TYPE")) );
				workSheetTemplate.setPlantSite(initializePlatSiteObject(plantSite));
				int wPlantSiteId = rst.getInt("COL_PLANT_SITE_ID");
				workSheetTemplate.getPlantSite().setId(wPlantSiteId == 0 ? -1 : wPlantSiteId);
				if(withPreview.getValue() == 1) {
					workSheetTemplate.setPreview(rst.getBytes("COL_PREVIEW"));
				}
				setPlantSitesAndPublicWorkSheetId(workSheetTemplate, connection);
				
				workSheetTemplates[count++] = workSheetTemplate;
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e.getMessage());
			throw new MessageErrorUnknownException(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(" Exception " + e.getMessage());
			throw new MessageErrorUnknownException(String.valueOf(Constants.MSG_ERROR_UNKNOWN));
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (rst != null) {
					rst.close();
				}
				if (connection != null) {
					connection.close();
				}

			} catch (Exception e) {
				LOGGER.error(" SQL Exception " + e.getMessage());
				throw new MessageErrorUnknownException(String.valueOf(Constants.MSG_ERROR_UNKNOWN));
			}
		}

		return workSheetTemplates;
	}

	/**
	 * Gets the published worksheet query.
	 * 
	 * @param plantSiteTrigram the plant site trigram
	 * @param plantSiteCountryCod the plant site country cod
	 * 
	 * @return the published worksheet query
	 */
	public static String getPublishedWorksheetQuery(String plantSiteTrigram,
			String plantSiteCountryCod, BooleanParam withPreview) {

		StringBuffer publishedWorksheet = new StringBuffer();
		boolean isAvailableTrigram = false;
		String plantSiteTrigrame = plantSiteTrigram;
		String plantSiteCountry = plantSiteCountryCod;
		publishedWorksheet.append("SELECT MAX(COL_VERSION),");
		publishedWorksheet
				.append(" T_WORKSHEET_TEMPLATES.COL_WORKSHEET_TEMPLATE_ID, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_NAME, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_VERSION, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_TEMPLATE_TYPE, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_IS_PUBLISHED, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_FORMAT, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_HEIGHT, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_WIDTH, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_ORIENTATION, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_AUTHOR, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_UPDATE_TIME, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_NO_COLUMN, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_NO_ROW, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_TOP_MARGIN, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_LEFT_MARGIN, ");
		publishedWorksheet
				.append(" T_WORKSHEET_TEMPLATES.COL_VERTICAL_SPACING, ");
		publishedWorksheet
				.append(" T_WORKSHEET_TEMPLATES.COL_HORIZONTAL_SPACING, ");
		publishedWorksheet
				.append(" T_WORKSHEET_TEMPLATES.COL_PROPAGATION_TYPE, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_PLANT_SITE_ID, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES.COL_PREVIEW ");
		publishedWorksheet.append(" FROM ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATES, ");
		publishedWorksheet.append(" T_WORKSHEET_TEMPLATE_PUBLICATIONS, ");
		publishedWorksheet.append(" T_PLANT_SITES ");
		publishedWorksheet.append(" WHERE ");
		publishedWorksheet
				.append(" T_WORKSHEET_TEMPLATES.COL_WORKSHEET_TEMPLATE_ID = T_WORKSHEET_TEMPLATE_PUBLICATIONS.COL_PUBLISHED_WORKSHEET_TEMPLATE_ID ");

		publishedWorksheet.append(" AND ");
		publishedWorksheet
				.append(" T_WORKSHEET_TEMPLATE_PUBLICATIONS.COL_PLANT_SITE_ID =   T_PLANT_SITES.COL_PLANT_SITE_ID ");
		if ((plantSiteTrigrame != null && (!plantSiteTrigrame.equals("")))
				|| (plantSiteCountry != null && (!plantSiteCountry.equals("")))) {
			publishedWorksheet.append(" AND ");
			publishedWorksheet
					.append(" T_WORKSHEET_TEMPLATE_PUBLICATIONS.COL_PLANT_SITE_ID = ");
			publishedWorksheet
					.append(" (SELECT T_PLANT_SITES.COL_PLANT_SITE_ID FROM T_PLANT_SITES WHERE ");
			if (plantSiteTrigrame != null && (!plantSiteTrigrame.equals(""))) {
				publishedWorksheet.append(" T_PLANT_SITES.COL_TRIGRAM = '"
						+ plantSiteTrigrame + "'");
				isAvailableTrigram = true;
			}
			if (isAvailableTrigram && plantSiteCountry != null
					&& (!plantSiteCountry.equals(""))) {
				publishedWorksheet.append(" AND ");
				publishedWorksheet.append(" T_PLANT_SITES.COL_COUNTRY_CODE = '"
						+ plantSiteCountry + "' )");
			} else {
				publishedWorksheet.append(" ) ");
			}
		}

		publishedWorksheet.append(" GROUP BY COL_NAME ");
		return publishedWorksheet.toString();
	}

	/**
	 * Work sheet template instance.
	 * 
	 * @param workSheetTemplate the work sheet template
	 * 
	 * @return the work sheet template
	 */
	private static WorkSheetTemplate workSheetTemplateInstance(
			WorkSheetTemplate workSheetTemplate) {
		workSheetTemplate = new WorkSheetTemplate();
		return workSheetTemplate;
	}

	/* Get Filtered List of Work Sheet Templates */

	/**
	 * Gets the all work sheet templates.
	 * 
	 * @param published the published
	 * @param clonable the clonable
	 * @param plantSiteId the plant site id
	 * @param typeId the type id
	 * @param height the height
	 * @param width the width
	 * @param isLastVersion the is last version
	 * 
	 * @return the all work sheet templates
	 */
	public static String getAllWorkSheetTemplates(BooleanParam published,
			BooleanParam clonable, int plantSiteId, int typeId, int height,
			int width, BooleanParam isLastVersion) {
		StringBuffer allWorkSheetTemplates = new StringBuffer();

		boolean filtered = false;

		allWorkSheetTemplates.append(" SELECT ");
		allWorkSheetTemplates
				.append(" DISTINCT(DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID) , ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_NAME, ");
		if ((isLastVersion.getValue() == 1 || isLastVersion.getValue() == 0) && isLastVersion.getValue() != -1) {
			allWorkSheetTemplates.append(" MAX(DRAFT_TEMPLATE.COL_VERSION), ");
		} else {
			allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_VERSION, ");
		}
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_TEMPLATE_TYPE, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_IS_PUBLISHED, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_FORMAT, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_HEIGHT, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_WIDTH, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_ORIENTATION, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_AUTHOR, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_UPDATE_TIME, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_NO_COLUMN, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_NO_ROW, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_TOP_MARGIN, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_LEFT_MARGIN, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_VERTICAL_SPACING, ");
		allWorkSheetTemplates
				.append(" DRAFT_TEMPLATE.COL_HORIZONTAL_SPACING, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_PROPAGATION_TYPE, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_PLANT_SITE_ID, ");
		allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_PREVIEW ");
		allWorkSheetTemplates.append(" FROM ");
		allWorkSheetTemplates.append(" T_WORKSHEET_TEMPLATES DRAFT_TEMPLATE, ");
		allWorkSheetTemplates
				.append(" T_WORKSHEET_TEMPLATE_PUBLICATIONS PUBLISHED_TEMPLATE, ");
		allWorkSheetTemplates
				.append(" T_USERS_PARAMETERS_BY_APPLICATION USER_PARAM ");

		if (published.getValue() == 1 || plantSiteId > 0 || typeId > 0 || height > 0
				|| width > 0 || isLastVersion.getValue() == 1) {
			allWorkSheetTemplates.append(" WHERE ");
		}

		if ((published.getValue() == 1 || published.getValue() == 0) && published.getValue() != -1) {
			allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_IS_PUBLISHED = "
					+ published);
			filtered = true;
		}
		if (clonable.getValue() >= 0 && filtered) {
			allWorkSheetTemplates.append(" AND ");
			filtered = false;
		}
		if ((clonable.getValue() == 1 || clonable.getValue() == 0) && clonable.getValue() != -1) {
			//allWorkSheetTemplates.append(" AND ");
			allWorkSheetTemplates
					.append(" PUBLISHED_TEMPLATE.COL_IS_CLONAMBLE = "
							+ clonable);
			allWorkSheetTemplates.append(" AND ");
			allWorkSheetTemplates
					.append(" DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID = PUBLISHED_TEMPLATE.COL_PUBLISHED_WORKSHEET_TEMPLATE_ID ");
			filtered = true;
		}
		if (filtered && plantSiteId > 0) {
			allWorkSheetTemplates.append(" AND ");
			filtered = false;
		}
		if (plantSiteId > 0) {
			allWorkSheetTemplates
					.append(" PUBLISHED_TEMPLATE.COL_PLANT_SITE_ID = "
							+ plantSiteId);
			allWorkSheetTemplates.append(" AND ");
			allWorkSheetTemplates
					.append(" DRAFT_TEMPLATE.COL_PLANT_SITE_ID = PUBLISHED_TEMPLATE.COL_PLANT_SITE_ID ");
			filtered = true;
		}
		if (filtered && typeId > 0) {
			allWorkSheetTemplates.append(" AND ");
			filtered = false;
		}

		if (typeId > 0) {
			allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_TEMPLATE_TYPE = "
					+ typeId);
			filtered = true;
		}

		if (filtered && height > 0) {
			allWorkSheetTemplates.append(" AND ");
			filtered = false;
		}

		if (height > 0) {
			allWorkSheetTemplates.append(" DRAFT_TEMPLATE.COL_HEIGHT = "
					+ height);
			filtered = true;
		}

		if (filtered && width > 0) {
			allWorkSheetTemplates.append(" AND ");
			filtered = false;
		}

		if (width > 0) {
			allWorkSheetTemplates
					.append(" DRAFT_TEMPLATE.COL_WIDTH = " + width);
			filtered = true;
		}

		if ((isLastVersion.getValue() == 1 || isLastVersion.getValue() == 0) && isLastVersion.getValue() != -1) {
			allWorkSheetTemplates.append(" GROUP BY DRAFT_TEMPLATE.COL_NAME ");
		}
		return allWorkSheetTemplates.toString();
	}

	/* Query to Filter Draft and Published List of Work Sheet Templates */

	/**
	 * Gets the draft published list work sheet templates.
	 * 
	 * @param published the published
	 * @param clonable the clonable
	 * @param plantSiteId the plant site id
	 * @param typeId the type id
	 * @param height the height
	 * @param width the width
	 * @param isLastVersion the is last version
	 * @param userId the user id
	 * @param user the user
	 * 
	 * @return the draft published list work sheet templates
	 */
	public static String getDraftPublishedListWorkSheetTemplates(
			BooleanParam published, BooleanParam clonable, int plantSiteId, int typeId,
			int height, int width, BooleanParam isLastVersion, int userId,
			int user) {

		StringBuffer draftPublishWorkSheetTemplate = new StringBuffer();
		
		draftPublishWorkSheetTemplate.append(" SELECT ");
		draftPublishWorkSheetTemplate
				.append(" DISTINCT(DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID) , ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_NAME, ");
		if((isLastVersion.getValue() == 1 || isLastVersion.getValue() == 0) && isLastVersion.getValue() != -1) {
			draftPublishWorkSheetTemplate
					.append(" MAX(DRAFT_TEMPLATE.COL_VERSION), ");
		} else {
			draftPublishWorkSheetTemplate
					.append(" DRAFT_TEMPLATE.COL_VERSION, ");
		}
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_TEMPLATE_TYPE, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_IS_PUBLISHED, ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_FORMAT, ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_HEIGHT, ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_WIDTH, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_ORIENTATION, ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_AUTHOR, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_UPDATE_TIME, ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_NO_COLUMN, ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_NO_ROW, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_TOP_MARGIN, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_LEFT_MARGIN, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_VERTICAL_SPACING, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_HORIZONTAL_SPACING, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_PROPAGATION_TYPE, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_PLANT_SITE_ID, ");
		draftPublishWorkSheetTemplate
				.append(" DRAFT_TEMPLATE.COL_PREVIEW ");
		draftPublishWorkSheetTemplate.append(" FROM ");
		draftPublishWorkSheetTemplate
				.append(" T_WORKSHEET_TEMPLATES DRAFT_TEMPLATE, ");
		draftPublishWorkSheetTemplate
				.append(" T_WORKSHEET_TEMPLATE_PUBLICATIONS PUBLISHED_TEMPLATES ");		
		draftPublishWorkSheetTemplate
				.append(" WHERE DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID IN (");
		draftPublishWorkSheetTemplate.append(" SELECT ");
		draftPublishWorkSheetTemplate
		.append(" TEMPLATE_DETAIL.COL_WORKSHEET_TEMPLATE_ID ");
		draftPublishWorkSheetTemplate.append(" FROM ");
		draftPublishWorkSheetTemplate.append(" ( SELECT ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_IS_PUBLISHED, ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID, ");
		draftPublishWorkSheetTemplate.append(" PUBLISHED_TEMPLATE.COL_PLANT_SITE_ID PUBLISHED_SITE_ID, ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_PLANT_SITE_ID DRAFT_SITE_ID ");
		draftPublishWorkSheetTemplate.append(" FROM ");
		draftPublishWorkSheetTemplate.append(" T_WORKSHEET_TEMPLATES DRAFT_TEMPLATE ");
		if (user == Constants.LOCAL_MANAGER) {
			draftPublishWorkSheetTemplate.append(" LEFT OUTER JOIN ");
		} else{
			draftPublishWorkSheetTemplate.append(" JOIN ");
		}
		draftPublishWorkSheetTemplate.append(" T_WORKSHEET_TEMPLATE_PUBLICATIONS PUBLISHED_TEMPLATE ");
		draftPublishWorkSheetTemplate.append(" ON ");
		draftPublishWorkSheetTemplate.append(" (DRAFT_TEMPLATE.COL_IS_PUBLISHED = 1 " );
		draftPublishWorkSheetTemplate.append(" AND ");
		draftPublishWorkSheetTemplate.append(" PUBLISHED_TEMPLATE.COL_PUBLISHED_WORKSHEET_TEMPLATE_ID ");
		draftPublishWorkSheetTemplate.append(" = ");
		draftPublishWorkSheetTemplate.append(" DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID)) ");
		draftPublishWorkSheetTemplate.append(" TEMPLATE_DETAIL, ");
		draftPublishWorkSheetTemplate.append(" T_USERS USERS, ");
		draftPublishWorkSheetTemplate.append(" T_USERS_PARAMETERS_BY_APPLICATION PARAM ");
		draftPublishWorkSheetTemplate.append(" WHERE ");
		draftPublishWorkSheetTemplate.append(" USERS.COL_USER_ID = " + userId);
		draftPublishWorkSheetTemplate.append(" AND ");
		draftPublishWorkSheetTemplate.append(" PARAM.COL_USER_ID = USERS.COL_USER_ID ");
		draftPublishWorkSheetTemplate.append(" AND ");
		draftPublishWorkSheetTemplate.append(" PARAM.COL_PARAM_NAME = 'PLANT_SITE' ");
		draftPublishWorkSheetTemplate.append(" AND ");
		draftPublishWorkSheetTemplate.append(" ((TEMPLATE_DETAIL.DRAFT_SITE_ID = PARAM.COL_PARAM_VALUE ");
		draftPublishWorkSheetTemplate.append(" AND ");
		draftPublishWorkSheetTemplate.append(" TEMPLATE_DETAIL.COL_IS_PUBLISHED = 0) ");
		draftPublishWorkSheetTemplate.append(" OR ");
		draftPublishWorkSheetTemplate.append(" (TEMPLATE_DETAIL.PUBLISHED_SITE_ID = PARAM.COL_PARAM_VALUE ");
		draftPublishWorkSheetTemplate.append(" AND ");
		draftPublishWorkSheetTemplate.append(" TEMPLATE_DETAIL.COL_IS_PUBLISHED = 1)) ");
		if ((published.getValue() == 1 || published.getValue() == 0) && published.getValue() != -1) {
			draftPublishWorkSheetTemplate.append(" AND ");
			draftPublishWorkSheetTemplate
					.append(" DRAFT_TEMPLATE.COL_IS_PUBLISHED = " + published);
		}
		
		if (clonable.getValue() >= 0) {
			draftPublishWorkSheetTemplate.append(" AND ");
		}
		
		if ((clonable.getValue() == 1 || clonable.getValue() == 0) && clonable.getValue() != -1) {
			//draftPublishWorkSheetTemplate.append(" AND ");
			draftPublishWorkSheetTemplate
					.append(" PUBLISHED_TEMPLATES.COL_IS_CLONAMBLE = "
							+ clonable);
			draftPublishWorkSheetTemplate.append(" AND ");
			draftPublishWorkSheetTemplate
					.append(" DRAFT_TEMPLATE.COL_WORKSHEET_TEMPLATE_ID = PUBLISHED_TEMPLATES.COL_PUBLISHED_WORKSHEET_TEMPLATE_ID ");
		}

		if (plantSiteId > 0) {
			draftPublishWorkSheetTemplate.append(" AND ");
			draftPublishWorkSheetTemplate
					.append(" PUBLISHED_TEMPLATES.COL_PLANT_SITE_ID = "
							+ plantSiteId);
			draftPublishWorkSheetTemplate.append(" AND ");
			draftPublishWorkSheetTemplate
					.append(" DRAFT_TEMPLATE.COL_PLANT_SITE_ID = PUBLISHED_TEMPLATES.COL_PLANT_SITE_ID ");
		}

		if (typeId > 0) {
			draftPublishWorkSheetTemplate.append(" AND ");
			draftPublishWorkSheetTemplate
					.append(" DRAFT_TEMPLATE.COL_TEMPLATE_TYPE = " + typeId);
		}

		if (height > 0) {
			draftPublishWorkSheetTemplate.append(" AND ");
			draftPublishWorkSheetTemplate
					.append(" DRAFT_TEMPLATE.COL_HEIGHT = " + height);
		}

		if (width > 0) {
			draftPublishWorkSheetTemplate.append(" AND ");
			draftPublishWorkSheetTemplate
					.append(" DRAFT_TEMPLATE.COL_WIDTH = " + width);
		}

		draftPublishWorkSheetTemplate.append(" ) ");

		if ((isLastVersion.getValue() == 1 || isLastVersion.getValue() == 0) && isLastVersion.getValue() != -1) {
			draftPublishWorkSheetTemplate
					.append(" GROUP BY DRAFT_TEMPLATE.COL_NAME ");
		}
		return draftPublishWorkSheetTemplate.toString();
	}
	
	private static PlantSite[] getPublishedPlantSitesOfWorksheet(int templateId,Connection connec) throws Exception{
		int workSheetTemplateId = templateId;
		Connection connection = connec;
		PlantSite[] publishedPlantSites = null;
		PlantSite plantSite = null;
		ResultSet rst = null;
		PreparedStatement pstmt = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			publishedPlantSites = new PlantSite[DBUtil.getInstance()
					.getRowCount(
							WorkSheetTemapleDAO.PUBLISHED_PLANT_SITE_IDS_COUNT,
							new Object[] { workSheetTemplateId },connection)];
			
			pstmt = connection
					.prepareStatement(WorkSheetTemapleDAO.PUBLISHED_PLANT_SITE_IDS);
			pstmt.setInt(1, workSheetTemplateId);
			rst = pstmt.executeQuery();
			int count = 0;
			while (rst.next()) {
				plantSite = initializePlatSiteObject(plantSite);
				int plantSiteId = rst.getInt("COL_PLANT_SITE_ID");
				plantSite.setId(plantSiteId == 0 ? -1 : plantSiteId);
				publishedPlantSites[count] = plantSite;
				count++;
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e.getMessage());
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());
			throw new MessageErrorUnknownException();
		} finally {
			try {
				if (pstmt != null){
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
				if (rst != null) {
					rst.close();
				}
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		return publishedPlantSites;
	}
	
	/**
	 * Initialize plat site object.
	 * 
	 * @param plantSite the plant site
	 * 
	 * @return the plant site
	 */
	private static PlantSite initializePlatSiteObject(PlantSite plantSite){
		PlantSite site = plantSite;
		site = new PlantSite();
		return site;
	}
	
	private static void setPlantSitesAndPublicWorkSheetId(WorkSheetTemplate template,Connection connec) throws Exception{
		WorkSheetTemplate workSheetTemplate = template;
		Connection connection = connec;
		if (workSheetTemplate != null && workSheetTemplate.getType().getValue() == 0) {
			int publishedWorkSheetTemplateId = DBUtil
					.getInstance()
					.getRowCount(
							WorkSheetTemapleDAO.PUBLISHED_WORKSHEET_TEMPLATE_ID,
							new Object[] { workSheetTemplate.getId() },
							connection);
			publishedWorkSheetTemplateId = publishedWorkSheetTemplateId == 0 ? -1 : publishedWorkSheetTemplateId;
			workSheetTemplate.setPublishedSingleWorksheetTemplateId(publishedWorkSheetTemplateId);
		}
		
		if (workSheetTemplate != null && workSheetTemplate.isPublished()) {
			workSheetTemplate
					.setListOfPublishedPlantSites(getPublishedPlantSitesOfWorksheet(
							workSheetTemplate.getId(), connection));
		}
	}
}
