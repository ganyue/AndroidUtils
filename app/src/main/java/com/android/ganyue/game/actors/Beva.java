package com.android.ganyue.game.actors;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
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
public class Beva extends Actor {

    enum State {
        body, clap, lear, lfoot, rfoot, play, rear, tail, def,
    }
    private State state;

    private Animation bodyAnim;
    private Animation clapAnim;
    private Animation defaultAnim;
    private Animation learAnim;
    private Animation lfootAnim;
    private Animation playAnim;
    private Animation rearAnim;
    private Animation rfootAnim;
    private Animation tailAnim;

    private float stateTime;
    private TextureRegion currentFrame;
    private Animation currentAnim;

    public ImageButton bodyButton;
    public ImageButton clapButton;

    private float x;
    private float y;
    private float width;
    private float height;
    private Rectangle bodyRect;
    private Rectangle clapRect;

    public Beva(float x, float y, float width, float height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        state = State.def;
    }

    public void init (Stage stage) {
        TextureAtlas bodyAtlas = new TextureAtlas(Gdx.files.internal("anim/body/body.atlas"));
        TextureAtlas clapAtlas = new TextureAtlas(Gdx.files.internal("anim/clap/clap.atlas"));
        TextureAtlas defAtlas = new TextureAtlas(Gdx.files.internal("anim/default/default.atlas"));
        bodyAnim = new Animation(0.1f, bodyAtlas.findRegions("body"));
        clapAnim = new Animation(0.1f, clapAtlas.findRegions("clap"));
        defaultAnim = new Animation(0.1f, defAtlas.findRegions("default"));

        currentAnim = defaultAnim;
        bodyRect = new Rectangle(width * 0.5f, height * 0.2f, height * 0.3f, height * 0.3f);
        clapRect = new Rectangle(bodyRect.x + bodyRect.width, bodyRect.y, height * 0.3f, height * 0.3f);

        TextureRegionDrawable drawable1 = new TextureRegionDrawable(bodyAtlas.findRegion("body", 1));
        TextureRegionDrawable drawable2 = new TextureRegionDrawable(bodyAtlas.findRegion("body", 7));
        bodyButton = new ImageButton(drawable1, drawable2);
        bodyButton.setSize(bodyRect.width, bodyRect.height);
        bodyButton.setPosition(x + bodyRect.x, y + bodyRect.y);
        bodyButton.addListener(bodyListener);

        clapButton = new ImageButton(drawable1, drawable2);
        clapButton.setSize(clapRect.width, clapRect.height);
        clapButton.setPosition(x + clapRect.x, y + clapRect.y);
        clapButton.addListener(radioListener);

        stage.addActor(this);
        stage.addActor(bodyButton);
        stage.addActor(clapButton);
//        this.addListener(bodyListener);
    }

    public void changeState (State state) {
        if (this.state != State.def) {
            return;
        }
        this.state = state;
    }

    private void update () {
        if (currentAnim.isAnimationFinished(stateTime)) {
            stateTime = Gdx.graphics.getDeltaTime();
            state = State.def;
        }

        switch (state) {
            case body:
                currentFrame = bodyAnim.getKeyFrame(stateTime, true);
                currentAnim = bodyAnim;
                break;
            case clap:
                currentFrame = clapAnim.getKeyFrame(stateTime, true);
                currentAnim = clapAnim;
                break;
            default:
                currentFrame = defaultAnim.getKeyFrame(stateTime, true);
                currentAnim = defaultAnim;
                break;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime();
        update();
        batch.draw(currentFrame, x, y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private InputListener bodyListener = new InputListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Log.d("yue.gan", "touch down : " + x + "-" + y);
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            Log.d("yue.gan", "touch up : " + x + "-" + y);
            state = State.body;
        }
    };

    private InputListener radioListener = new InputListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Log.d("yue.gan", "touch down : " + x + "-" + y);
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            Log.d("yue.gan", "touch up : " + x + "-" + y);
            changeState(State.clap);
        }
    };


}
