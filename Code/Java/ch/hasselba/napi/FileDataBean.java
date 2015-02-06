package ch.hasselba.napi;

import java.io.Serializable;

public class FileDataBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String fileName;
	private String fileData;
	private String dbPath;
	private String dbServer;

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public String getDbServer() {
		return dbServer;
	}

	public void setDbServer(String dbServer) {
		this.dbServer = dbServer;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void loadData() {
		this.fileData = NAPIUtils.loadFile(this.dbServer, this.dbPath,
				this.fileName);
	}

	public void saveData() {
		NAPIUtils.saveFile(this.dbServer, this.dbPath, this.fileName,
				this.fileData);
	}
}
