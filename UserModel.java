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
package com.lms.admin.model;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;

import com.lms.admin.dao.UserDAO;
import com.lms.admin.dao.UserGroupDAO;
import com.lms.admin.forms.UserForm;
import com.lms.admin.ivo.UserIVO;
import com.lms.admin.orm.UserGroupOrm;
import com.lms.admin.orm.UserMasterOrm;
import com.lms.admin.ovo.UserOVO;
import com.lms.util.CommonUtils;
import com.lms.util.DBUtils;
import com.lms.util.Global;
import com.lms.util.PaginationBean;

/**
 * Created by Vijayaraja
 * 
 * lms
 */
public class UserModel {

	/*
	 * Common return value
	 */
	private transient boolean returnValue = false;

	private transient UserDAO userDAO = null;

	private transient PaginationBean paginationBean = null;

	private static final Logger logger = Logger.getLogger(UserModel.class);

	public UserModel() {
		// TODO Auto-generated constructor stub
	}

	public boolean init(final UserIVO userIVO, final UserOVO userOVO) {
		this.returnValue = false;
		this.userDAO = new UserDAO();
		if (userOVO.getPaginationBean() == null) {
			this.userGroupPaginate(userIVO, userOVO);
		}
		this.returnValue = this.userDAO.init(userIVO, userOVO);
		if ((userOVO.getDisplayList() != null)
				&& (userOVO.getDisplayList().size() == 0)) {
			final int totalRecords = this.userDAO.getTotalRecords(userIVO);
			UserModel.logger.info("Total Records " + totalRecords);
			userOVO.getPaginationBean().refreshPageNo(totalRecords);
			if (totalRecords > 0) {
				this.init(userIVO, userOVO);
			}
		}
		return this.returnValue;
	}

	private void userGroupPaginate(final UserIVO userIVO, final UserOVO userOVO) {
		if (CommonUtils.getInstance().hasPageSize(userIVO.getRequest())) {
			this.paginationBean = new PaginationBean(userIVO.getRequest());
		} else {
			this.initPaginate(userIVO, userOVO);
		}
		userOVO.setPaginationBean(this.paginationBean);
	}

	private void initPaginate(final UserIVO userIVO, final UserOVO userOVO) {
		this.userDAO = new UserDAO();
		final int totalRecords = this.userDAO.getTotalRecords(userIVO);
		this.paginationBean = new PaginationBean(totalRecords, userIVO
				.getRequest());
	}

	public boolean view(final UserIVO userIVO, final UserOVO userOVO) {
		this.returnValue = false;
		this.userDAO = new UserDAO();
		this.userGroupPaginate(userIVO, userOVO);
		this.returnValue = this.userDAO.view(userIVO, userOVO);
		return this.returnValue;
	}

	public boolean add(final UserIVO userIVO, final UserOVO userOVO) {
		this.returnValue = true;
		this.userDAO = new UserDAO();
		this.userGroupPaginate(userIVO, userOVO);
		this.returnValue = this.userDAO.add(userIVO, userOVO);
		return this.returnValue;
	}

	public boolean create(final UserIVO userIVO, final UserOVO userOVO) {
		this.returnValue = true;
		this.userGroupPaginate(userIVO, userOVO);
		if (this.validate(userIVO, userOVO)) {
			try {
				this.preProcessSave(userIVO);
				this.userDAO = new UserDAO();
				this.returnValue = this.userDAO
						.createOrUpdate(userIVO, userOVO);
				if (userIVO.getUserForm().getMethod().equals("createUser")
						&& !Global.UPDATE.equals(userIVO.getUserForm()
								.getAction())) {
					this.returnValue = this.userDAO.createAccessRights(userIVO,
							userOVO);
				}
				this.userGroupPaginate(userIVO, userOVO);
				userOVO.getPaginationBean().setCurrentPageNo(
						userOVO.getPaginationBean().getTotalPages());
			} catch (final IllegalAccessException e) {
				this.returnValue = false;
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				this.returnValue = false;
				e.printStackTrace();
			} catch (final NoSuchMethodException e) {
				this.returnValue = false;
				e.printStackTrace();
			}
		}
		return this.returnValue;
	}

	private void preProcessSave(final UserIVO userIVO)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		final UserForm userForm = userIVO.getUserForm();
		final UserMasterOrm userMasterOrm = new UserMasterOrm();
		PropertyUtils.copyProperties(userMasterOrm, userForm);
		userIVO.setUserMasterOrm(userMasterOrm);
	}

	private boolean validate(final UserIVO userIVO, final UserOVO userOVO) {
		boolean isValidate = true;
		this.userDAO = new UserDAO();
		ActionMessage message = null;
		try {
			if (!Global.UPDATE.equals(userIVO.getUserForm().getAction()) && isValidate) {
				if (!DBUtils.getInstance().isExist(UserMasterOrm.class,
						userIVO.getUserForm().getUserId())) {
					isValidate = false;
					message = new ActionMessage(Global.ERRORS_ISEXIST,
							new String[] { userIVO.getUserForm().getUserId() });
					userOVO.getErrors().add(Global.ISEXIST, message);
				}else if (DBUtils.getInstance().isExist(UserGroupOrm.class,
						userIVO.getUserForm().getUserGroupId())) {
					isValidate = false;
					message = new ActionMessage(Global.ERRORS_IS_NOT_EXIST,
							new String[] { userIVO.getUserForm().getUserGroupId() });
					userOVO.getErrors().add(Global.ISEXIST, message);
				}
			}
			
			if (userIVO.getUserForm().getConfirmationFlag().equals(Global.YES)
					&& (userIVO.getUserForm().getConfirmationDateString() == null || 
							"".equals(userIVO.getUserForm()
									.getConfirmationDateString()))) {
				isValidate = false;
				message = new ActionMessage(Global.ERRORS_ISNEED,
						new String[] { Global.CONFIRMATION_DATE });
				userOVO.getErrors().add(Global.NEED_CONFIRMATION_DATE, message);

			}
			
		} catch (Exception e) {
			logger.info(" Error Validation "+e.getMessage());
		}

		return isValidate;
	}

	public boolean remove(final UserIVO userIVO, final UserOVO userOVO) {
		this.returnValue = false;
		this.userDAO = new UserDAO();
		this.userGroupPaginate(userIVO, userOVO);
		this.returnValue = this.userDAO.removeAccessRights(userIVO, userOVO);
		this.returnValue = this.userDAO.remove(userIVO, userOVO);
		return this.returnValue;
	}

	public boolean update(final UserIVO userIVO, final UserOVO userOVO) {
		return this.create(userIVO, userOVO);
	}
}
