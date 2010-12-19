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
package com.lms.admin.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.lms.admin.ivo.UserTypeIVO;
import com.lms.admin.orm.UserTypeOrm;
import com.lms.admin.ovo.UserTypeOVO;
import com.lms.util.DBUtils;
import com.lms.util.HibernateUtils;

/**
 * Created by Vijayaraja
 * 
 * lms
 */
public class UserTypeDAO {

	private static final Logger logger = Logger.getLogger(UserTypeDAO.class);

	private transient Session session = null;

	private transient Transaction transaction = null;

	private transient boolean returnValue = true;

	private transient Query query = null;

//	private static final String USER_TYPE_MASTER = "FROM UserTypeOrm as QULM";
	
	private static final String USER_TYPE_MASTER = "SELECT distinct QULM.id,QULM.desc FROM UserTypeOrm as QULM ";

	private static final String USER_TYPE_TOTAL_RECORDS = "SELECT COUNT(*) FROM UserTypeOrm as QULM";

	private static final String USER_TYPE_VIEW = "FROM UserTypeOrm as QULM WHERE QULM.id = :id";

//	private static final String LEAVE_POLICY_COMBO_DESC = "SELECT LDTM.desc FROM LeavePolicyOrm as LDTM where LDTM.id = ";
	

	/*select LDTM_DESC from LDTM
	where LDTM_ID in (select QULM_LDTM_ID from QULM where qulm_id = 'QUL001');*/
	
	private static final String LEAVE_POLICY_COMBO_DESC = 
			"SELECT LDTM.desc FROM LeavePolicyOrm as LDTM where LDTM.id in " +
			"( SELECT QULM.leavePolicyId FROM UserTypeOrm as QULM where QULM.id = ";
	
	private static final String LEAVE_POLICY_COMBO_ID = 
		"SELECT LDTM.id FROM LeavePolicyOrm as LDTM where LDTM.id in " +
		"( SELECT QULM.leavePolicyId FROM UserTypeOrm as QULM where QULM.id = ";


	private static final String LEAVE_POLICY_COMBO = "FROM LeavePolicyOrm as LDTM";

	private static final String DELETE_USER_TYPE = "DELETE FROM UserTypeOrm as QULM WHERE QULM.id = :id";;

	@SuppressWarnings("unchecked")
	public boolean init(final UserTypeIVO userTypeIVO, final UserTypeOVO userTypeOVO) {
		UserTypeDAO.logger.info(" executing init method ");
		try {
			this.session = HibernateUtils.currentSession();
			this.transaction = this.session.beginTransaction();
			this.query = this.session.createQuery(UserTypeDAO.USER_TYPE_MASTER);
			this.query = this.query.setFirstResult(userTypeOVO.getPaginationBean()
					.getPageSize()
					* (userTypeOVO.getPaginationBean().getCurrentPageNo() - 1));
			this.query.setMaxResults(userTypeOVO.getPaginationBean().getPageSize());			
			final List result = this.formList(this.query.list());
			this.transaction.commit();
			userTypeOVO.setDisplayList(result);
			HibernateUtils.closeSession();
		} catch (final HibernateException he) {
			this.returnValue = false;
			UserTypeDAO.logger.info(he);
		} catch (final Exception e) {
			this.returnValue = false;
			UserTypeDAO.logger.info(e);
		}
		return this.returnValue;
	}

	private List formList(final List list) {
		final List<UserTypeOrm> returnList = new ArrayList<UserTypeOrm>();
		final Iterator itr = list.iterator();
		UserTypeOrm userTypeOrm = null;
		Object[] valueObjects = null;
		while (itr.hasNext()) {
			valueObjects = (Object[]) itr.next();
			userTypeOrm = new UserTypeOrm();
			userTypeOrm.setId(valueObjects[0].toString());
			userTypeOrm.setDesc(valueObjects[1].toString());
			returnList.add(userTypeOrm);
		}
		return returnList;
	}

	public int getTotalRecords() {
		int totalRecords = 0;
		try {
			totalRecords = DBUtils.getInstance().getTotalRecords(
					UserTypeDAO.USER_TYPE_TOTAL_RECORDS);
		} catch (final HibernateException he) {
			this.returnValue = false;
			UserTypeDAO.logger.info(he);
		} catch (final Exception e) {
			this.returnValue = false;
			UserTypeDAO.logger.info(e);
		}
		return totalRecords;
	}

