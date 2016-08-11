1、科大讯飞语音库，这里只集成了三个，语音合成（文字转语音），语音听写（语音转文字），语音识别（识别语法）

2、如果要混淆打包，需要先看看本module的progrard-rules.pro

3、如果要使用这个module记住，要先到XunfeiUtils里头去修改APPID

4、android studio 1.0以后开启混合打包 ：
buildTypes {
    debug {
        ... ...
    }

    release {
        minifyEnabled true  //开启混淆打包
        ... ...
    }
}