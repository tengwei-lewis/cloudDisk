package com.cloud.disk.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.disk.util.FileUtil;
import com.cloud.disk.util.UtilException;
import com.cloud.disk.web.model.FileModel;

@RestController
@RequestMapping(value = "/rest/file")
public class FileController {

	@Value("${user.root.path}")
	private String rootPath;

	@RequestMapping(value = "/get-file-list", method = RequestMethod.POST)
	public List<FileModel> getFileList(@RequestParam(name = "path", required = false) final String path) throws UtilException, IOException {
		final List<FileModel> fmList = new ArrayList<>();
		final String userPath = rootPath;
		String filePath = null;
		if (StringUtils.isNotBlank(path)) {
			filePath = userPath + File.separator + path;
		} else {
			filePath = userPath;
		}
		final DirectoryStream<Path> pathList = FileUtil.getFileList(filePath);
		FileModel fm = null;
		for (final Path p : pathList) {
			fm = new FileModel();
			final String fileName = p.getFileName().toString();
			final String absolutePath = p.toAbsolutePath().toString();
			fm.setFileName(fileName);
			fm.setFilePath(absolutePath);
			if (Files.isDirectory(p)) {
				fm.setFileType(FileModel.Type.FOLDER.toString());
			} else {
				fm.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()));
				fm.setFileSize(FileUtil.formetFileSize(Files.size(p)));
			}
			fm.setLastModified(p.toFile().lastModified());
			fmList.add(fm);
		}
		return fmList;
	}

	@RequestMapping(value = "/get-file", method = RequestMethod.POST)
	public byte[] getFile(@RequestParam(name = "path") final String path) throws UtilException {
		return FileUtil.getFileToByte(path);
	}

}
