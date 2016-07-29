package org.zywx.wbpalmstar.plugin.uexscrollpicture;

import android.content.Context;
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

    private Gson mGson;
    private int mCurrentId = 0;


    public EUExScrollPicture(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
        mGson=new Gson();
    }

    @Override
    protected boolean clean() {
        if (mBrwView!=null) {
            int viewCount = mBrwView.getChildCount();
            for (int i = viewCount - 1; i >= 0; i--) {
                if (!(mBrwView.getChildAt(i) instanceof ViewGroup)){
                    continue;
                }
                if (((ViewGroup)mBrwView.getChildAt(i)).getChildAt(0) instanceof AutoScrollViewPager) {
                    AutoScrollViewPager autoScrollViewPager = (AutoScrollViewPager) ((ViewGroup) mBrwView.getChildAt(i)).getChildAt(0);
                    autoScrollViewPager.stopAutoScroll();
                }
            }
            mBrwView.removeAllViews();
        }
        return false;
    }

    public String createNewScrollPicture(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return null;
        }
        String json = params[0];
        final ConfigInfoVO config = mGson.fromJson(json,new TypeToken<ConfigInfoVO>(){}.getType());
        if (TextUtils.isEmpty(config.getViewId())){
            config.setViewId(generateId());
        }
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
        addIndicatorImg(0, urls.size(), indicatorContainer);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                addIndicatorImg(i % urls.size(), urls.size(), indicatorContainer);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mBrwView.addView(rootLayout,layoutParams);
        return config.getViewId();
    }

    private String generateId(){
        mCurrentId++;
        return String.valueOf(mCurrentId);
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
            if (mContext==null){
                return;
            }
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

    private void callBackPluginJs(String methodName, String jsonData){
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }

}
