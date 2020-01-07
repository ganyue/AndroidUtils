package com.android.ganyue.frg;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ganyue.R;
import com.android.ganyue.application.MApplication;
import com.gy.appbase.fragment.BaseFragment;
import com.gy.appbase.inject.ViewInject;
import com.gy.utils.log.LogUtils;

/**
 * created by yue.gan 18-9-22
 */
public class TestFrg extends BaseFragment{

    @ViewInject(R.id.llyt_content) private LinearLayout mLLytContent;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    protected void initViews(View view) {
        String[] ttfNameStrs = {
                "AndroidClock.ttf",
                "CarroisGothicSC-Regular.ttf",
                "ComingSoon.ttf",
                "CutiveMono.ttf",
                "DancingScript-Bold.ttf",
                "DancingScript-Regular.ttf",
                "DroidSans-Bold.ttf",
                "DroidSansMono.ttf",
                "DroidSans.ttf",
                "NotoColorEmoji.ttf",
                "NotoNaskhArabic-Bold.ttf",
                "NotoNaskhArabic-Regular.ttf",
                "NotoNaskhArabicUI-Bold.ttf",
                "NotoNaskhArabicUI-Regular.ttf",
                "NotoSansArmenian-Bold.ttf",
                "NotoSansArmenian-Regular.ttf",
                "NotoSansBalinese-Regular.ttf",
                "NotoSansBamum-Regular.ttf",
                "NotoSansBatak-Regular.ttf",
                "NotoSansBengali-Bold.ttf",
                "NotoSansBengali-Regular.ttf",
                "NotoSansBengaliUI-Bold.ttf",
                "NotoSansBengaliUI-Regular.ttf",
                "NotoSansBuginese-Regular.ttf",
                "NotoSansBuhid-Regular.ttf",
                "NotoSansCanadianAboriginal-Regular.ttf",
                "NotoSansCham-Bold.ttf",
                "NotoSansCham-Regular.ttf",
                "NotoSansCherokee-Regular.ttf",
                "NotoSansCoptic-Regular.ttf",
                "NotoSansDevanagari-Bold.ttf",
                "NotoSansDevanagari-Regular.ttf",
                "NotoSansDevanagariUI-Bold.ttf",
                "NotoSansDevanagariUI-Regular.ttf",
                "NotoSansEthiopic-Bold.ttf",
                "NotoSansEthiopic-Regular.ttf",
                "NotoSansGeorgian-Bold.ttf",
                "NotoSansGeorgian-Regular.ttf",
                "NotoSansGlagolitic-Regular.ttf",
                "NotoSansGujarati-Bold.ttf",
                "NotoSansGujarati-Regular.ttf",
                "NotoSansGujaratiUI-Bold.ttf",
                "NotoSansGujaratiUI-Regular.ttf",
                "NotoSansGurmukhi-Bold.ttf",
                "NotoSansGurmukhi-Regular.ttf",
                "NotoSansGurmukhiUI-Bold.ttf",
                "NotoSansGurmukhiUI-Regular.ttf",
                "NotoSansHanunoo-Regular.ttf",
                "NotoSansHebrew-Bold.ttf",
                "NotoSansHebrew-Regular.ttf",
                "NotoSansJavanese-Regular.ttf",
                "NotoSansKannada-Bold.ttf",
                "NotoSansKannada-Regular.ttf",
                "NotoSansKannadaUI-Bold.ttf",
                "NotoSansKannadaUI-Regular.ttf",
                "NotoSansKayahLi-Regular.ttf",
                "NotoSansKhmer-Bold.ttf",
                "NotoSansKhmer-Regular.ttf",
                "NotoSansKhmerUI-Bold.ttf",
                "NotoSansKhmerUI-Regular.ttf",
                "NotoSansLao-Bold.ttf",
                "NotoSansLao-Regular.ttf",
                "NotoSansLaoUI-Bold.ttf",
                "NotoSansLaoUI-Regular.ttf",
                "NotoSansLepcha-Regular.ttf",
                "NotoSansLimbu-Regular.ttf",
                "NotoSansLisu-Regular.ttf",
                "NotoSansMalayalam-Bold.ttf",
                "NotoSansMalayalam-Regular.ttf",
                "NotoSansMalayalamUI-Bold.ttf",
                "NotoSansMalayalamUI-Regular.ttf",
                "NotoSansMandaic-Regular.ttf",
                "NotoSansMeeteiMayek-Regular.ttf",
                "NotoSansMongolian-Regular.ttf",
                "NotoSansMyanmar-Bold.ttf",
                "NotoSansMyanmar-Regular.ttf",
                "NotoSansMyanmarUI-Bold.ttf",
                "NotoSansMyanmarUI-Regular.ttf",
                "NotoSansNewTaiLue-Regular.ttf",
                "NotoSansNKo-Regular.ttf",
                "NotoSansOlChiki-Regular.ttf",
                "NotoSansOriya-Bold.ttf",
                "NotoSansOriya-Regular.ttf",
                "NotoSansOriyaUI-Bold.ttf",
                "NotoSansOriyaUI-Regular.ttf",
                "NotoSansRejang-Regular.ttf",
                "NotoSansSaurashtra-Regular.ttf",
                "NotoSansSinhala-Bold.ttf",
                "NotoSansSinhala-Regular.ttf",
                "NotoSansSundanese-Regular.ttf",
                "NotoSansSylotiNagri-Regular.ttf",
                "NotoSansSymbols-Regular-Subsetted2.ttf",
                "NotoSansSymbols-Regular-Subsetted.ttf",
                "NotoSansSyriacEstrangela-Regular.ttf",
                "NotoSansTagbanwa-Regular.ttf",
                "NotoSansTaiLe-Regular.ttf",
                "NotoSansTaiTham-Regular.ttf",
                "NotoSansTaiViet-Regular.ttf",
                "NotoSansTamil-Bold.ttf",
                "NotoSansTamil-Regular.ttf",
                "NotoSansTamilUI-Bold.ttf",
                "NotoSansTamilUI-Regular.ttf",
                "NotoSansTelugu-Bold.ttf",
                "NotoSansTelugu-Regular.ttf",
                "NotoSansTeluguUI-Bold.ttf",
                "NotoSansTeluguUI-Regular.ttf",
                "NotoSansThaana-Bold.ttf",
                "NotoSansThaana-Regular.ttf",
                "NotoSansThai-Bold.ttf",
                "NotoSansThai-Regular.ttf",
                "NotoSansThaiUI-Bold.ttf",
                "NotoSansThaiUI-Regular.ttf",
                "NotoSansTibetan-Bold.ttf",
                "NotoSansTibetan-Regular.ttf",
                "NotoSansTifinagh-Regular.ttf",
                "NotoSansVai-Regular.ttf",
                "NotoSansYi-Regular.ttf",
                "NotoSerif-BoldItalic.ttf",
                "NotoSerif-Bold.ttf",
                "NotoSerif-Italic.ttf",
                "NotoSerif-Regular.ttf",
                "Roboto-BlackItalic.ttf",
                "Roboto-Black.ttf",
                "Roboto-BoldItalic.ttf",
                "Roboto-Bold.ttf",
                "RobotoCondensed-BoldItalic.ttf",
                "RobotoCondensed-Bold.ttf",
                "RobotoCondensed-Italic.ttf",
                "RobotoCondensed-LightItalic.ttf",
                "RobotoCondensed-Light.ttf",
                "RobotoCondensed-Regular.ttf",
                "Roboto-Italic.ttf",
                "Roboto-LightItalic.ttf",
                "Roboto-Light.ttf",
                "Roboto-MediumItalic.ttf",
                "Roboto-Medium.ttf",
                "Roboto-Regular.ttf",
                "Roboto-ThinItalic.ttf",
                "Roboto-Thin.ttf",
        };

        AssetManager asm = MApplication.getApplication().getAssets();
        for (String name: ttfNameStrs) {
            Typeface typeface=Typeface.createFromAsset(asm,name);
            TextView textViewN = new TextView(getActivity());
            textViewN.setTypeface(typeface);
            textViewN.setTextColor(Color.RED);
            textViewN.setSingleLine();
            textViewN.setText(name);
            mLLytContent.addView(textViewN);
            TextView textView = new TextView(getActivity());
            textView.setTypeface(typeface);
            textView.setTextColor(Color.BLUE);
            textView.setText("ગુજરાતી (ભારત)");
            mLLytContent.addView(textView);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void activityCall(int type, Object extra) {

    }
}
