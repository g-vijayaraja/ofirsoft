/**
 * (C) Hubino (P) Ltd.
 *
 * The program(s) herein may be used and/or copied only with the 
 * written permission of Hubino (P) Ltd Chennai. 
 * or in accordance with the terms and conditions stipulated in the 
 * agreement/contract under which the program(s) have been supplied.
 *
 * 
 */

package com.hubino.txtml.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hubino.txtml.util.FileUtil;

/**
 * This FileProcess Class uses to get the XML files from the source folder, get
 * the backup of the input XML file and generate specific reports,finally Remove
 * the XML file in source folder
 * 
 * @author Vijayaraja
 * 
 */
public class FileProcess {

	static Logger log = Logger
			.getLogger(com.hubino.txtml.common.FileProcess.class);

	/**
	 * This method used get the files from source directory
	 * 
	 */
	public static void fileOperator() {
		FileVO fileVO = new FileVO();
		List<String> fileslist = new ArrayList<String>();
		try {
			fileslist = FileUtil.filesInDir(global.sourceDir);
		} catch (Exception e) {
			log.error(e);
		}
		fileVO.setSourceDir(global.sourceDir);
		fileVO.setTargetDir(global.targetDir);
		fileVO.setErrorDIR(global.errorDIR);

		if (fileslist.size() > 0) {
			if (FileProcess.fileModify(fileslist, fileVO)) {
				fileslist.clear();
			}
		}
	}

	/**
	 * This fileModify method used to copy and rename the file After will
	 * generate the report,it remove the file in source directory.
	 * 
	 */
	public static boolean fileModify(List<String> fileList, FileVO fileVO) {

		boolean isRetval = false;
		for (int i = 0; i < fileList.size(); i++) {
			fileVO.setHandleFile(fileList.get(i).toString());
			log.info("Archiving the file  " + fileVO.getHandleFile());

			try {
				if (FileUtil.copyFile(fileVO.getSourceDir(), fileVO
						.getHandleFile(), fileVO.getTargetDir())
						& FileUtil.fileRename(fileVO.getTargetDir(), fileVO
								.getHandleFile())) {
					if (FileProcess.generateReport(fileVO)) {
						isRetval = FileUtil.fileRemove(fileVO.getSourceDir(),
								fileVO.getHandleFile());
					} else {
						isRetval = FileUtil.copyFile(fileVO.getSourceDir(),
								fileVO.getHandleFile(), fileVO.getErrorDIR())
								& FileUtil.fileRename(fileVO.getErrorDIR(),
										fileVO.getHandleFile())
								& FileUtil.fileRemove(fileVO.getSourceDir(),
										fileVO.getHandleFile());
						log.info(fileVO.getHandleFile() + " has error value");
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				log.error(e);
			}
		}
		return isRetval;
	}

	/**
	 * This method used to start the generating report
	 */
	public static boolean generateReport(FileVO fileVo) {
		boolean success = false;
		RunUtil runUtil = new RunUtil();
		success = runUtil.serviceChooser(fileVo);
		return success;
	}

	public static void main(String[] args) {
		FileProcess.fileOperator();
	}
}
