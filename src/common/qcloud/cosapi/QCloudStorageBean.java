package common.qcloud.cosapi;

import java.util.List;

import gamecore.util.DataUtils;

public class QCloudStorageBean {

	public Integer appId;
	public String secretId;
	public String secretKey;
	public String bucketNames;
	public List<String> bucketNameList;

	public QCloudStorageBean(Integer appId, String secretId, String secretKey, String bucketNames) {
		super();
		this.appId = appId;
		this.secretId = secretId;
		this.secretKey = secretKey;
		this.bucketNames = bucketNames;

		bucketNameList = DataUtils.string2Array(bucketNames, ",");

	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucketNames() {
		return bucketNames;
	}

	public void setBucketNames(String bucketNames) {
		this.bucketNames = bucketNames;
	}

}
