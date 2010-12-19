/**
 * (C) Hubino (P) Ltd.
 *
 * The program(s) herein may be used and/or copied only with the 
 * written permission of Hubino (P) Ltd. 
 * or in accordance with the terms and conditions stipulated in the 
 * agreement/contract under which the program(s) have been supplied.
 *
 * 
 */
package renault.swo.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import renault.swo.bo.PlantSite;
import renault.swo.bo.SWordUser;
import renault.swo.bo.UserProfile;
import renault.swo.exception.MessageErrorUnknownException;
import renault.swo.global.Constants;
import renault.swo.util.DBUtil;


/**
 * The  Class UserDAO.
 * 
 * @author Vijayaraja Gnansambandan.
 */
public class UserDAO implements Serializable{

	/**
	 * Instantiates a new user dao.
	 */
	
	public UserDAO() {
		// TODO Auto-generated constructor stub
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4519672912063337404L;

	/** The logger. */
	private final static Logger LOGGER = Logger.getLogger(UserDAO.class);

	/** The Constant GET_USER_BY_IPN. */
	private static final String GET_USER_BY_IPN = "  SELECT 											    "
			+ "  	USERS.COL_USER_ID,USERS.COL_USER_CODE,                                                  "
			+ "  	PARAM.FIRST_NAME,PARAM.LAST_NAME,PARAM.PLANT_SITE,PARAM.XML_PARAM,                      "
			+ "  	PROFIL.COL_PROFIL_CODE                                                                  "
			+ "  FROM                                                                                       "
			+ "  T_USERS USERS,                                                                             "
			+ "  T_USERS_PROFIL USER_PROFIL,                                                                "
			+ "  (SELECT COL_USER_ID,                                                                       "
			+ "         MAX(CASE WHEN COL_PARAM_NAME = 'LAST_NAME' THEN COL_PARAM_VALUE END) LAST_NAME,     "
			+ "  	   MAX(CASE WHEN COL_PARAM_NAME = 'FIRST_NAME' THEN COL_PARAM_VALUE END) FIRST_NAME,    "
			+ "  	   MAX(CASE WHEN COL_PARAM_NAME = 'PLANT_SITE' THEN COL_PARAM_VALUE END) PLANT_SITE,    "
			+ "  	   MAX(CASE WHEN COL_PARAM_NAME = 'XML_PARAM' THEN COL_LONG_PARAM_VALUE END) XML_PARAM  "
			+ "  FROM T_USERS_PARAMETERS_BY_APPLICATION                                                     "
			+ "  GROUP BY COL_USER_ID) PARAM,                                                               "
			+ "  T_AVAILABLE_PROFILES_BY_APPLICATION PROFIL                                                 "
			+ "  WHERE                                                                                      "
			+ "  USERS.COL_USER_CODE = ?                                                            		"
			+ "  AND                                                                                        "
			+ "  USER_PROFIL.COL_USER_ID = USERS.COL_USER_ID                                                "
			+ "  AND                                                                                        "
			+ "  PARAM.COL_USER_ID = USERS.COL_USER_ID                                                      "
			+ "  AND                                                                                        "
			+ "  USER_PROFIL.COL_PROFIL_ID = PROFIL.COL_PROFIL_ID                                           ";

	/** The Constant GET_LIST_OF_FUNCTION_CODE. */
	private static final String GET_LIST_OF_FUNCTION_CODE = "    SELECT 													"
			+ "    	 FUNC.COL_FCN_CODE                                      "
			+ "    FROM                                                     "
			+ "    	T_AVAILABLE_FUNCTION_BY_APPLICATION FUNC,               "
			+ "    	T_PROFILES_AUTHORIZATION_BY_APPLICATION AUTH,           "
			+ "    	T_AVAILABLE_PROFILES_BY_APPLICATION PROFIL,             "
			+ "    	T_USERS_PROFIL USERPROFILE		                        "
			+ "    WHERE                                                    "
			+ "    	AUTH.COL_PROFIL_ID = PROFIL.COL_PROFIL_ID               "
			+ "    AND                                                      "
			+ "    	FUNC.COL_FCN_BY_APP_ID = AUTH.COL_FCN_BY_APP_ID         "
			+ "    AND                                                      "
			+ "    	PROFIL.COL_PROFIL_ID =  AUTH.COL_PROFIL_ID              "
			+ "    AND                                                      "
			+ "    	PROFIL.COL_PROFIL_CODE = ?                              "
			+ "    AND                                                      "
			+ "    	USERPROFILE.COL_PROFIL_ID = PROFIL.COL_PROFIL_ID        "
			+ "    AND                                                      "
			+ "    	USERPROFILE.COL_USER_ID = ?                             ";

	/** The Constant GET_COUNT_OF_FUNCTION_CODE. */
	private static final String GET_COUNT_OF_FUNCTION_CODE = "    SELECT 	"
			+ "    	COUNT(*)                                       			"
			+ "    FROM                                                     "
			+ "    	T_AVAILABLE_FUNCTION_BY_APPLICATION FUNC,               "
			+ "    	T_PROFILES_AUTHORIZATION_BY_APPLICATION AUTH,           "
			+ "    	T_AVAILABLE_PROFILES_BY_APPLICATION PROFIL,             "
			+ "    	T_USERS_PROFIL USERPROFILE		                        "
			+ "    WHERE                                                    "
			+ "    	AUTH.COL_PROFIL_ID = PROFIL.COL_PROFIL_ID               "
			+ "    AND                                                      "
			+ "    	FUNC.COL_FCN_BY_APP_ID = AUTH.COL_FCN_BY_APP_ID         "
			+ "    AND                                                      "
			+ "    	PROFIL.COL_PROFIL_ID =  AUTH.COL_PROFIL_ID              "
			+ "    AND                                                      "
			+ "    	PROFIL.COL_PROFIL_CODE = ?                              "
			+ "    AND                                                      "
			+ "    	USERPROFILE.COL_PROFIL_ID = PROFIL.COL_PROFIL_ID        "
			+ "    AND                                                      "
			+ "    	USERPROFILE.COL_USER_ID = ?                             ";

	/** The Constant GET_USER. */
	private static final String GET_USER = "  SELECT 																					"
			+ "  	USERS.COL_USER_ID,USERS.COL_USER_CODE,                                                  "
			+ "  	PARAM.FIRST_NAME,PARAM.LAST_NAME,PARAM.PLANT_SITE,PARAM.XML_PARAM,                      "
			+ "  	PROFIL.COL_PROFIL_CODE                                                                  "
			+ "  FROM                                                                                       "
			+ "  T_USERS USERS,                                                                             "
			+ "  T_USERS_PROFIL USER_PROFIL,                                                                "
			+ "  (SELECT COL_USER_ID,                                                                       "
			+ "         MAX(CASE WHEN COL_PARAM_NAME = 'LAST_NAME' THEN COL_PARAM_VALUE END) LAST_NAME,     "
			+ "  	   MAX(CASE WHEN COL_PARAM_NAME = 'FIRST_NAME' THEN COL_PARAM_VALUE END) FIRST_NAME,    "
			+ "  	   MAX(CASE WHEN COL_PARAM_NAME = 'PLANT_SITE' THEN COL_PARAM_VALUE END) PLANT_SITE,    "
			+ "  	   MAX(CASE WHEN COL_PARAM_NAME = 'XML_PARAM' THEN COL_PARAM_VALUE END) XML_PARAM       "
			+ "  FROM T_USERS_PARAMETERS_BY_APPLICATION                                                     "
			+ "  GROUP BY COL_USER_ID) PARAM,                                                               "
			+ "  T_AVAILABLE_PROFILES_BY_APPLICATION PROFIL                                                 "
			+ "  WHERE                                                                                      "
			+ "  USERS.COL_USER_ID = ?                                                            "
			+ "  AND                                                                                        "
			+ "  USER_PROFIL.COL_USER_ID = USERS.COL_USER_ID                                                "
			+ "  AND                                                                                        "
			+ "  PARAM.COL_USER_ID = USERS.COL_USER_ID                                                      "
			+ "  AND                                                                                        "
			+ "  USER_PROFIL.COL_PROFIL_ID = PROFIL.COL_PROFIL_ID                                           ";

	/** The Constant SWORD_USER_LIST. */
	private static final String SWORD_USER_LIST = "SELECT 																"
			+ "    		USERS.COL_USER_ID,USERS.COL_USER_CODE,                                                          "
			+ "    		PARAM.FIRST_NAME,PARAM.LAST_NAME,PARAM.PLANT_SITE,                                              "
			+ "    		PROFIL.COL_PROFIL_CODE,PLANTS.COL_COUNTRY_CODE,PLANTS.COL_TRIGRAM,PLANTS.COL_DESCRIPTION        "
			+ "    	FROM                                                                                                "
			+ "    		T_USERS USERS,                                                                                  "
			+ "    		T_USERS_PROFIL USER_PROFIL,                                                                     "
			+ "    		(SELECT COL_USER_ID,                                                                            "
			+ "    			   MAX(CASE WHEN COL_PARAM_NAME = 'LAST_NAME' THEN COL_PARAM_VALUE END) LAST_NAME,          "
			+ "    			   MAX(CASE WHEN COL_PARAM_NAME = 'FIRST_NAME' THEN COL_PARAM_VALUE END) FIRST_NAME,        "
			+ "    			   MAX(CASE WHEN COL_PARAM_NAME = 'PLANT_SITE' THEN COL_PARAM_VALUE END) PLANT_SITE         "
			+ "    		FROM T_USERS_PARAMETERS_BY_APPLICATION                                                          "
			+ "    		GROUP BY COL_USER_ID) PARAM LEFT JOIN T_PLANT_SITES PLANTS ON (PLANTS.COL_PLANT_SITE_ID = PARAM.PLANT_SITE), "
			+ "    		T_AVAILABLE_PROFILES_BY_APPLICATION PROFIL                                                      "
			+ "    	WHERE                                                                                               "
			+ "    		USER_PROFIL.COL_USER_ID = USERS.COL_USER_ID                                                     "
			+ "    	AND                                                                                                 "
			+ "    		PARAM.COL_USER_ID = USERS.COL_USER_ID                                                           "
			+ "    	AND                                                                                                 "
			+ "    		USER_PROFIL.COL_PROFIL_ID = PROFIL.COL_PROFIL_ID                                                ";

	/** The Constant UPDATE_USER_LAST_NAME. */
	private static final String UPDATE_USER_LAST_NAME = " UPDATE T_USERS_PARAMETERS_BY_APPLICATION PARAM SET PARAM.COL_PARAM_VALUE = ? "
			+ " WHERE COL_USER_ID = ? " + " AND COL_PARAM_NAME = 'LAST_NAME' ";

	/** The Constant UPDATE_USER_FIRST_NAME. */
	private static final String UPDATE_USER_FIRST_NAME = " UPDATE T_USERS_PARAMETERS_BY_APPLICATION PARAM SET PARAM.COL_PARAM_VALUE = ? "
			+ " WHERE COL_USER_ID = ? " + " AND COL_PARAM_NAME = 'FIRST_NAME' ";

	/** The Constant UPDATE_USER_PLANT_SITE. */
	private static final String UPDATE_USER_PLANT_SITE = " UPDATE T_USERS_PARAMETERS_BY_APPLICATION PARAM SET PARAM.COL_PARAM_VALUE = ? "
			+ " WHERE COL_USER_ID = ? " + " AND COL_PARAM_NAME = 'PLANT_SITE' ";
	
	/** The Constant UPDATE_CM_USER_PLANT_SITE. */
	private static final String UPDATE_CM_USER_PLANT_SITE = " UPDATE T_USERS_PARAMETERS_BY_APPLICATION PARAM SET PARAM.COL_PARAM_VALUE = NULL "
		+ " WHERE COL_USER_ID = ? " + " AND COL_PARAM_NAME = 'PLANT_SITE' ";
	
	/** The Constant UPDATE_USER_PROFILE. */
	private static final String UPDATE_USER_PROFILE = " UPDATE T_USERS_PROFIL SET COL_PROFIL_ID = (																	"
			+ " SELECT COL_PROFIL_ID FROM T_AVAILABLE_PROFILES_BY_APPLICATION PROFIL,T_APPLICATION APP                       "
			+ " WHERE PROFIL.COL_PROFIL_CODE = ?  AND APP.COL_APP_NAME = 'Sword' AND APP.COL_APP_ID = PROFIL.COL_APP_ID )    "
			+ " WHERE COL_USER_ID = ?                                                                                        ";

	/** The Constant SWORD_USER_COUNT. */
	private static final String SWORD_USER_COUNT = "SELECT count(*) FROM T_USERS ";

	/** The Constant VALIDATE_USER_BY_IPN_WITH_ID. */
	private static final String VALIDATE_USER_BY_IPN_WITH_ID = " 	SELECT 1 AND 																			"
			+ "    (SELECT 1 FROM T_USERS USERS WHERE USERS.COL_USER_ID = ? AND USERS.COL_USER_CODE = ?) "
			+ "  IS NOT NULL AS RETURN_CODE                                                              ";

	/** The Constant VALIDATE_USER_BY_IPN_WITH_OUT_ID. */
	private static final String VALIDATE_USER_BY_IPN_WITH_OUT_ID = "select 1 and "
			+ "(select 1 from T_USERS USERS WHERE USERS.COL_USER_CODE = ?) "
			+ "IS NOT NULL AS RETURN_CODE";

	/** The Constant GET_USER_ID_BY_IPN. */
	private static final String GET_USER_ID_BY_IPN = "select USERS.COL_USER_ID from T_USERS USERS WHERE USERS.COL_USER_CODE = ?";

	/** The Constant VALIDATE_USER_BY_ID. */
	private static final String VALIDATE_USER_BY_ID = "SELECT 1 and "
			+ " (SELECT 1 FROM T_USERS USERS WHERE USERS.COL_USER_ID = ?) "
			+ " IS NOT NULL AS RETURN_CODE ";

	/** The Constant ADD_USER. */
	private static final String ADD_USER = "INSERT INTO T_USERS (COL_USER_CODE,COL_TYPE) "
			+ " values(?,0)	";

	/** The Constant ADD_USER_PROFIL. */
	private static final String ADD_USER_PROFIL = "INSERT INTO T_USERS_PROFIL (COL_USER_ID,COL_PROFIL_ID) "
			+ " values(?, ( SELECT COL_PROFIL_ID FROM T_AVAILABLE_PROFILES_BY_APPLICATION PROFIL,T_APPLICATION APP"
			+ " WHERE PROFIL.COL_PROFIL_CODE = ?  AND APP.COL_APP_NAME = 'Sword' AND APP.COL_APP_ID = PROFIL.COL_APP_ID) )	";

	/** The Constant ADD_USER_PARAM. */
	private static final String ADD_USER_PARAM = "INSERT INTO T_USERS_PARAMETERS_BY_APPLICATION "
			+ "(COL_APP_ID,COL_USER_ID,COL_PARAM_NAME,COL_PARAM_VALUE,COL_LONG_PARAM_VALUE) "
			+ " values (?,?,?,?,?)";

	/** The Constant COUNT_CENTRAL_MANAGERS. */
	private static final String COUNT_CENTRAL_MANAGERS = "	SELECT COUNT(*) 										"
			+ "	FROM                                                    "
			+ "		T_USERS USERS,T_USERS_PROFIL USRPROFIL,             "
			+ "		T_AVAILABLE_PROFILES_BY_APPLICATION PROFIL,         "
			+ "		T_APPLICATION APP                                   "
			+ "	WHERE                                                   "
			+ "		USERS.COL_USER_ID = USRPROFIL.COL_USER_ID           "
			+ "	AND                                                     "
			+ "		PROFIL.COL_PROFIL_ID = USRPROFIL.COL_PROFIL_ID      "
			+ "	AND                                                     "
			+ "		PROFIL.COL_PROFIL_CODE = "+ Constants.CENTRAL_MANAGER
			+ "	AND                                                     "
			+ "		APP.COL_APP_ID = PROFIL.COL_APP_ID                  "
			+ "	AND                                                     "
			+ "		COL_APP_NAME = 'Sword';                             ";

	/** The Constant DELETE_USER. */
	private static final String DELETE_USER = "DELETE FROM T_USERS WHERE COL_USER_ID = ? ";

	/**
	 * Connect user.
	 * 
	 * @param ipn the ipn
	 * 
	 * @return the s word user
	 * 
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public static SWordUser connectUser(String ipn) throws Exception {

		LOGGER.info("request ipn value " + ipn);
		LOGGER.debug("UserDAO.GET_USER_BY_IPN :: " + UserDAO.GET_USER_BY_IPN);

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;

		SWordUser swordUser = null;
		PlantSite plantSite = new PlantSite();

		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection.prepareStatement(UserDAO.GET_USER_BY_IPN);
			pstmt.setString(1, ipn);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				swordUser = initializeSwordUserObject(swordUser);
				swordUser.setId(rst.getInt("USERS.COL_USER_ID"));
				swordUser.setIPN(rst.getString("USERS.COL_USER_CODE"));
				swordUser.setFirstName(rst.getString("PARAM.FIRST_NAME"));
				swordUser.setLastName(rst.getString("PARAM.LAST_NAME"));
				swordUser.setPlantSite(plantSite);
				int plantSiteId = rst.getInt("PARAM.PLANT_SITE");
				swordUser.getPlantSite().setId(plantSiteId == 0 ? -1 : plantSiteId);
				swordUser.setXmlParametersDescription(rst.getBytes("PARAM.XML_PARAM"));
				swordUser.setProfile(UserProfile.fromString(rst
						.getString("PROFIL.COL_PROFIL_CODE")));
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
		return swordUser;
	}
	
	/**
	 * Initialize sword user object.
	 * 
	 * @param user the user
	 * 
	 * @return the s word user
	 */
	private static SWordUser initializeSwordUserObject(SWordUser user){
		SWordUser swordUser = user;
		swordUser = new SWordUser();
		return swordUser;
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
	
	/**
	 * Gets the list of function.
	 * 
	 * @param user the user
	 * 
	 * @return the list of function
	 * 
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public static String[] getListOfFunction(SWordUser user)
			throws SQLException, Exception {

		LOGGER.debug("UserDAO.GET_LIST_OF_FUNCTION_CODE :: "
				+ UserDAO.GET_LIST_OF_FUNCTION_CODE);
		ResultSet rst = null;
		PreparedStatement pstmt = null;
		String[] listOfFunctionCode = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			listOfFunctionCode = new String[DBUtil.getInstance()
					.getRowCount(
							UserDAO.GET_COUNT_OF_FUNCTION_CODE,
							new Object[] { user.getProfile().getValue(),
									user.getId() },connection)];
			
			pstmt = connection
					.prepareStatement(UserDAO.GET_LIST_OF_FUNCTION_CODE);
			pstmt.setInt(1, user.getProfile().getValue());
			pstmt.setInt(2, user.getId());
			rst = pstmt.executeQuery();
			int count = 0;
			while (rst.next()) {
				listOfFunctionCode[count] = rst.getString("FUNC.COL_FCN_CODE");
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
		return listOfFunctionCode;
	}

	/**
	 * Gets the list of users.
	 * 
	 * @return the list of users
	 * 
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public static SWordUser[] getListOfUsers() throws Exception {

		LOGGER.debug("UserDAO.SWORD_USER_LIST :: " + UserDAO.SWORD_USER_LIST);

		ResultSet rst = null;
		Statement stmt = null;

		SWordUser[] swordUserList = null;
		SWordUser swordUser = null;
		PlantSite plantSite = new PlantSite();
		Connection connection = null;

		try {
			connection = DBUtil.getInstance().getConnection();
			swordUserList = new SWordUser[DBUtil.getInstance().getRowCount(UserDAO.SWORD_USER_COUNT,connection)];
			
			stmt = connection.createStatement();
			rst = stmt.executeQuery(UserDAO.SWORD_USER_LIST);
			int count = 0;
			while (rst.next()) {
				swordUser = initializeSwordUserObject(swordUser);
				swordUser.setId(rst.getInt("USERS.COL_USER_ID"));
				swordUser.setIPN(rst.getString("USERS.COL_USER_CODE"));
				swordUser.setFirstName(rst.getString("PARAM.FIRST_NAME"));
				swordUser.setLastName(rst.getString("PARAM.LAST_NAME"));
				swordUser.setPlantSite(initializePlatSiteObject(plantSite));
				int plantSiteId = rst.getInt("PARAM.PLANT_SITE");
				swordUser.getPlantSite().setId(plantSiteId == 0 ? -1 : plantSiteId);
				swordUser.getPlantSite().setCountryCode(plantSiteId == 0 ? "" : rst.getString("PLANTS.COL_COUNTRY_CODE"));
				swordUser.getPlantSite().setTrigram(plantSiteId == 0 ? "" : rst.getString("PLANTS.COL_TRIGRAM"));
				swordUser.getPlantSite().setDescription(plantSiteId == 0 ? "" : rst.getString("PLANTS.COL_DESCRIPTION"));
				swordUser.setProfile(UserProfile.fromString(rst
						.getString("PROFIL.COL_PROFIL_CODE")));
				swordUserList[count] = swordUser;
				count++;
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());

		} finally {
			try {
				if (stmt != null){
					stmt.close();
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
		return swordUserList;
	}
	
	/**
	 * Gets the user profile.
	 * 
	 * @param id the id
	 * 
	 * @return the user profile
	 * 
	 * @throws Exception the exception
	 */
	public static SWordUser getUserProfile(int id) throws Exception {
		LOGGER.info("request id value " + id);
		LOGGER.debug("UserDAO.GET_USER :: " + UserDAO.GET_USER);

		ResultSet rst = null;
		PreparedStatement pstmt = null;

		SWordUser swordUser = null;
		PlantSite plantSite = new PlantSite();
		Connection connection = null;

		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection.prepareStatement(UserDAO.GET_USER);
			pstmt.setInt(1, id);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				swordUser = new SWordUser();
				swordUser.setId(rst.getInt("USERS.COL_USER_ID"));
				swordUser.setIPN(rst.getString("USERS.COL_USER_CODE"));
				swordUser.setFirstName(rst.getString("PARAM.FIRST_NAME"));
				swordUser.setLastName(rst.getString("PARAM.LAST_NAME"));
				swordUser.setPlantSite(plantSite);
				int plantSiteId = rst.getInt("PARAM.PLANT_SITE");
				swordUser.getPlantSite().setId(plantSiteId == 0 ? -1 : plantSiteId);
				swordUser.setProfile(UserProfile.fromString(rst
						.getString("PROFIL.COL_PROFIL_CODE")));
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());

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

		return swordUser;
	}

	/**
	 * Validate user.
	 * 
	 * @param id the id
	 * 
	 * @return true, if successful
	 * 
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public static boolean validateUser(int id) throws SQLException, Exception {
		LOGGER.debug("UserDAO.VALIDATE_USER_BY_ID :: "
				+ UserDAO.VALIDATE_USER_BY_ID);
		boolean isUserValid = false;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection.prepareStatement(UserDAO.VALIDATE_USER_BY_ID);
			pstmt.setInt(1, id);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				isUserValid = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());

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
		return isUserValid;
	}

	/**
	 * Validate user.
	 * 
	 * @param ipn the ipn
	 * 
	 * @return true, if successful
	 * 
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public static boolean validateUser(String ipn) throws SQLException, Exception {
		LOGGER.debug("UserDAO.VALIDATE_USER_BY_IPN_WITH_OUT_ID :: "
				+ UserDAO.VALIDATE_USER_BY_IPN_WITH_OUT_ID);
		boolean isUserValid = false;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(UserDAO.VALIDATE_USER_BY_IPN_WITH_OUT_ID);
			pstmt.setString(1, ipn);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				isUserValid = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());

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
		return isUserValid;
	}

	/**
	 * Validate user.
	 * 
	 * @param id the id
	 * @param ipn the ipn
	 * 
	 * @return true, if successful
	 * 
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public static boolean validateUser(int id, String ipn) throws SQLException, Exception {
		LOGGER.debug("UserDAO.VALIDATE_USER_AND_PROFILE :: "
				+ UserDAO.VALIDATE_USER_BY_IPN_WITH_ID);
		boolean isUserValid = false;

		ResultSet rst = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			pstmt = connection
					.prepareStatement(UserDAO.VALIDATE_USER_BY_IPN_WITH_ID);
			pstmt.setInt(1, id);
			pstmt.setString(2, ipn);
			rst = pstmt.executeQuery();
			if (rst.next()) {
				isUserValid = rst.getBoolean("RETURN_CODE");
			}
		} catch (SQLException e) {
			LOGGER.error(" SQLException " + e.getMessage());

		} catch (Exception e) {
			LOGGER.error("Exception " + e.getMessage());

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
				LOGGER.error("SQLException " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}
		return isUserValid;
	}

	/**
	 * Update user.
	 * 
	 * @param swordUser the sword user
	 * 
	 * @return true, if successful
	 * 
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public static boolean updateUser(SWordUser swordUser) throws Exception {
		SWordUser toBeUpdatedUser = swordUser;
		boolean isUserUpdated = false;
		Connection connection = null;
		int updatedRows = 0;
		try {
			connection = DBUtil.getInstance().getConnection();
			connection.setAutoCommit(false);
			if (toBeUpdatedUser.getLastName() != null
					&& !toBeUpdatedUser.getLastName().equals("")) {
				updatedRows = DBUtil.getInstance().executeUpdate(
						UserDAO.UPDATE_USER_LAST_NAME,
						new Object[] { toBeUpdatedUser.getLastName(),
								toBeUpdatedUser.getId() }, connection);
				LOGGER.debug("(Updated Rows) - Last Name : " + updatedRows);
			}
			if (toBeUpdatedUser.getFirstName() != null
					&& !toBeUpdatedUser.getFirstName().equals("")) {
				updatedRows = DBUtil.getInstance().executeUpdate(
						UserDAO.UPDATE_USER_FIRST_NAME,
						new Object[] { toBeUpdatedUser.getFirstName(),
								toBeUpdatedUser.getId() }, connection);
				LOGGER.debug("(Updated Rows) First Name" + updatedRows);
			}
			
			if (toBeUpdatedUser.getProfile().getValue() == Constants.CENTRAL_MANAGER) {
				updatedRows = DBUtil.getInstance().executeUpdate(
						UserDAO.UPDATE_CM_USER_PLANT_SITE,
						new Object[] { toBeUpdatedUser.getId() }, connection);
				LOGGER.debug("(Updated Rows) Central Manager Plant Site" + updatedRows);
			}else if (toBeUpdatedUser.getPlantSite() != null) {
				updatedRows = DBUtil.getInstance().executeUpdate(
						UserDAO.UPDATE_USER_PLANT_SITE,
						new Object[] { toBeUpdatedUser.getPlantSite().getId(),
								toBeUpdatedUser.getId() }, connection);
				LOGGER.debug("(Updated Rows) Plant Site" + updatedRows);
			}
			
			
			
			LOGGER.debug("(Updated Rows) UPDATE_USER_PROFILE " + UserDAO.UPDATE_USER_PROFILE);
			LOGGER.debug("(Values " + toBeUpdatedUser.getProfile().getValue() + " ,"+toBeUpdatedUser.getId() );
			DBUtil.getInstance().executeUpdate(
					UserDAO.UPDATE_USER_PROFILE,
					new Object[] { toBeUpdatedUser.getProfile().getValue(),
							toBeUpdatedUser.getId() }, connection);
			
			isUserUpdated = updatedRows > 0;
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			LOGGER.error("SQLException " + e.getMessage());
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			connection.rollback();
			LOGGER.error("Exception " + e.getMessage());
			throw new MessageErrorUnknownException();
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
		return isUserUpdated;
	}

	/**
	 * Adds the user.
	 * 
	 * @param swordUser the sword user
	 * 
	 * @return true, if successful
	 * 
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public static boolean addUser(SWordUser swordUser) throws Exception {
		boolean isUserAdded = false;
		SWordUser toBeAddedUser = swordUser;

		PreparedStatement pstmt = null;
		Connection connection = null;

		try {
			connection = DBUtil.getInstance().getConnection();
			connection.setAutoCommit(false);
			LOGGER.debug("UserDAO.ADD_USER :: " + UserDAO.ADD_USER);
			DBUtil.getInstance().executeUpdate(UserDAO.ADD_USER,
					new Object[] { toBeAddedUser.getIPN() }, connection);

			int userId = Integer.parseInt(DBUtil.getInstance().getValue(
					UserDAO.GET_USER_ID_BY_IPN,
					new Object[] { toBeAddedUser.getIPN() }, connection));
			toBeAddedUser.setId(userId);

			LOGGER
					.debug("UserDAO.ADD_USER_PROFIL :: "
							+ UserDAO.ADD_USER_PROFIL);
			DBUtil.getInstance()
					.executeUpdate(
							UserDAO.ADD_USER_PROFIL,
							new Object[] { toBeAddedUser.getId(),
									toBeAddedUser.getProfile().getValue() },
							connection);

			if (toBeAddedUser.getLastName() != null
					&& !toBeAddedUser.getLastName().equals("")) {
				addUserParam(Constants.APP_ID, toBeAddedUser.getId(),
						Constants.LAST_NAME, toBeAddedUser.getLastName(), null,
						connection);
			}else {
				addUserParam(Constants.APP_ID, toBeAddedUser.getId(),
						Constants.LAST_NAME, null, null, connection);
			}
			
			if (toBeAddedUser.getFirstName() != null
					&& !toBeAddedUser.getFirstName().equals("")) {
				addUserParam(Constants.APP_ID, toBeAddedUser.getId(),
						Constants.FIRST_NAME, toBeAddedUser.getFirstName(),
						null, connection);
			}else {
				addUserParam(Constants.APP_ID, toBeAddedUser.getId(),
						Constants.FIRST_NAME, null, null, connection);
			}
			
			if (toBeAddedUser.getPlantSite() != null) {
				addUserParam(Constants.APP_ID, toBeAddedUser.getId(),
						Constants.PLANT_SITE, String.valueOf(toBeAddedUser
								.getPlantSite().getId()), null, connection);
			}else {
				addUserParam(Constants.APP_ID, toBeAddedUser.getId(),
						Constants.PLANT_SITE, null, null, connection);
			}

			if (toBeAddedUser.getXmlParametersDescription() != null) {
				addUserParam(Constants.APP_ID, toBeAddedUser.getId(),
						Constants.XML_PARAM, null, toBeAddedUser
								.getXmlParametersDescription(), connection);
			}else {
				addUserParam(Constants.APP_ID, toBeAddedUser.getId(),
						Constants.XML_PARAM, null, null, connection);
			}
			
			
			isUserAdded = true;
			connection.commit();
		} catch (SQLException e) {
			isUserAdded = false;
			connection.rollback();
			LOGGER.error("SQLException " + e.getMessage());
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			isUserAdded = false;
			connection.rollback();
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
				
			} catch (Exception uncatched) {
				LOGGER.error("Exception " + uncatched.getMessage());
				throw new MessageErrorUnknownException();
			}
		}

		return isUserAdded;
	}

	/**
	 * Adds the user param.
	 * 
	 * @param appId the app id
	 * @param userId the user id
	 * @param paramName the param name
	 * @param paramValue the param value
	 * @param xmlParam the xml param
	 * @param connection the connection
	 * 
	 * @throws SQLException the SQL exception
	 */
	private static void addUserParam(int appId, int userId, String paramName,
			String paramValue, byte[] xmlParam, Connection connection)
			throws SQLException {
		PreparedStatement pstmt = null;
		LOGGER.debug("UserDAO.ADD_USER_PARAM :: " + UserDAO.ADD_USER_PARAM);
		pstmt = connection.prepareStatement(UserDAO.ADD_USER_PARAM);
		pstmt.setInt(1, appId);
		pstmt.setInt(2, userId);
		pstmt.setString(3, paramName);
		if (Constants.XML_PARAM.equals(paramName)) {
			pstmt.setString(4, null);
			pstmt.setBytes(5, xmlParam);
		} else {
			pstmt.setString(4, paramValue);
			pstmt.setString(5, null);
		}
		int updatedRows = pstmt.executeUpdate();
		LOGGER.debug("UserDAO.ADD_USER_PARAM - updatedRows :: " + updatedRows);
	}

	/**
	 * Checks if is last central manager.
	 * 
	 * @return true, if is last central manager
	 * 
	 * @throws MessageErrorUnknownException the message error unknown exception
	 */
	public static boolean isLastCentralManager() throws MessageErrorUnknownException {
		LOGGER.debug("UserDAO.COUNT_CENTRAL_MANAGERS :: "
				+ UserDAO.COUNT_CENTRAL_MANAGERS);
		boolean isLastCentralManager = false;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			isLastCentralManager = 1 == DBUtil.getInstance().getRowCount(
					UserDAO.COUNT_CENTRAL_MANAGERS,connection);
			
		} catch (Exception e) {
			LOGGER.error(" SQLException " + e.getMessage());
		}finally{
			try {
				if(connection!= null){
					connection.close();
				}
			} catch (SQLException e) {
				throw new MessageErrorUnknownException();
			}
		}
		return isLastCentralManager;
	}

