package com.sample.utils;

import android.content.Context;

import com.sample.DefaultFileReaderWriter;
import com.sample.FileReaderWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;

/**
 * Wrapper around Android logcat for simpler use in UC application.
 */
public class LogUC {
    public static final Level DEFAULT_LEVEL = BuildConfig.DEBUG ? Level.TRACE : Level.DEBUG;
    private static Level sCurrentLevel = DEFAULT_LEVEL;
    private static final String LOGCAT_DUMP_LOG_NAME = "logcat_dump.log";
    private static final String DEFAULT_LOG_NAME = "EonLog.log";
    private static final String DEFAULT_ZIP_NAME = "EonLog_%i.zip";
    private static final int DEFAULT_ROLLBACK_LIMIT = 5;
    private static final String WATCHDOG_LOG_NAME = "EonWatchdog.log";
    private static final String WATCHDOG_ZIP_NAME = "EonWatchdogLog_%i.zip";
    private static final int WATCHDOG_ROLLBACK_LIMIT = 1;
    private static String logFolder;

    private LogUC() {
    }

    public static void d(String tag, String msg) {
        log(Level.DEBUG_INT, tag, msg);
    }

    public static void i(String tag, String msg) {
        log(Level.INFO_INT, tag, msg);
    }

    public static void e(String tag, String msg) {
        log(Level.ERROR_INT, tag, msg);
    }

    public static void v(String tag, String msg) {
        log(Level.TRACE_INT, tag, msg);
    }

    public static void w(String tag, String msg) {
        log(Level.WARN_INT, tag, msg);
    }

    public static void log(int level, String tag, String msg) {
        Logger log = LoggerFactory.getLogger(tag);
        switch (level) {
            case Level.DEBUG_INT:
                log.debug(msg);
                break;
            case Level.INFO_INT:
                log.info(msg);
                break;
            case Level.ERROR_INT:
                log.error(msg);
                break;
            case Level.TRACE_INT:
                log.trace(msg);
                break;
            case Level.WARN_INT:
                log.warn(msg);
                break;
            default:
                break;
        }
    }

    public static void saveCurrentLogcat(String logTag, Context context) {
        LogUC.i(logTag, "Preparing logcat dump file");
        FileReaderWriter fileReaderWriter = new DefaultFileReaderWriter(context);

        File logcatDumpFile = new File(LogUC.getLogFolder(), LOGCAT_DUMP_LOG_NAME);

        // delete previous Logcat file
        try {
            fileReaderWriter.deleteFile(logcatDumpFile);
        } catch (IOException e) {
            LogUC.w(logTag, "Could not delete logcat dump log file: " + e);
        }

        try {
            Runtime.getRuntime().exec("logcat -d -b all -f " + logcatDumpFile.getAbsolutePath()).waitFor();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            LogUC.w(logTag, "Exception with collecting logcat dump: " + e);
        }
    }

    /**
     * We have separate initializations for Main process and Watchdog process
     * because they cannot write to same log file.
     * This method should only be called from Main process.
     */
    public static void initDefaultLogger(String logPath) {
        initLogger(logPath, DEFAULT_LOG_NAME, DEFAULT_ZIP_NAME, DEFAULT_ROLLBACK_LIMIT);
    }

    /**
     * We have separate initializations for Main process and Watchdog process
     * because they cannot write to same log file.
     * This method should only be called from Watchdog process.
     * If Watchdog service is ever returned to Main process, this method should be removed.
     */
    public static void initWatchDogLogger(String logPath) {
        initLogger(logPath, WATCHDOG_LOG_NAME, WATCHDOG_ZIP_NAME, WATCHDOG_ROLLBACK_LIMIT);
    }

    public static void initLogger(String logPath, String logName, String zipName, int rollbackLimit) {
        logFolder = logPath;

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        // setup file appender
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setFile(logFolder + logName);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(context);

        // setup rolling policy for file appender
        FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
        fixedWindowRollingPolicy.setMinIndex(1);
        fixedWindowRollingPolicy.setMaxIndex(rollbackLimit);
        fixedWindowRollingPolicy.setFileNamePattern(logFolder + zipName);
        fixedWindowRollingPolicy.setParent(rollingFileAppender);
        fixedWindowRollingPolicy.setContext(context);

        // setup triggering policy for file appender
        SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy<>();
        sizeBasedTriggeringPolicy.setMaxFileSize("5MB");
        sizeBasedTriggeringPolicy.setContext(context);

        // set and start policies for file appender
        fixedWindowRollingPolicy.start();
        sizeBasedTriggeringPolicy.start();
        rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);
        rollingFileAppender.setTriggeringPolicy(sizeBasedTriggeringPolicy);

        //set log pattern for file appender
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread]: %logger - %msg%n");
        encoder.setContext(context);
        encoder.start();

        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        LogcatAppender logcatAppender = null;

        if (BuildConfig.DEBUG) {
            // setup message pattern for logcat appender
            PatternLayoutEncoder logcatEncoder = new PatternLayoutEncoder();
            logcatEncoder.setPattern("%msg");
            logcatEncoder.setContext(context);
            logcatEncoder.start();

            // setup tag pattern for logcat appender
            PatternLayoutEncoder logcatTagEncoder = new PatternLayoutEncoder();
            logcatTagEncoder.setPattern("%logger");
            logcatTagEncoder.setContext(context);
            logcatTagEncoder.start();

            // set patterns and start logcat appender
            logcatAppender = new LogcatAppender();
            logcatAppender.setContext(context);
            logcatAppender.setEncoder(logcatEncoder);
            logcatAppender.setTagEncoder(logcatTagEncoder);
            logcatAppender.start();
        }

        // set the root logger to use both appenders
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)
                LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(sCurrentLevel);
        root.addAppender(rollingFileAppender);
        if (logcatAppender != null) {
            root.addAppender(logcatAppender);
        }
    }

    public static Level getCurrentLevel() {
        return sCurrentLevel;
    }

    public static void setCurrentLevel(Level currentLevel) {
        if (currentLevel == null) {
            return;
        }
        sCurrentLevel = currentLevel;
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)
                LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(sCurrentLevel);
    }

    public static String getLogFolder() {
        return logFolder;
    }
}