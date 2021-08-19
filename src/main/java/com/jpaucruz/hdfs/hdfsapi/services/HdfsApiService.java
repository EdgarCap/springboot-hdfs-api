package com.jpaucruz.hdfs.hdfsapi.services;

import com.jpaucruz.hdfs.hdfsapi.configuration.HdfsConfiguration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.io.File;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;


@Service
public class HdfsApiService {
  
  private HdfsConfiguration hdfsConfiguration;
  
  @Autowired
  public HdfsApiService(HdfsConfiguration hdfsConfiguration){
    this.hdfsConfiguration = hdfsConfiguration;
  }
  
  public void createFolder(String folderName) throws IOException{
  
    FileSystem hdfsFS = FileSystem.get(hdfsConfiguration.config());
    Path folderPath = new Path(hdfsFS.getHomeDirectory().toString() + "/" + folderName);
    
    if (!hdfsFS.exists(folderPath)) {
      hdfsFS.mkdirs(folderPath);
      hdfsFS.close();
    }else{
      hdfsFS.close();
      throw new HttpServerErrorException(
        "The folder " + folderPath.toUri().getPath() + " already exists", HttpStatus.BAD_REQUEST, "", new HttpHeaders(), null, null
      );
    }
    
  }
  
  public void createFile(String folderName, String fileName) throws IOException{
    
    FileSystem hdfsFS = FileSystem.get(hdfsConfiguration.config());
    Path folderPath = new Path(hdfsFS.getHomeDirectory().toString() + "/" + folderName);
  
    if (hdfsFS.exists(folderPath)) {
    
      Path filePath = new Path(folderPath + "/" + fileName);
    
      if (!hdfsFS.exists(filePath)){
        hdfsFS.create(filePath);
        hdfsFS.close();
      }else{
        hdfsFS.close();
        throw new HttpServerErrorException(
          "The file " + fileName + " already exists in folder " + folderPath.toUri().getPath(), HttpStatus.BAD_REQUEST, "", new HttpHeaders(), null, null
        );
      }
    
    }else{
      hdfsFS.close();
      throw new HttpServerErrorException(
        "The folder " + folderPath.toUri().getPath() + " does not exist", HttpStatus.BAD_REQUEST, "", new HttpHeaders(), null, null
      );
    }
    
  }

public void deleteFile(String folderName, String fileName) throws IllegalArgumentException, IOException {
    FileSystem hdfsFS = FileSystem.get(hdfsConfiguration.config());
    Path folderPath = new Path(hdfsFS.getHomeDirectory().toString() + "/" + folderName);
  
    if (hdfsFS.exists(folderPath)) {
    
      Path filePath = new Path(folderPath + "/" + fileName);
    
      hdfsFS.delete(filePath, false);

      }
    
    else{
      hdfsFS.close();
      System.out.println(folderPath.toUri().getPath());
      throw new HttpServerErrorException(
        "The folder " + folderPath.toUri().getPath() + " does not exist", HttpStatus.BAD_REQUEST, "", new HttpHeaders(), null, null
      );
    }
    
  }
  public void insertfile(MultipartFile fileinput, String fileName) throws IOException{
	    FileSystem hdfsFS = FileSystem.get(hdfsConfiguration.config());
        File convFile =new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        Path conFilePath = new Path(System.getProperty("java.io.tmpdir")+"/"+fileName);
        System.out.println( convFile.toString());
        Path folderPath = new Path(hdfsFS.getHomeDirectory().toString());
	    Path filePath = new Path(folderPath + "/" + fileName);
	    System.out.println("gigiovive");
	    try {
			fileinput.transferTo(convFile);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    hdfsFS.copyFromLocalFile(false, conFilePath, filePath); 
  }
}
