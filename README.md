<div align="center">


<h1>  ocrtest (文字识别) </h1>

#### A android software integrating functions such as [Baidu AI](https://console.bce.baidu.com/ai/?_=1535192109895#/ai/ocr/report/index) and [IFLYTEK's speech synthesis](https://console.bce.baidu.com/ai/?_=1535192109895#/ai/ocr/report/index).

![ocrtest_logo.png](https://upload-images.jianshu.io/upload_images/9140378-99d2976eb46f7698.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

<br />
<a href="https://www.pgyer.com/9NY0">点击下载</a>
<br />

---

## Functional demo(功能演示)
|通用文字识别|高精度文字识别|声音切换|拍照截取界面|
|:--:|:--:|:--:|:--:|
|![2.png](https://upload-images.jianshu.io/upload_images/9140378-ee868ffaaca08e5d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)|![3.png](https://upload-images.jianshu.io/upload_images/9140378-954807f2205d4984.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)|![4.gif](https://upload-images.jianshu.io/upload_images/9140378-f0cf9edbd55c05fa.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)|![1.png](https://upload-images.jianshu.io/upload_images/9140378-0d7811229cba0ddc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)|

---

## Third party libraries used(使用的第三方库)
|文字识别数据解析|图片加载|悬浮弹框按钮|底部悬浮框|
|:--:|:--:|:--:|:--:|
| [Fastjson](https://github.com/alibaba/fastjson)|[Glide](https://github.com/bumptech/glide)|[FloatingActionButton](https://github.com/Clans/FloatingActionButton)|[BottomSheet](https://github.com/soarcn/BottomSheet)|

### The dependencies added by this project(添加的依赖)

</div>

```java
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation 'com.android.support:appcompat-v7:27.1.1'
    implementation 'com.android.support.constraint:constraint-layout:1.1.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation project(path: ':ocr_ui')
    implementation 'com.android.support:recyclerview-v7:27.1.1'
    //json解析数据
    implementation 'com.alibaba:fastjson:1.2.47'
    //glide加载图片
    implementation 'com.github.bumptech.glide:glide:4.7.1'
    //悬浮按钮
    implementation 'com.getbase:floatingactionbutton:1.10.1'
    //上滑悬浮菜单
    implementation 'com.cocosw:bottomsheet:1.+@aar'
```
---
## LICENSE
```
   Copyright 2018 wanghao15536870732

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
