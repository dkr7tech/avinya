package com.common.minios;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import com.common.utils.SecurityUtils;

import io.minio.BucketExistsArgs;
import io.minio.DownloadObjectArgs;
import io.minio.GetBucketTagsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.ServerSideEncryptionCustomerKey;
import io.minio.errors.MinioException;
import io.minio.messages.Tags;

public class MinioTest {

	private static final String accessKey = "minioadmin";
	private static final String secretKey = "minioadmin";
	private static final String MINIO_ENDPOINT = "http://127.0.0.1:9000";

	private static final String BUCKET_NAME = "my-test-bucket";

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
		try {

			MinioClient minioClient = MinioClient.builder().endpoint(MINIO_ENDPOINT).credentials(accessKey, secretKey)
					.build();

			BucketExistsArgs args2 = BucketExistsArgs.builder().bucket(BUCKET_NAME).build();

			boolean isExist = minioClient.bucketExists(args2);

			if (isExist) {
				System.out.println("Bucket already exists.");
			} else {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
			}

			minioClient.listBuckets().forEach(b -> System.out.println(b.name()));

			URL url = new URL("https://images-na.ssl-images-amazon.com/images/I/81X4TID9IML._SL1500_.jpg");
			Path tempFilePath = Files.createTempFile("kid", ".jpg");
			try (InputStream in = url.openStream()) {
				Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
			}

			// minioClient.putObject(PutObjectArgs.builder().bucket("user1").object("Resume.pdf")
			// .stream(new FileInputStream("/tmp/Resume.pdf"), Files.size(tempFile),
			// -1).build());

			minioClient.putObject(PutObjectArgs.builder().bucket(BUCKET_NAME).object("kid.jpg")
					//.sse(SecurityUtils.getServerSideKey()).
					.stream(new FileInputStream(tempFilePath.toFile()), Files.size(tempFilePath), -1).build());

			try (InputStream stream = minioClient
					.getObject(GetObjectArgs.builder().bucket(BUCKET_NAME).object("kid.jpg").build())) {
				// Read the stream
			}
			   Tags tags =
				          minioClient.getBucketTags(GetBucketTagsArgs.builder().bucket("my-bucketname").build());
				      System.out.println("Bucket tags: " + tags.get());

		      {
		        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		        keyGen.init(256);
		        ServerSideEncryptionCustomerKey ssec =
		            new ServerSideEncryptionCustomerKey(keyGen.generateKey());

		        // Download SSE-C encrypted 'my-objectname' from 'my-bucketname' to 'my-filename'
		        minioClient.downloadObject(
		            DownloadObjectArgs.builder()
		                .bucket("my-bucketname")
		                .object("my-objectname")
		                .filename("my-filename")
		                .ssec(SecurityUtils.getServerSideKey()) // Replace with same SSE-C used at the time of upload.
		                .build());
		        System.out.println("my-objectname is successfully downloaded to my-filename");
		      }
			// Files.delete(tempFile);
		} catch (MinioException e) {
			System.out.println("Error occurred: " + e);
		}

	}

}