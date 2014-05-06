package com.hillstone.hsa.index.domain;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-17
 * Time: 下午2:21
 * To change this template use File | Settings | File Templates.
 */
public class LogObj {
    private long    date;
    private String  log;
    private String type;
    private String sn;
    private String severity;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
