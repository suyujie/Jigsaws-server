package common.aws;

public class AwsS3StorageBean {

	public String cdnUrl;
	public String bucketName;
	public String accessUrl;
	public String accessId;
	public String accessKey;

	public AwsS3StorageBean(String cdnUrl, String bucketName, String accessUrl, String accessId, String accessKey) {
		this.cdnUrl = cdnUrl;
		this.bucketName = bucketName;
		this.accessUrl = accessUrl;
		this.accessId = accessId;
		this.accessKey = accessKey;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getCdnUrl() {
		return cdnUrl;
	}

	public void setCdnUrl(String cdnUrl) {
		this.cdnUrl = cdnUrl;
	}

	public String getAccessUrl() {
		return accessUrl;
	}

	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

}
