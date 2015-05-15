package org.zywx.wbpalmstar.plugin.uexscrollpicture;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.zywx.wbpalmstar.base.ACEImageLoader;

import java.util.List;

/**
 * Created by ylt on 2015/4/28.
 */
public class MyViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> urls;
    private OnItemPicCallBack mCallBack;
    private String mViewId;

    public MyViewPagerAdapter(Context context,List<String> urls,String viewId){
        this.mContext=context;
        this.urls=urls;
        this.mViewId=viewId;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    private int getPosition(int position) {
        return position % urls.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof ImageView){
            ImageView imageView= (ImageView) object;
            imageView.setImageBitmap(null);
            container.removeView(imageView);
            imageView=null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView=new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(imageView);
        ACEImageLoader.getInstance().displayImage(imageView, urls.get(getPosition(position)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack!=null){
                    mCallBack.callBack(mViewId,position);
                }
            }
        });
        return imageView;
    }

    public void setmCallBack(OnItemPicCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface OnItemPicCallBack{
        void callBack(String viewId,int index);
    }
}
