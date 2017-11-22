package com.cloud.disk.web.model;

import java.io.Serializable;

public class FileModel implements Serializable {
	private static final long serialVersionUID = -4407132192946147735L;
	
	public static enum Type
	{
		FOLDER("folder"),
		;
		private final String value;
		private Type(final String value)
		{
			this.value = value;
		}
		
		@Override
		public String toString()
		{
			return this.value;
		}
	}
	

	private String fileName;
	
	private String filePath;
	
	private String fileType;
	
	private String fileSize;

	private Long lastModified;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}
}
