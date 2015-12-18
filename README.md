## uexScrollPicture 轮播图接口文档


### 1.设置配置信息
```
setConfigInfo(param)
var param={
	interval:,//自动滚动的间隔时间，单位为毫秒，默认3000
	animaId:,//动画ID，0-没有动画，1-3D效果，
	urls:,//List<String> 的json字符串
	viewId:,//轮播图id，用于删除
};

```

### 2.开始图片轮播
```
startAutoScroll();
```

### 3.停止图片轮播
```
stopAutoScroll();
```

### 4.删除view
```
removeView(param)
var param={
	viewId://
}
```

### 5.轮播图点击事件
```
onPicItemClick(param)
var param={
	index:,//第几个图片被点击，从0开始
	url:,//被点击图片的url	
}
```

