package com.sample;

import java.io.File;
import java.io.IOException;

public interface FileReaderWriter {

    /**
     * Write contents to a file.
     * @param filePath relative file path in cache folder
     * @param data contents to write to a file
     */
    void writeFileToCache(String filePath, byte[] data);

    /**
     * Write contents to a file.
     * @param filePath relative file path in cache folder
     * @param data contents to write to a file
     */
    void writeFileToCache(String filePath, String data);

    /**
     * Write contents to a file.
     * @param filePath absolute file path
     * @param data contents to write to a file
     */
    void writeFile(String filePath, byte[] data);

    /**
     * Write contents to a file.
     * @param filePath absolute file path
     * @param data contents to write to a file
     */
    void writeFile(String filePath, String data);

    /**
     * Read file contents.
     * @param filePath  file path
     * @return file contents as a String object
     */
    String readFile(String filePath);

    /**
     * Read file contents.
     * @param filePath  absolute file path
     * @return file contents as a String object
     */
    String readFileFromAbsolutePath(String filePath);

    /**
     * Copy folder from srcPath to outputPath.
     * @param srcFolder folder to copy from
     * @param outputFolder folder in which new folder will be created
     * @throws IOException exception is thrown in case of an error
     */
    void copyFolder(File srcFolder, File outputFolder) throws IOException;

    /**
     * Delete folder.
     * @param folder path to delete
     * @throws IOException exception is thrown if error occurs
     */
    void deleteFolder(File folder) throws IOException;

    /**
     * Delete file with provided path.
     * @param file file
     * @throws IOException exception is thrown if error occurs
     */
    void deleteFile(File file) throws IOException;

    /**
     * Zip provided folder to a specified path.
     * @param inputFolder folder to zip
     * @param outZip newly created .zip file
     * @throws IOException exception is thrown in case of an error
     */
    void zipFolder(File inputFolder, File outZip) throws IOException;

    /**
     * Retrieve file from content provider and save it locally.
     * @param fileUri string containing file Uri
     * @param destinationFile new file
     * @throws IOException exception is thrown in case of an error
     */
    void copyFileFromContentProvider(String fileUri, File destinationFile) throws IOException;
}
