package dk.kea.class19.JanniJD.gameengine19.Carscroller;

import android.graphics.Bitmap;


import dk.kea.class19.JanniJD.gameengine19.GameEngine;

public class WorldRenderer
{
    GameEngine gameEngine;
    World world;
    Bitmap carImage;
    Bitmap monsterImage;

    public WorldRenderer(GameEngine gameEngine, World world)
    {
        gameEngine = gameEngine;
        world = world;
        carImage = gameEngine.loadBitmanp("carscroller/xbluecar2.png");
        monsterImage = gameEngine.loadBitmanp("carscroller/xyellowmonster2.png");
    }

    public void render()
    {
        gameEngine.drawBitmap(carImage, world.car.x, world.car.y);

        for (int i = 0; i < world.monsterList.size(); i++)
        {
            gameEngine.drawBitmap(monsterImage, world.monsterList.get(i).x, world.monsterList.get(i).y);
        }
    }
}
