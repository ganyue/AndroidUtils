package com.gy.libgdx.test;

import android.util.Log;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by ganyu on 2016/7/29.
 *
 */
public class GameAdapter extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    ImageButton imageButton;
    Stage stage;

    @Override
    public void create () {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        TextureRegionDrawable drawable1 = new TextureRegionDrawable(new TextureRegion(img, 100, 100));
        TextureRegionDrawable drawable2 = new TextureRegionDrawable(new TextureRegion(img, 20, 20, 100, 100));
        imageButton = new ImageButton(drawable1, drawable2);
        imageButton.setPosition(200, 200);
        imageButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Log.d("yue.gan", "up pos : " + x + " - " + y);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Log.d("yue.gan", "down pos : " + x + " - " + y);
                return true;
            }
        });

        stage.addActor(imageButton);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        batch.begin();
//        batch.draw(img, 0, 0);
//        batch.end();
        stage.draw();
    }

    @Override
    public void dispose () {
        batch.dispose();
        img.dispose();
    }
}
