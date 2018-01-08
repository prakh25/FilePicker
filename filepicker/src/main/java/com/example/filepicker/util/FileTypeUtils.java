package com.example.filepicker.util;

import android.webkit.MimeTypeMap;

import com.example.filepicker.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by prakh on 07-01-2018.
 */

public class FileTypeUtils {
    public enum FileType {
        DIRECTORY(R.drawable.fp_ic_folder_black_24dp, R.string.fp_type_directory),
        DOCUMENT(R.drawable.fp_ic_doc, R.string.fp_type_document),
        CERTIFICATE(R.drawable.fp_ic_cert, R.string.fp_type_certificate, "cer", "der", "pfx", "p12", "arm", "pem"),
        DRAWING(R.drawable.fp_ic_drawing, R.string.fp_type_drawing, "ai", "cdr", "dfx", "eps", "svg", "stl", "wmf", "emf", "art", "xar"),
        EXCEL(R.drawable.fp_ic_excel, R.string.fp_type_excel, "xls", "xlk", "xlsb", "xlsm", "xlsx", "xlr", "xltm", "xlw", "numbers", "ods", "ots"),
        IMAGE(R.drawable.fp_ic_image, R.string.fp_type_image, "bmp", "gif", "ico", "jpeg", "jpg", "pcx", "png", "psd", "tga", "tiff", "tif", "xcf"),
        MUSIC(R.drawable.fp_ic_music, R.string.fp_type_music, "aiff", "aif", "wav", "flac", "m4a", "wma", "amr", "mp2", "mp3", "wma", "aac", "mid", "m3u"),
        VIDEO(R.drawable.fp_ic_video, R.string.fp_type_video, "avi", "mov", "wmv", "mkv", "3gp", "f4v", "flv", "mp4", "mpeg", "webm"),
        PDF(R.drawable.fp_ic_pdf, R.string.fp_type_pdf, "pdf"),
        POWER_POINT(R.drawable.fp_ic_power_point, R.string.fp_type_power_point, "pptx", "keynote", "ppt", "pps", "pot", "odp", "otp"),
        WORD(R.drawable.fp_ic_word, R.string.fp_type_word, "doc", "docm", "docx", "dot", "mcw", "rtf", "pages", "odt", "ott"),
        ARCHIVE(R.drawable.fp_ic_archive, R.string.fp_type_archive, "cab", "7z", "alz", "arj", "bzip2", "bz2", "dmg", "gzip", "gz", "jar", "lz", "lzip", "lzma", "zip", "rar", "tar", "tgz"),
        APK(R.drawable.fp_ic_apk, R.string.fp_type_apk, "apk");

        private int icon;
        private int description;
        private String[] extensions;

        FileType(int icon, int description, String... extensions) {
            this.icon = icon;
            this.description = description;
            this.extensions = extensions;
        }

        public String[] getExtensions() {
            return extensions;
        }

        public int getIcon() {
            return icon;
        }

        public int getDescription() {
            return description;
        }
    }

    private static Map<String, FileType> fileTypeExtensions = new HashMap<>();

    static {
        for (FileType fileType : FileType.values()) {
            for (String extension : fileType.getExtensions()) {
                fileTypeExtensions.put(extension, fileType);
            }
        }
    }

    public static FileType getFileType(File file) {
        if (file.isDirectory()) {
            return FileType.DIRECTORY;
        }

        FileType fileType = fileTypeExtensions.get(getExtension(file.getName()));
        if (fileType != null) {
            return fileType;
        }

        return FileType.DOCUMENT;
    }

    public static String getExtension(String fileName) {
        String encoded;
        try {
            encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encoded = fileName;
        }
        return MimeTypeMap.getFileExtensionFromUrl(encoded).toLowerCase();
    }
}