	/**
	 * Delete user.
	 * 
	 * @param toBeDeletedUserId the to be deleted user id
	 * 
	 * @return true, if successful
	 * 
	 * @throws MessageErrorUnknownException the message error unknown exception
	 * @throws Exception the exception
	 */
	public static boolean deleteUser(int toBeDeletedUserId) throws MessageErrorUnknownException, Exception {
		LOGGER.debug("UserDAO.DELETE_USER :: " + UserDAO.DELETE_USER);
		boolean isUserDeleted = false;
		PreparedStatement pstmt = null;
		Connection connection = null;
		try {
			connection = DBUtil.getInstance().getConnection();
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(UserDAO.DELETE_USER);
			pstmt.setInt(1, toBeDeletedUserId);
			int deletedRows = pstmt.executeUpdate();
			LOGGER.info("Deleted Rows  " + deletedRows);
			isUserDeleted = deletedRows > 0;
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			isUserDeleted = false;
			LOGGER.error("SQLException " + e.getMessage());
			throw new MessageErrorUnknownException();
		} catch (Exception e) {
			isUserDeleted = false;
			connection.rollback();
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
					
				} catch (Exception uncatched) {
					LOGGER.error("Exception " + uncatched.getMessage());
				}
		}
		return isUserDeleted;
	}

}
