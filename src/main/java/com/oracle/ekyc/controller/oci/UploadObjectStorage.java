package com.oracle.ekyc.controller.oci;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;

public class UploadObjectStorage {

        
      
        public String upload(MultipartFile file) throws Exception {
             
                        PutObjectRequest putObjectRequest = null;

                      
                        System.out.println("Authenticating...");
                     

                        ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
                        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(
                                        configFile);

                        ObjectStorage client = ObjectStorageClient.builder().build(provider);

 
                        String objectName = file.getOriginalFilename();
                        System.out.println("Loading the file to Object Storage with name: " + objectName);

                        
                        InputStream inputStream = file.getInputStream();

                        System.out.println("Creating the source object to send");
                        System.out.println(file.getSize());
                        putObjectRequest = PutObjectRequest.builder()
                                        .namespaceName("bm3n5p8nor1r")
                                        .bucketName("ekyc-bucket")
                                        .objectName(objectName)
                                        .contentLength(file.getSize())
                                        .putObjectBody(inputStream)
                                        .build();
                        client.putObject(putObjectRequest);

                        System.out.println("Response: " + putObjectRequest);
                        client.close();
                        return putObjectRequest.toString();
                
        }
}