	public boolean view(final UserTypeIVO userTypeIVO, final UserTypeOVO userTypeOVO) {
		try {
			this.session = HibernateUtils.currentSession();
			this.transaction = this.session.beginTransaction();
			String id = userTypeIVO.getUserTypeForm().getKeyValue();
			id = (id == null) || id.equals("") ? String.valueOf(
					userTypeIVO.getUserTypeForm().getId()).trim() : id;
			this.query = this.session.createQuery(UserTypeDAO.USER_TYPE_VIEW).setString("id", id);
			final List result = this.query.list();
			final UserTypeOrm userTypeOrm = (UserTypeOrm) result.iterator().next();
			userTypeOVO.setUserTypeOrm(userTypeOrm);
			this.transaction.commit();
			HibernateUtils.closeSession();
			if ((userTypeIVO.getUserTypeForm().getMethod() != null)
					&& !userTypeIVO.getUserTypeForm().getMethod().equals("update")) {
				userTypeOrm.setLeavePolicyId(this.formDIsplayPolicyId(DBUtils.getInstance().getComboListUseHql(
						UserTypeDAO.LEAVE_POLICY_COMBO_DESC + "'"
								+ userTypeOrm.getId() + "')")));
			}else{
			final Object[] leavePolicyIds =  DBUtils.getInstance().getComboListUseHql(
					UserTypeDAO.LEAVE_POLICY_COMBO_ID + "'"
					+ userTypeOrm.getId() + "')").toArray();
			
			userTypeOVO.setLeavePolicyIdS(this.castStringArray(leavePolicyIds));			
			}
		} catch (final HibernateException he) {
			this.returnValue = false;
			UserTypeDAO.logger.info(he);
		} catch (final Exception e) {
			this.returnValue = false;
			UserTypeDAO.logger.info(e);
		}
		return this.returnValue;
	}

	private String[] castStringArray(final Object[] leavePolicyIds) {
		final String[] tempStr = new String[leavePolicyIds.length];
		for (int i = 0; i < leavePolicyIds.length; i++) {
			tempStr[i] = leavePolicyIds[i].toString();
		}
		return tempStr;
	}

	private String formDIsplayPolicyId(final List comboListUseHql) {
		final Iterator itr = comboListUseHql.iterator();
		String returnValue = "";
		while (itr.hasNext()) {
			final String leavePolicyId = (String) itr.next();
			returnValue = returnValue + leavePolicyId + ",";
		}
		returnValue = returnValue.substring(0, returnValue.length()-1);
		return returnValue;
	}

	public boolean add(final UserTypeIVO userTypeIVO, final UserTypeOVO userTypeOVO) {
		try {
			final List<List> resultSet = new ArrayList<List>();
			final List comboList = DBUtils.getInstance().getComboListUseHql(
					UserTypeDAO.LEAVE_POLICY_COMBO);
			resultSet.add(comboList);
			userTypeOVO.setDisplayList(resultSet);
		} catch (final HibernateException he) {
			this.returnValue = false;
			UserTypeDAO.logger.info(he);
		} catch (final Exception e) {
			this.returnValue = false;
			UserTypeDAO.logger.info(e);
		}
		return this.returnValue;
	}

	public boolean createOrUpdate(final UserTypeIVO userTypeIVO,
			final UserTypeOVO userTypeOVO) {
		this.session = HibernateUtils.currentSession();
		this.transaction = this.session.beginTransaction();
		try {
			this.session.saveOrUpdate(userTypeIVO.getUserTypeOrm());
			this.transaction.commit();
			HibernateUtils.closeSession();
		} catch (final HibernateException he) {
			this.returnValue = false;
			UserTypeDAO.logger.info(he);
		} catch (final Exception e) {
			this.returnValue = false;
			UserTypeDAO.logger.info(e);
		}
		return this.returnValue;
	}

	public boolean remove(final UserTypeIVO userTypeIVO, final UserTypeOVO userTypeOVO) {
		try {
			this.session = HibernateUtils.currentSession();
			this.transaction = this.session.beginTransaction();
			String id = userTypeIVO.getUserTypeForm().getKeyValue();
			id = id == null ? userTypeIVO.getUserTypeForm().getId().trim() : id;
			final int deleteEntries = this.session.createQuery(UserTypeDAO.DELETE_USER_TYPE)
					.setString("id", id).executeUpdate();
			UserTypeDAO.logger.info("Deleted " + deleteEntries + " record(s)");
			this.transaction.commit();
			HibernateUtils.closeSession();

		} catch (final HibernateException he) {
			this.returnValue = false;
			UserTypeDAO.logger.info(he);
		} catch (final Exception e) {
			this.returnValue = false;
			UserTypeDAO.logger.info(e);
		}
		return this.returnValue;
	}

}
