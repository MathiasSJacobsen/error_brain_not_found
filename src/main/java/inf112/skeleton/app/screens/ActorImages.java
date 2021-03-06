package inf112.skeleton.app.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ActorImages {

    private final Skin skin;

    public ActorImages() {
        TextureAtlas atlas = new TextureAtlas();

        TextureRegion createGame = new TextureRegion(new Texture("assets/images/Create game active.png"));
        TextureRegion createGameOver = new TextureRegion(new Texture("assets/images/Create game Inactive.png"));
        TextureRegion joinGame = new TextureRegion(new Texture("assets/images/Join game active.png"));
        TextureRegion joinGameOver = new TextureRegion(new Texture("assets/images/Join game inactive.png"));

        TextureRegion confirmReady = new TextureRegion(new Texture("assets/images/ConfirmButton.png"));
        TextureRegion confirmNotReady = new TextureRegion(new Texture("assets/images/ConfirmButtonNotReady.png"));
        TextureRegion Exit = new TextureRegion(new Texture("assets/images/Exit button active.png"));
        TextureRegion ExitOver = new TextureRegion(new Texture("assets/images/Exit button inactive.png"));
        TextureRegion Start = new TextureRegion(new Texture("assets/images/Start button active.png"));
        TextureRegion StartOver = new TextureRegion(new Texture("assets/images/Start button inactive.png"));
        TextureRegion Settings = new TextureRegion(new Texture("assets/images/Settings Active.png"));
        TextureRegion SettingsOver = new TextureRegion(new Texture("assets/images/Settings Inactive.png"));
        TextureRegion backButton = new TextureRegion(new Texture("assets/images/Back Button Inactive.png"));
        TextureRegion backButtonOver = new TextureRegion(new Texture("assets/images/Back Button Active.png"));
        TextureRegion screenToggler = new TextureRegion(new Texture("assets/images/Screen Toggle Inactive.png"));
        TextureRegion screenTogglerOver = new TextureRegion(new Texture("assets/images/Screen Toggle Active.png"));
        TextureRegion powerDownActive = new TextureRegion(new Texture("assets/images/Power Down Active.png"));
        TextureRegion powerDownInactive = new TextureRegion(new Texture("assets/images/Power Down Inactive.png"));
        TextureRegion powerUpActive = new TextureRegion(new Texture("assets/images/Power Up Active.png"));
        TextureRegion powerUpInactive = new TextureRegion(new Texture("assets/images/Power Up Inactive.png"));
        TextureRegion poweringDown = new TextureRegion(new Texture("assets/images/Powering Down.png"));
        TextureRegion damageToken = new TextureRegion(new Texture("assets/images/damageToken.png"));
        TextureRegion lifeToken = new TextureRegion(new Texture("assets/images/lifeToken.png"));

        TextureRegion loadingScreenBackground = new TextureRegion(new Texture("assets/images/RoboRallyMenuScreen.png"));
        TextureRegion menuScreenBackground = new TextureRegion(new Texture("assets/images/GUI_Edited.jpg"));
        TextureRegion youWinBackground = new TextureRegion(new Texture("assets/images/You Won.png"));
        TextureRegion youLostBackground = new TextureRegion(new Texture("assets/images/You Lost.png"));

        atlas.addRegion("Confirm ready", confirmReady);
        atlas.addRegion("Confirm not ready", confirmNotReady);
        atlas.addRegion("Exit", Exit);
        atlas.addRegion("Exit over", ExitOver);
        atlas.addRegion("Start", Start);
        atlas.addRegion("Start over", StartOver);
        atlas.addRegion("Settings", Settings);
        atlas.addRegion("Settings over", SettingsOver);
        atlas.addRegion("Back", backButton);
        atlas.addRegion("Back over", backButtonOver);
        atlas.addRegion("Back over", backButtonOver);
        atlas.addRegion("Screen Toggle", screenToggler);
        atlas.addRegion("Screen Toggle over", screenTogglerOver);
        atlas.addRegion("Power down active", powerDownActive);
        atlas.addRegion("Power down inactive", powerDownInactive);
        atlas.addRegion("Power up active", powerUpActive);
        atlas.addRegion("Power up inactive", powerUpInactive);
        atlas.addRegion("Powering down", poweringDown);
        atlas.addRegion("Loading screen background", loadingScreenBackground);
        atlas.addRegion("Menu screen background", menuScreenBackground);
        atlas.addRegion("You won background", youWinBackground);
        atlas.addRegion("You lost background", youLostBackground);
        atlas.addRegion("Damage token", damageToken);
        atlas.addRegion("Life token", lifeToken);
        atlas.addRegion("Create game", createGame);
        atlas.addRegion("Create game over", createGameOver);
        atlas.addRegion("Join game", joinGame);
        atlas.addRegion("Join game over", joinGameOver);

        this.skin = new Skin(atlas);
    }

    public Skin getSkin() {
        return skin;
    }
}
