package common.aws;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AwsS3 {

	private static Logger logger = LogManager.getLogger(AwsS3.class.getName());

	public static boolean upload(AwsS3StorageBean storageBean, String key, File file) throws IOException {

		AmazonS3 s3 = new AmazonS3Client(
				new BasicAWSCredentials(storageBean.getAccessId(), storageBean.getAccessKey()));

		Region usWest2 = Region.getRegion(Regions.AP_SOUTHEAST_1);
		s3.setRegion(usWest2);

		try {

			s3.putObject(new PutObjectRequest(storageBean.getBucketName(), key, file));

			ObjectListing objectListing = s3
					.listObjects(new ListObjectsRequest().withBucketName(storageBean.getBucketName()).withPrefix("My"));
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				System.out.println(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
			}
			System.out.println();

		} catch (AmazonServiceException ase) {
			logger.error("Error Message:    " + ase.getMessage());
		} catch (AmazonClientException ace) {
			logger.error("Error Message: " + ace.getMessage());
		}

		return true;
	}

}
