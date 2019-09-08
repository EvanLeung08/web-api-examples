package com.eshare.api.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * Rest API for File Download
 *
 * Created by liangyh on 2019/9/8. Email:10856214@163.com
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class RestDownloadController {


  @GetMapping(value = "/download", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StreamingResponseBody> download(final HttpServletResponse response) {

    String fileName = "test.zip";
    String outputFile = "samples/test.txt";//samples
    response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getType());
    response.setHeader(
        "Content-Disposition",
        "attachment;filename=" + fileName);

    StreamingResponseBody stream;
    stream = out -> {
      final URL filePath = this.getClass().getClassLoader().getResource(outputFile);
      handleFileStream(response.getOutputStream(), filePath);
    };
    log.info("steaming response {} ", stream);
    return new ResponseEntity(stream, HttpStatus.OK);
  }

  /**
   * Write files into outputstream
   */
  private void handleFileStream(OutputStream outputStream, URL filePath) throws IOException {
    final File directory = ResourceUtils.getFile(filePath);
    if (directory.exists() && directory.isDirectory()) {
      final ZipOutputStream zipOut = new ZipOutputStream(outputStream);
      for (final File file : directory.listFiles()) {
        try (InputStream inputStream = new FileInputStream(file)) {
          final ZipEntry zipEntry = new ZipEntry(file.getName());
          zipOut.putNextEntry(zipEntry);
          byte[] bytes = new byte[1024];
          int length;
          while ((length = inputStream.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
          }
        }
      }
      IOUtils.closeQuietly(zipOut);
    } else {
      try (final InputStream inputStream = new FileInputStream(ResourceUtils.getFile(filePath))) {
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes)) >= 0) {
          outputStream.write(bytes, 0, length);
        }
      }
    }

  }

}
