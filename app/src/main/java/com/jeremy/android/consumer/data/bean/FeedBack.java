package com.jeremy.android.consumer.data.bean;

import com.jeremy.android.consumer.utils.TimeUtils;

/**
 * 意见反馈实体类
 */
public class FeedBack {

    //反馈内容
    private String contents = "";

    //联系方式 (电话or邮箱)
    private String contact = "";

    //反馈时间
    private String feed_time = "";

    //apk版本
    private String version = "";

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFeed_time() {
        return feed_time;
    }

    public void setFeed_time(long feed_time) {
        this.feed_time = TimeUtils.getFormatByTimeStamp(feed_time);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
