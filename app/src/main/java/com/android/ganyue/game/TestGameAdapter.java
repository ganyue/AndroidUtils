package com.android.ganyue.game;

import com.android.ganyue.game.actors.Beva;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by ganyu on 2016/7/29.
 *
 */
public class TestGameAdapter extends ApplicationAdapter {

    private Stage stage;
    private Beva beva;

    @Override
    public void create () {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        beva = new Beva(0, 0, 300, 270);
        beva.init(stage);
    }

    @Override
    public void render () {
        //clear color buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose () {
    }
}
