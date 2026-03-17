//package org.example.tallemalle_backend.utils;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//@Component
//public class UploadUtil {
//    @Value("${project.upload.path}")
//    private String defaultUploadPath;
//
//    public String makeFolder() {
//        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
//
//        String folderPath = date.replace("/", File.separator);
//
//        File uploadPath = new File(defaultUploadPath + File.separator + folderPath);
//
//        if (!uploadPath.exists()) {
//            uploadPath.mkdirs();
//
//        }
//
//        return uploadPath.getPath();
//    }
//}