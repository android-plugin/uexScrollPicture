package org.zywx.wbpalmstar.plugin.uexscrollpicture.VO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ylt on 2015/4/28.
 */
public class ConfigInfoVO implements Serializable {
    private static final long serialVersionUID = 2652231287567742609L;

    private int interval=1500;//自动滚动的间隔时间，单位为毫秒，默认3000
    private int slideBorderMode=0;
    private int animaId=0;
    private int width=0;//轮播图宽度
    private int height=0;//
    private int[] anchor;
    private String viewId;
    private List<String> urls;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getSlideBorderMode() {
        return slideBorderMode;
    }

    public void setSlideBorderMode(int slideBorderMode) {
        this.slideBorderMode = slideBorderMode;
    }

    public int getAnimaId() {
        return animaId;
    }

    public void setAnimaId(int animaId) {
        this.animaId = animaId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public int[] getAnchor() {
        return anchor;
    }

    public void setAnchor(int[] anchor) {
        this.anchor = anchor;
    }
}
