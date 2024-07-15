package org.abx.virturalpet.configuration;

import com.adobe.testing.s3mock.junit5.S3MockExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@ExtendWith(S3MockExtension.class)
@TestConfiguration
public class S3MockConfig {

}
