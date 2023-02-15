package org.kakaowork.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class CustomLogger {
    Logger logger = Logger.getLogger("customlogger");
    private static final CustomLogger instance = new CustomLogger();
    public static final String infoLog = "result.log";

    private CustomLogger() {
        FileHandler infoLogFile = null;
        try {
            infoLogFile = new FileHandler(infoLog, true);
        }catch(SecurityException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }

        infoLogFile.setFormatter(new java.util.logging.Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder sb = new StringBuilder();
                sb.append(
                                new SimpleDateFormat("[yyyy-MM-dd hh:mm:ss]")
                                        .format(new Date(record.getMillis())))
                        .append(" ").append(formatMessage(record))
                        .append(System.getProperty("line.separator"));
                if (record.getThrown() != null) {
                    try {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        record.getThrown().printStackTrace(pw);
                        pw.close();
                        sb.append(sw.toString());
                    } catch (Exception ex) {

                    }
                }
                return sb.toString();
            }
        });

        infoLogFile.setLevel(Level.INFO);
        logger.addHandler(infoLogFile);
    }

    public static CustomLogger getLogger() {
        return instance;
    }

    public void info(String msg) {
        logger.info(msg);
    }
}
