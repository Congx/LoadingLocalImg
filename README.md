# LoadingLocalImg
超高仿微信图片选择


library-loadingLocalImg使用说明

1.直接使用默认的ImageGridActivity,选择后的结果会通过Intent返回给上一个activity,
  可以通过ImageGridActivity.RESULT_KEY,获取返回的路径集合
2.如果想要自定义自己的topbar,可以使用ListImgFragment,并实现OnResultListener获取返回的集合
  并且可以通过调用setMaxSize()方法设置允许选择的最大值
3. 默认最多选择九张图片
4. 

