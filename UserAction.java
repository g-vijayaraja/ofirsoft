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
package com.lms.admin.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.lms.admin.forms.UserForm;
import com.lms.admin.ivo.UserIVO;
import com.lms.admin.model.UserModel;
import com.lms.admin.ovo.UserOVO;
import com.lms.common.validator.LmsValidator;
import com.lms.util.Global;
import com.lms.util.SecurityHandler;

/**
 * Created by Vijayaraja
 * 
 * lms
 */
public class UserAction extends DispatchAction{

	private static final Logger logger = Logger.getLogger(UserAction.class);
	
	private transient UserModel userModel = null;
	
	private transient UserIVO userIVO = null;
	
	private transient UserOVO userOVO = null;
	
	public ActionForward userInit(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		final UserForm userForm = (UserForm)form;

		if ((userForm != null)
				&& SecurityHandler.getInstance().securityCheck(request,
						userForm.getFormValue(), Global.READ_ACCESS)) {
			this.userModel = new UserModel();
			this.userIVO = new UserIVO();
			this.userOVO = new UserOVO();
			
			this.userIVO.setRequest(request);
			this.userIVO.setUserForm(userForm);
			
			if (!this.userModel.init(this.userIVO, this.userOVO)) {
				return mapping.findForward(Global.ERRORS);
			}
			request.setAttribute("userList", this.userOVO.getDisplayList());
			request.setAttribute(Global.MODE, Global.LIST);
			request.setAttribute(Global.PAGINATION_BEAN, this.userOVO
					.getPaginationBean());
			return mapping.findForward(Global.SUCCESS);
		}else {
			UserAction.logger.info("User has not rights to execute this method");
			return mapping.findForward(Global.FAILURE);
		}
		
	}
	
