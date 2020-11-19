## WaveView
![控件效果](https://github.com/chengqian0109/images/blob/master/WaveView.gif)
## 1. 编辑项目根目录的 build.gradle 添加仓库支持：
```
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
## 2. 添加 Gradle 依赖：
![](https://jitpack.io/v/chengqian0109/WaveView.svg) WaveView 后面的「Tag」指的是左边这个 JitPack 徽章后面的「版本名称」，请自行替换。(https://jitpack.io/#chengqian0109/WaveView)
```
    dependencies {
        implementation 'com.github.chengqian0109:WaveView:Tag'
    }
```
## 3. xml布局文件中的使用：
```
     <com.jack.widget.WaveView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:waveGravity="bottom"
        app:waveWidth="1.5dp"
        app:waveMargin="2.5dp"
        app:waveCount="5"
        app:waveColor="#d00"
        app:waveAnimDuration="500"
        app:waveMinRatio="0.1"
        app:waveAnimDelay="180"/>
```
## 4. Java源代码中使用：
```
    WaveView waveView = new WaveView(this);
    waveView.setWaveColor(Color.CYAN);
    waveView.setAnimDelay(350);
    waveView.setWaveCount(6);
    waveView.setWaveWidth(30);
    waveView.setWaveGravity(WaveView.Gravity.BOTTOM);
    waveView.setWaveMargin(50);
    waveView.setAnimDuration(1000);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroupLayoutParams.WRAP_CONTENT, 200);
    ll.addView(waveView, layoutParams);
```
## 5. 属性与方法：

|name|format|description|method
|:---:|:---:|:---:|:---:|
|waveColor|color|线条颜色，默认白色|setWaveColor(int color)或setWaveColorRes(int colorId)
|waveCount|integer|线条数量，默认值3|setWaveCount(int waveCount)
|waveWidth|dimension|线条宽度，默认值1dp|setWaveWidth(int pixels)或setWaveWidthDp(float dpValue)或setWaveWidthRes(int dimensionId)
|waveMargin|dimension|相邻线条之间的间距，默认值1dp|setWaveMargin(int pixels)或setWaveMarginDp(float dpValue)或setWaveMarginRes(int dimensionId)
|waveAnimDuration|integer|单次动画执行时长，默认值240ms|setAnimDuration(int animDuration)
|waveAnimDelay|integer|相邻线条动画延时，默认值100ms|setAnimDelay(int animDelay)
|waveMinRatio|float|线条长度的最小比例，默认值0.3|setWaveMinRatio(float waveMinRatio)
|waveGravity|enum|线条相对画布的位置，默认值Gravity.CENTER|setWaveGravity(Gravity waveGravity)
