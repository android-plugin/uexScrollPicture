package org.zywx.wbpalmstar.plugin.uexscrollpicture;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexscrollpicture.VO.ConfigInfoVO;

import java.util.HashMap;
import java.util.List;

public class EUExScrollPicture extends EUExBase {

    private static final String BUNDLE_DATA = "data";
    private static final int MSG_CREATE_NEW_SCROLL_PICTURE = 1;
    private static final int MSG_START_AUTO_SCROLL = 2;
    private static final int MSG_STOP_AUTO_SCROLL = 3;
    private static final int MSG_REMOVE_VIEW = 4;

    private Gson mGson;


    public EUExScrollPicture(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
        mGson=new Gson();
    }

    @Override
    protected boolean clean() {
        return false;
    }


    public void createNewScrollPicture(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CREATE_NEW_SCROLL_PICTURE;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void createNewScrollPictureMsg(String[] params) {
        String json = params[0];
        final ConfigInfoVO config = mGson.fromJson(json,new TypeToken<ConfigInfoVO>(){}.getType());
        AutoScrollViewPager viewPager;
        MyViewPagerAdapter adapter;
        final List<String> urls;
        viewPager =new AutoScrollViewPager(mContext);
        urls = config.getUrls();
        adapter =new MyViewPagerAdapter(mContext, urls, config.getViewId());
        viewPager.setAdapter(adapter);
        adapter.setmCallBack(new MyViewPagerAdapter.OnItemPicCallBack() {

            @Override
            public void callBack(String viewId, int index) {
                HashMap<String, String> result = new HashMap<String, String>();
                result.put("viewId", config.getViewId());
                result.put("index", String.valueOf(index%urls.size()));
                callBackPluginJs(JsConst.ON_PIC_ITEM_CLICK, DataHelper.gson.toJson(result));
            }

        });

        int[] position=new int[2];
        if (config.getAnchor()!=null){
            position=config.getAnchor();
        }else{
            position[0]=0;
            position[1]=0;
        }
        viewPager.setInterval(config.getInterval());
        viewPager.setCycle(true);
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % urls.size());
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        AbsoluteLayout.LayoutParams layoutParams=new AbsoluteLayout.LayoutParams(config.getWidth(),config.getHeight(),position[0],position[1]);
        RelativeLayout rootLayout=getContainer();
        rootLayout.addView(viewPager);
        rootLayout.setTag(config.getViewId());
        final LinearLayout indicatorContainer =addIndicatorContainer(rootLayout);
        addIndicatorImg(0,urls.size(),indicatorContainer);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                addIndicatorImg(i%urls.size(),urls.size(),indicatorContainer);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mBrwView.addView(rootLayout,layoutParams);
    }

    private RelativeLayout getContainer(){
        RelativeLayout container=new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        container.setLayoutParams(layoutParams);
        return container;
    }

    private LinearLayout addIndicatorContainer(RelativeLayout parent){
        LinearLayout indicator=new LinearLayout(mContext);
//        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        indicator.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.setMargins(0, 0, 0, 20);
        parent.addView(indicator, layoutParams);
        return indicator;
    }

    private void addIndicatorImg(int index,int count,LinearLayout parent){
        parent.removeAllViews();
        for (int i=0;i<count;i++){
            ImageView imageView=new ImageView(mContext);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8,0,8,0);
            imageView.setLayoutParams(layoutParams);
            if (i==index){
                imageView.setBackgroundResource(EUExUtil.getResDrawableID("plugin_uexscrollpictrue_selected_pic"));
            }else{
                imageView.setBackgroundResource(EUExUtil.getResDrawableID("plugin_uexscrollpictrue_unselected_pic"));
            }
            parent.addView(imageView);
        }
    }

    public void startAutoScroll(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_START_AUTO_SCROLL;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void startAutoScrollMsg(String[] params) {
        String json = params[0];
        String viewId=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            viewId=jsonObject.optString("viewId");
        } catch (JSONException e) {
        }
        if (!TextUtils.isEmpty(viewId)){
            int viewCount=mBrwView.getChildCount();
            for (int i=viewCount-1;i>=0;i--){
                if (viewId.equals(mBrwView.getChildAt(i).getTag())){
                    AutoScrollViewPager autoScrollViewPager= (AutoScrollViewPager) ((ViewGroup)mBrwView.getChildAt(i)).getChildAt(0);
                    autoScrollViewPager.startAutoScroll();
                    break;
                }
            }
        }
    }

    public void stopAutoScroll(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_STOP_AUTO_SCROLL;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void stopAutoScrollMsg(String[] params) {
        String json = params[0];
        String viewId=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            viewId=jsonObject.optString("viewId");
        } catch (JSONException e) {
        }
        if (!TextUtils.isEmpty(viewId)){
            int viewCount=mBrwView.getChildCount();
            for (int i=viewCount-1;i>=0;i--){
                if (viewId.equals(mBrwView.getChildAt(i).getTag())){
                    AutoScrollViewPager autoScrollViewPager= (AutoScrollViewPager) ((ViewGroup)mBrwView.getChildAt(i)).getChildAt(0);
                    autoScrollViewPager.stopAutoScroll();
                    break;
                }
            }
        }
    }


    public void removeView(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_REMOVE_VIEW;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void removeViewMsg(String[] params) {
        String json = params[0];
        String viewId=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            viewId=jsonObject.optString("viewId");
        } catch (JSONException e) {
        }
        if (!TextUtils.isEmpty(viewId)){
            int viewCount=mBrwView.getChildCount();
            for (int i=viewCount-1;i>=0;i--){
                if (viewId.equals(mBrwView.getChildAt(i).getTag())){
                    mBrwView.removeView(mBrwView.getChildAt(i));
                    break;
                }
            }
        }
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case MSG_CREATE_NEW_SCROLL_PICTURE:
                createNewScrollPictureMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_START_AUTO_SCROLL:
                startAutoScrollMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_STOP_AUTO_SCROLL:
                stopAutoScrollMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_REMOVE_VIEW:
                removeViewMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            default:
                super.onHandleMessage(message);
        }
    }

    private void callBackPluginJs(String methodName, String jsonData){
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }

}
