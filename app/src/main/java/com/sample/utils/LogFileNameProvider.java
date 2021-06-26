package com.sample.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LogFileNameProvider {
    private static final String EON_TV_LOG_NAME = "EonTVLog.zip";
    private static final String CUSTOMIZATION_APP_LOG_NAME = "EonCustomizationLog.zip";
    private static final String OTA_LOG_NAME = "EonOTALog.zip";
    private static final String FTU_LOG_NAME = "EonFTULog.zip";
    private static final String RCU_LOG_NAME = "EonRCULog.zip";
    private static final String SYSTEM_SERVICE_LOG_NAME = "EonSystemServiceLog.zip";
    private static final String CDN_RESOLVER_LOG_NAME = "EonCDN.zip";
    private static final String QOE_AGENT_LOG_NAME = "EonQoE.zip";
    private static final String RAC_LOG_NAME = "EonRAC.zip";

    private static final String EON_TV_LOG_URI = "content://com.ug.eon.android.tv.logprovider/logs";
    private static final String CUSTOMIZATION_APP_LOG_URI =
            "content://com.ug.eon.android.tv.customizationapp.logprovider/logs";
    private static final String OTA_LOG_URI = "content://com.ug.eon.android.tv.eonotaupdater.logprovider/logs";
    private static final String FTU_LOG_URI = "content://com.ug.eon.android.tv.ftu.logprovider/logs";
    private static final String RCU_LOG_URI = "content://com.ug.eon.rcu.t4h.logprovider/logs";
    private static final String SYSTEM_SERVICE_LOG_URI = "content://com.ug.eon.systemservice.logprovider/logs";
    private static final String CDN_RESOLVER_URI = "content://com.ug.eon.cdnresolver.logprovider/logs";
    private static final String QOE_AGENT_URI = "content://com.ug.eon.qoeagent.logprovider/logs";
    private static final String RAC_URI = "content://com.ug.eon.rac.logprovider/logs";

    private static final Map<String, String> allLogs = new HashMap<>();

    private static final String LOG_DATE_FORMAT = "yyyy-MM-dd--HH-mm-ss";

    static {
        allLogs.put(EON_TV_LOG_NAME, EON_TV_LOG_URI);
        allLogs.put(CUSTOMIZATION_APP_LOG_NAME, CUSTOMIZATION_APP_LOG_URI);
        allLogs.put(OTA_LOG_NAME, OTA_LOG_URI);
        allLogs.put(FTU_LOG_NAME, FTU_LOG_URI);
        allLogs.put(RCU_LOG_NAME, RCU_LOG_URI);
        allLogs.put(SYSTEM_SERVICE_LOG_NAME, SYSTEM_SERVICE_LOG_URI);
        allLogs.put(CDN_RESOLVER_LOG_NAME, CDN_RESOLVER_URI);
        allLogs.put(QOE_AGENT_LOG_NAME, QOE_AGENT_URI);
        allLogs.put(RAC_LOG_NAME, RAC_URI);
    }

    public Map<String, String> getAllLogs() {
        return allLogs;
    }

    public String getLogFileName(String deviceSerial) {
        return "EonLogs_" + getCurrentDate() +
                "_" + deviceSerial + ".zip";
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat(LOG_DATE_FORMAT, Locale.US);
        return dateFormat.format(new Date());
    }
}
