package com.sample;

import android.content.Context;
import android.net.Uri;

import com.sample.utils.LogUC;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DefaultFileReaderWriter implements FileReaderWriter {

    private static final String TAG = DefaultFileReaderWriter.class.getName();
    private Context mContext;

    public DefaultFileReaderWriter(Context context) {
        mContext = context;
    }

    @Override
    public void writeFile(String filePath, byte[] data) {
        File file = new File(filePath);
        if (!file.isAbsolute()) {
            LogUC.w(TAG, "Path is relative, cannot create");
            return;
        }

        try {
            internalWriteFile(createFile(file), data);
        } catch (IOException e) {
            LogUC.e(TAG, "File write failed: " + e.toString());
        }
    }

    @Override
    public void writeFile(String filePath, String data) {
        writeFile(filePath, data.getBytes());
    }

    @Override
    public void writeFileToCache(String filePath, byte[] data) {
        try {
            internalWriteFile(createFileInCache(filePath), data);
        } catch (IOException e) {
            LogUC.e(TAG, "File write failed: " + e.toString());
        }
    }

    @Override
    public void writeFileToCache(String filePath, String data) {
        writeFileToCache(filePath, data.getBytes());
    }

    private void internalWriteFile(File file, byte[] data) throws IOException {
        if (file == null) {
            LogUC.w(TAG, "file is null");
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(data);
        }
    }

    private File createFileInCache(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            LogUC.w(TAG, "file name is empty");
            return null;
        }

        if (!filePath.startsWith("/")) {
            filePath = File.separator + filePath;
        }

        File file = new File(mContext.getCacheDir(), filePath);
        file.getParentFile().mkdirs();
        return file.getParentFile().exists() ? file : null;
    }

    private File createFile(File file) {
        file.getParentFile().mkdirs();
        return file.getParentFile().exists() ? file : null;
    }

    @Override
    public String readFile(String filePath) {
        File file = new File(mContext.getCacheDir(), filePath);
        return readFileFromAbsolutePath(file.getAbsolutePath());
    }

    @Override
    public String readFileFromAbsolutePath(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();

        File file = new File(filePath);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }
        } catch (FileNotFoundException e) {
            LogUC.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            LogUC.e(TAG, "Can not read file: " + e.toString());
        }

        return stringBuilder.toString();
    }

    @Override
    public void copyFolder(File srcFolder, File outputFolder) throws IOException {
        FileUtils.copyDirectory(srcFolder, outputFolder);
    }

    @Override
    public void deleteFolder(File folder) {
        deleteRecursive(folder);
    }

    @Override
    public void deleteFile(File file) {
        deleteRecursive(file);
    }

    @SuppressWarnings("java:S4042")
    //suppressed warning because Files.delete() is available only from SDK 26 (minSdkVersion is currently 21)
    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        if (!fileOrDirectory.delete()) {
            LogUC.w(TAG, "File could not be deleted: " + fileOrDirectory.getAbsolutePath());
        }
    }

    @Override
    public void zipFolder(File inputFolder, File outZip) throws IOException {
        LogUC.d(TAG, "Zipping to file: " + outZip.getAbsolutePath());

        FileOutputStream fos = new FileOutputStream(outZip);
        try (ZipOutputStream zos = new ZipOutputStream(fos)) {
            File[] files = inputFolder.listFiles();

            if (files == null) {
                throw new IOException("File list empty, nothing to zip");
            }

            for (File file : files) {
                byte[] buffer = new byte[1024];
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    @Override
    public void copyFileFromContentProvider(String fileUri, File destinationFile) throws IOException {
        try (InputStream input = mContext.getContentResolver().openInputStream(Uri.parse(fileUri))) {
            if (input == null) {
                return;
            }
            File outputFile = createFile(destinationFile);
            try (FileOutputStream output = new FileOutputStream(outputFile)) {
                copyStream(input, output);
            }
        }
    }

    private void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[1024];
        int num;
        while ((num = is.read(buf)) != -1) {
            os.write(buf, 0, num);
        }
    }
}
