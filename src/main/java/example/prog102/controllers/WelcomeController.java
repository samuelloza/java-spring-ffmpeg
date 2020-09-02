package example.prog102.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WelcomeController {
  static final String EXECUTABLE_NAME = "/usr/bin/ffmpeg";

  @Value("${upload.path}")
  private String path;

  @Value("${storage.path}")
  private String storage;
  /**
  * displays hello world without param
  */

  @RequestMapping(method = RequestMethod.POST, value = "/video-converter")
  public String sayHelloPostAtributesWithFile(@RequestParam("file") final MultipartFile file,
      @RequestParam("comands") final String comads) {
    String fileName = "";
    String source = "";
    String target = "";
    try {
      fileName = file.getOriginalFilename();
      source = path + fileName;
      target = storage + file.getOriginalFilename().replaceFirst("[.][^.]+$", "") + ".avi";
      Files.copy(file.getInputStream(), Paths.get(source), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      return "Ups error to obtain data of the file";
    }

    try {
      Process processDuration = new ProcessBuilder(EXECUTABLE_NAME, "-i", source, target).redirectErrorStream(true).start();
      StringBuilder outPut = new StringBuilder();
      BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(processDuration.getInputStream()));
      String line;
      while ((line = processOutputReader.readLine()) != null) {
        outPut.append(line + System.lineSeparator());
      }
      processDuration.waitFor();
      return outPut.toString().trim();
    } catch (Exception e) {
      return "Error to convert file";
    }
  }
}
