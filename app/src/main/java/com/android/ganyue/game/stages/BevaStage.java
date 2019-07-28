package com.android.ganyue.game.stages;

import com.android.ganyue.game.actors.Background;
import com.android.ganyue.game.actors.Beva;
import com.android.ganyue.game.actors.Radio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gy.utils.constants.WindowConstants;

/**
 * Created by yue.gan on 2016/8/2.
 *
 */
public class BevaStage extends Stage {

    private Background background;
    private Beva beva;
    private Radio radio;

    public BevaStage() {
        super();
        Gdx.input.setInputProcessor(this);


        WindowConstants windowConstants = WindowConstants.getInstance(null);
        float windowW = windowConstants.getWindowWidth();
        float windowH = windowConstants.getWindowHeight();
        float titleH = windowConstants.convertDpToPix(48);

        background = new Background(0, 0, windowW, windowH - titleH);
        background.init(this);

        //init beva
        float bevaW = windowW / 2;
        float bevaH = bevaW * 0.9f;
        float bevaX = windowW / 8;
        float bevaY = (windowH - titleH) / 5;
        beva = new Beva(bevaX, bevaY, bevaW, bevaH);
        beva.init(this);

        //init radio
        float radioW = bevaW / 3 * 2;
        float radioH = radioW / 4 * 5;
        float radioX = bevaX + bevaW / 8 * 7;
        float radioY = bevaY;
        radio = new Radio(radioX, radioY, radioW, radioH);
        radio.setOnRadioClickListener(onRadioClickListener);
        radio.init(this);
    }


    private Radio.OnRadioClickListener onRadioClickListener = new Radio.OnRadioClickListener() {
        @Override
        public void onRadioClick() {
            beva.changeState(Beva.State.clap);
        }
    };

}
