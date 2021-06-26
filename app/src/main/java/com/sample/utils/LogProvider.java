package com.sample.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import kotlin.NotImplementedError;

public class LogProvider extends ContentProvider {
    private static final String TAG = LogProvider.class.getName();
    private static final String LOGS_DATA = "logs";
    private static final int MATCH_LOGS = 1;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


//    public static String getUriMatchers() {
//
//    }

    public static void addUriMatchers(String... args) {
        Arrays.stream(args).distinct().forEach(logAuthority -> {
            sUriMatcher.addURI(logAuthority, LOGS_DATA, MATCH_LOGS);
        });
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) {

        if (sUriMatcher.match(uri) != MATCH_LOGS) {
            LogUC.w(TAG, "Uri not matched");
            return null;
        }
        LogUC.d(TAG, "Logs requested");

        if (getContext() == null) {
            LogUC.w(TAG, "Context missing");
            return null;
        }

        try {
            PipeDataWriter writer = (output, uri1, mimeType, opts, args) -> {
                try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output.getFileDescriptor()))) {
                    String logFolder = LogUC.getLogFolder();
                    if (logFolder == null) {
                        LogUC.w(TAG, "Could not find Log folder");
                        return;
                    }
                    File logDir = new File(logFolder);

                    File[] logFiles = logDir.listFiles();
                    if (logFiles == null) {
                        LogUC.w(TAG, "Log files list is null");
                        return;
                    }

                    for (File file : logFiles) {
                        byte[] buffer = new byte[1024];
                        try (FileInputStream fis = new FileInputStream(file)) {
                            out.putNextEntry(new ZipEntry(file.getName()));
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                out.write(buffer, 0, length);
                            }
                            out.closeEntry();
                        }
                    }

                } catch (IOException e) {
                    LogUC.e(TAG, "Failed to send file " + uri1 + ": " + e);
                }
            };
            return openPipeHelper(uri, "application/zip", null, null, writer);
        } catch (IOException e) {
            LogUC.e(TAG, "Log provider exception: " + e);
        }
        return null;
    }

    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}

