package com.android.ganyue.game;

import com.android.ganyue.game.actors.Beva;
import com.android.ganyue.game.stages.BevaStage;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gy.utils.constants.WindowConstants;

/**
 * Created by ganyu on 2016/7/29.
 *
 */
public class TestGameAdapter extends ApplicationAdapter {

    private Stage stage;

    @Override
    public void create () {
        stage = new BevaStage();
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
