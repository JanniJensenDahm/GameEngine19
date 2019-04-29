package dk.kea.class19.JanniJD.gameengine19.Breakout;

import dk.kea.class19.JanniJD.gameengine19.GameEngine;
import dk.kea.class19.JanniJD.gameengine19.Screen;

public class Breakout extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        music = this.loadMusic("Breakout/music.ogg");
        return new MainMenuScreen(this);
    }

    public void onResume()
    {
        super.onResume();
        music.play();
    }

    public void onPause()
    {
        super.onPause();
        music.pause();
    }
}