	public ActionForward userView(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		UserAction.logger.info("Executing User Group View method ");

		final UserForm userForm = (UserForm) form;
		/*
		 *  Validate the user access rights
		 */
		if ((userForm != null)
				&& SecurityHandler.getInstance().securityCheck(request,
						userForm.getFormValue(), Global.READ_ACCESS)) {
			UserAction.logger.info("User has User Group view access ");
			this.userModel = new UserModel();
			this.userOVO = new UserOVO();
			this.userIVO = new UserIVO();

			this.userIVO.setUserForm(userForm);
			this.userIVO.setRequest(request);

			if (!this.userModel.view(this.userIVO, this.userOVO)) {
				return mapping.findForward(Global.ERRORS);
			}
			this.userOVO.getUserMasterOrm().setConfirmationFlag(this.userOVO.getConfirmationFlag());
			this.userOVO.getUserMasterOrm().setUserType(this.userOVO.getUserTYpe());
			request.setAttribute("UserMasterOrm", this.userOVO.getUserMasterOrm());
			request.setAttribute(Global.PAGINATION_BEAN, this.userOVO
					.getPaginationBean());
			request.setAttribute(Global.MODE, Global.VIEW);
			return mapping.findForward(Global.SUCCESS);
		} else {
			UserAction.logger.info("User has not rights to execute this method");
			return mapping.findForward(Global.FAILURE);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ActionForward addUser(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		UserAction.logger.info("Executing User Group Add method ");
		final UserForm userForm = (UserForm) form;
		if ((userForm != null)
				&& SecurityHandler.getInstance().securityCheck(request,
						userForm.getFormValue(), Global.READ_ACCESS)) {
			UserAction.logger.info("User has write access ");

			this.userModel = new UserModel();
			this.userOVO = new UserOVO();
			this.userIVO = new UserIVO();

			this.userIVO.setUserForm(userForm);
			this.userIVO.setRequest(request);
			if (!this.userModel.add(this.userIVO, this.userOVO)) {
				return mapping.findForward(Global.ERRORS);
			}
			/*
			 * Set the combo box values
			 */
			userForm.setConfirmationFlagList(this.userOVO.getDisplayList().get(0));
			userForm.setQualifyTypeList(this.userOVO.getDisplayList().get(1));
			request.setAttribute(Global.PAGINATION_BEAN, this.userOVO
					.getPaginationBean());
			request.setAttribute(Global.MODE, Global.ADD);
			if (Global.UPDATE.equals(userForm.getAction())) {
				request.setAttribute(Global.UPDATE, Global.UPDATE);
			}
			return mapping.findForward(Global.SUCCESS);
		} else {
			UserAction.logger.info("User has not rights to execute this method");
			return mapping.findForward(Global.FAILURE);
		}

	}

	public ActionForward createUser(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response)
	throws Exception {
		UserAction.logger.info("Executing User Create method ");
		request.setAttribute(Global.MODE, Global.ADD);
		final UserForm userForm = (UserForm)form;
		final ActionErrors errors = LmsValidator.getInstance().validate(mapping, request, userForm);
		if (SecurityHandler.getInstance().securityCheck(request,
				userForm.getFormValue(), Global.WRITE_ACCESS)
				&& errors.isEmpty()) {
			this.userModel = new UserModel();
			this.userOVO = new UserOVO();
			this.userIVO = new UserIVO();

			this.userIVO.setUserForm(userForm);
			this.userIVO.setRequest(request);
			
			this.userOVO.setErrors(errors);
			
			if (Global.UPDATE.equals(userForm.getAction())) {
				if (!this.userModel.update(this.userIVO, this.userOVO)) {
					return mapping.findForward(Global.ERRORS);
				}
				if (!errors.isEmpty()) {
					request.setAttribute(Global.ERRORS, errors);
					return this.updateUser(mapping, userForm, request, response);
				}else {
					return this.userView(mapping, form, request, response);
				}
			} else {
				if (!this.userModel.create(this.userIVO, this.userOVO)) {
					return mapping.findForward(Global.ERRORS);
				}else if (!this.userModel.init(this.userIVO, this.userOVO)) {
					return mapping.findForward(Global.ERRORS);
				}
			}
			
		}
		
		if (!errors.isEmpty()) {
			request.setAttribute(Global.ERRORS, errors);
			return this.addUser(mapping, userForm, request, response);
		}
		request.setAttribute(Global.PAGINATION_BEAN, this.userOVO
				.getPaginationBean());
		request.setAttribute("userList", this.userOVO.getDisplayList());
		request.setAttribute(Global.MODE, Global.LIST);
		return mapping.findForward(Global.SUCCESS);
	}
	
	public ActionForward removeUser(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		UserAction.logger.info("Executing removeUser method ");

		final UserForm userForm = (UserForm) form;
		/*
		 *  Validate the user access rights
		 */
		if ((userForm != null)
				&& SecurityHandler.getInstance().securityCheck(request,
						userForm.getFormValue(), Global.READ_ACCESS)) {
			UserAction.logger.info("User has read access ");
			this.userModel = new UserModel();
			this.userOVO = new UserOVO();
			this.userIVO = new UserIVO();

			this.userIVO.setUserForm(userForm);
			this.userIVO.setRequest(request);
			if (!this.userModel.remove(this.userIVO, this.userOVO)) {
				return mapping.findForward(Global.ERRORS);
			}
			/*
			 *  Forward the request to init method
			 */
			return this.userInit(mapping, form, request, response);
		} else {
			UserAction.logger.info("User has not rights to execute this method");
			return mapping.findForward(Global.FAILURE);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ActionForward updateUser(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		UserAction.logger.info("Executing User Group Add method ");
		final UserForm userForm = (UserForm) form;
		if ((userForm != null)
				&& SecurityHandler.getInstance().securityCheck(request,
						userForm.getFormValue(), Global.READ_ACCESS)) {
			UserAction.logger.info("User has write access ");

			this.userModel = new UserModel();
			this.userOVO = new UserOVO();
			this.userIVO = new UserIVO();

			this.userIVO.setUserForm(userForm);
			this.userIVO.setRequest(request);
			if (!this.userModel.add(this.userIVO, this.userOVO)) {
				return mapping.findForward(Global.ERRORS);
			}else if (!this.userModel.view(this.userIVO, this.userOVO)) {

			}
			/*
			 * Set the combo box values
			 */
			
			PropertyUtils.copyProperties(userForm, this.userOVO
					.getUserMasterOrm());
			userForm.setConfirmationFlagList(this.userOVO.getDisplayList().get(0));
			userForm.setQualifyTypeList(this.userOVO.getDisplayList().get(1));
			userForm.setConfirmationDateString(userForm.getConfirmationDate() == null ? "" : userForm.getConfirmationDate().toString());
			userForm.setLeaveCalculationDtString(userForm.getLeaveCalculationDt() == null ? "" : userForm.getLeaveCalculationDt().toString());
			request.setAttribute(Global.PAGINATION_BEAN, this.userOVO
					.getPaginationBean());
			request.setAttribute(Global.MODE, Global.ADD);
			request.setAttribute(Global.UPDATE, Global.UPDATE);
			return mapping.findForward(Global.SUCCESS);
		} else {
			UserAction.logger.info("User has not rights to execute this method");
			return mapping.findForward(Global.FAILURE);
		}
	}
}
