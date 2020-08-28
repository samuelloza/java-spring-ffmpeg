package example.prog102.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.SocketUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WelcomeController {
	public final static String EXECUTABLE = "/usr/bin/ffmpeg";

	@Value("${upload.path}")
	private String path;

	@Value("${storage.path}")
	private String storage;

	@RequestMapping(method = RequestMethod.POST, value = "/video-converter")

	public String sayHelloPostAtributesWithFile(@RequestParam("file") MultipartFile file,
			@RequestParam("comands") String comads) {
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
			Process processDuration = new ProcessBuilder("ffmpeg", "-i", source, target).redirectErrorStream(true).start();
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