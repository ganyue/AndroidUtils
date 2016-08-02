package com.android.ganyue.game.actors;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by ganyu on 2016/8/1.
 *
 */
public class Radio extends Actor {

    private Animation radioAnim;

    private float stateTime;
    private TextureRegion currentFrame;
    public ImageButton radioButton;

    private float x;
    private float y;
    private float width;
    private float height;

    private OnRadioClickListener onRadioClickListener;

    public Radio(float x, float y, float width, float height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void init (Stage stage) {
        TextureAtlas radioAtlas = new TextureAtlas(Gdx.files.internal("anim/radio/radio.atlas"));
        TextureAtlas bodyAtlas = new TextureAtlas(Gdx.files.internal("anim/body/body.atlas"));
        radioAnim = new Animation(0.1f, radioAtlas.findRegions("radio"));

        TextureRegion clearReagion = new TextureRegion(bodyAtlas.findRegion("body", 1), 0, 0, 10, 10);
        TextureRegionDrawable clearDrawable = new TextureRegionDrawable(clearReagion);
        radioButton = new ImageButton(clearDrawable);
        radioButton.setSize(width, height);
        radioButton.setPosition(x, y);
        radioButton.addListener(radioListener);

        stage.addActor(this);
        stage.addActor(radioButton);
    }

    public void setOnRadioClickListener (OnRadioClickListener listener) {
        this.onRadioClickListener = listener;
    }

    private void update () {
        currentFrame = radioAnim.getKeyFrame(stateTime, true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime();
        update();
        batch.draw(currentFrame, x, y, width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private InputListener radioListener = new InputListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Log.d("yue.gan", "touch down : " + x + "-" + y);
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            Log.d("yue.gan", "touch up : " + x + "-" + y);
            onRadioClickListener.onRadioClick();
        }
    };

    public interface OnRadioClickListener {
        void onRadioClick ();
    }
}
