package com.spring.app.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

// 파일 업로드/다운로드/삭제를 담당하는 공통 유틸 클래스이다.
@Component
public class FileManager {

    // 파일 확장자를 포함한 서버 저장용 고유 파일명을 생성한다.
    private String makeNewFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return null;
        }

        // 파일명에 포함될 수 있는 불필요한 경로 정보를 제거한다.
        String cleanOriginalFilename = StringUtils.cleanPath(originalFilename);

        // ../ 와 같은 경로 조작 문자가 포함된 파일명은 저장하지 않는다.
        if (cleanOriginalFilename.contains("..")) {
            return null;
        }

        int dotIndex = cleanOriginalFilename.lastIndexOf(".");
        if (dotIndex < 0 || dotIndex == cleanOriginalFilename.length() - 1) {
            return null;
        }

        String fileExt = cleanOriginalFilename.substring(dotIndex);
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return now + "_" + uuid + fileExt;
    }

    // 문자열 경로를 OS 독립적인 Path 객체로 변환하고, 실제 디렉토리가 없으면 생성한다.
    private Path prepareUploadDir(String path) throws IOException {
        Path uploadDir = Paths.get(path).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);
        return uploadDir;
    }

    // byte[] 기반 파일 업로드.
    public String doFileUpload(byte[] bytes, String originalFilename, String path) throws IOException {
        if (bytes == null) {
            return null;
        }

        String newFileName = makeNewFileName(originalFilename);
        if (newFileName == null) {
            return null;
        }

        // 문자열 결합 대신 Path.resolve()를 사용해 macOS/Windows/Linux 모두 안전하게 경로를 조합한다.
        Path uploadDir = prepareUploadDir(path);
        Path targetPath = uploadDir.resolve(newFileName).normalize();

        // resolve 이후에도 최종 경로가 업로드 디렉토리 밖으로 나가지 못하도록 한 번 더 방어한다.
        if (!targetPath.startsWith(uploadDir)) {
            return null;
        }

        Files.write(targetPath, bytes);
        return newFileName;
    }

    // InputStream 기반 파일 업로드. 스마트에디터 이미지 업로드 등에서 사용할 수 있다.
    public String doFileUpload(InputStream is, String originalFilename, String path) throws IOException {
        if (is == null) {
            return null;
        }

        String newFileName = makeNewFileName(originalFilename);
        if (newFileName == null) {
            return null;
        }

        // 문자열 결합 대신 Path.resolve()를 사용해 OS별 구분자 문제를 제거한다.
        Path uploadDir = prepareUploadDir(path);
        Path targetPath = uploadDir.resolve(newFileName).normalize();

        // 경로 조작 방어 코드이다.
        if (!targetPath.startsWith(uploadDir)) {
            return null;
        }

        try (InputStream input = is; OutputStream output = Files.newOutputStream(targetPath)) {
            byte[] buffer = new byte[1024];
            int size;
            while ((size = input.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            output.flush();
        }

        return newFileName;
    }

    // 파일 다운로드.
    public boolean doFileDownload(String fileName, String originalFilename, String path, HttpServletResponse response) {
        try {
            Path uploadDir = Paths.get(path).toAbsolutePath().normalize();
            Path targetPath = uploadDir.resolve(fileName).normalize();

            // 다운로드 대상 파일이 지정된 업로드 디렉토리 밖으로 벗어나지 못하도록 방어한다.
            if (!targetPath.startsWith(uploadDir) || !Files.exists(targetPath)) {
                return false;
            }

            if (originalFilename == null || originalFilename.isBlank()) {
                originalFilename = fileName;
            }

            try {
                originalFilename = new String(originalFilename.getBytes("UTF-8"), "8859_1");
            } catch (UnsupportedEncodingException e) {
                originalFilename = fileName;
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=" + originalFilename);

            byte[] readByte = new byte[4096];

            try (BufferedInputStream bfin = new BufferedInputStream(new FileInputStream(targetPath.toFile()));
                 ServletOutputStream souts = response.getOutputStream()) {

                int length;
                while ((length = bfin.read(readByte, 0, 4096)) != -1) {
                    souts.write(readByte, 0, length);
                }
                souts.flush();
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 파일 삭제.
    public void doFileDelete(String filename, String path) throws IOException {
        Path uploadDir = Paths.get(path).toAbsolutePath().normalize();
        Path targetPath = uploadDir.resolve(filename).normalize();

        // 삭제도 업로드 디렉토리 내부 파일만 허용한다.
        if (targetPath.startsWith(uploadDir)) {
            Files.deleteIfExists(targetPath);
        }
    }
}
