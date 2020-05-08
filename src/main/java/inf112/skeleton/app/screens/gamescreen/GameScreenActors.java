package inf112.skeleton.app.screens.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Align;
import inf112.skeleton.app.RallyGame;
import inf112.skeleton.app.cards.ProgramCard;
import inf112.skeleton.app.cards.Register;

import java.util.ArrayList;

public class GameScreenActors {

    public final float mapHeight = 12;
    public final float mapWidth = 16;
    public final float programCardRatio = 0.72f;
    private final float labelFontScale;

    public float screenWidth;
    public float screenHeight;
    public float mapRightPx;
    public float programCardWidth;
    public float programCardHeight;
    public float confirmButtonSize;
    public float damageTokenSize;
    public float lifeTokenSize;


    private final ArrayList<ImageButton> programCardButtons;
    private ImageButton confirmButton;
    private ImageButton powerDownButton;

    private final ArrayList<Label> registerNumberLabels;
    private final ArrayList<Label> cardPriorityLabels;
    private final ArrayList<Label> lockedLabels;
    private Label damageTokenLabel;
    private Label lifeTokenLabel;

    private final RallyGame game;
    private final Stage stage;
    private final ProgramCardSkin cardSkin;
    public InfoLabel infoLabel;


    public GameScreenActors(RallyGame game, Stage stage) {
        this.game = game;
        this.stage = stage;

        programCardButtons = new ArrayList<>();
        registerNumberLabels = new ArrayList<>();
        cardPriorityLabels = new ArrayList<>();
        lockedLabels = new ArrayList<>();

        cardSkin = new ProgramCardSkin();

        screenWidth = game.getScreen().viewport.getScreenWidth();
        screenHeight = game.getScreen().viewport.getScreenHeight();
        mapRightPx = (screenHeight / mapHeight) * mapWidth;

        programCardWidth = (screenWidth - mapRightPx) / 3f;
        programCardHeight = programCardWidth / programCardRatio * .92f;
        lifeTokenSize = screenHeight * (68f / 540f);
        confirmButtonSize = lifeTokenSize;
        damageTokenSize = lifeTokenSize;

        labelFontScale = screenWidth / 960f;
    }

    public void updateButtons() {
        moveLockedCards();
        updateLockedLabels();
        updateCards();
        updatePriorityLabels();
        updateConfirm();
        updatePowerDownButton();
        updateLifeTokenLabel();
        updateDamageTokenLabel();
        updateRegisterNumberLabels();
    }

    // PROGRAM CARD BUTTONS

    public void initializeProgramCardButtons() {
        int idx = 0;
        for (int dy = 1; dy <= 3; dy++) {
            for (int dx = 0; dx <= 2; dx++) {
                ProgramCard card = game.mainPlayer.getCardsOnHand().get(idx);
                ImageButton.ImageButtonStyle cardStyle = new ImageButton.ImageButtonStyle();
               // System.out.println("CARD: "+card.getName() + " " + card);
                cardStyle.up = cardSkin.getSkins().getDrawable(card.getName());
                ImageButton cardButton = new ImageButton(cardStyle);
                cardButton.setSize(programCardWidth, programCardHeight);
                cardButton.setPosition(mapRightPx + programCardWidth * dx, screenHeight - programCardHeight * dy * 1.18f);
                setCardButtonInputListener(card, cardButton);
                programCardButtons.add(cardButton);
                stage.addActor(cardButton);
                idx++;
            }
        }
    }

    private void setCardButtonInputListener(ProgramCard card, ImageButton cardButton) {
        cardButton.clearListeners();
        cardButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (game.isWaitingForCards()) {
                    game.mainPlayer.selectCard(card);
                    System.out.println(game.mainPlayer.getRegisters());
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    public void updateCards() {
        for (int cardButtonIndex = 0; cardButtonIndex < 9; cardButtonIndex++) {
            ImageButton cardButton = programCardButtons.get(cardButtonIndex);
            if (cardButtonIndex < game.mainPlayer.getCardsOnHand().size()) {
                ProgramCard card = game.mainPlayer.getCardsOnHand().get(cardButtonIndex);
                cardButton.getStyle().up = cardSkin.getSkins().getDrawable(card.getName());
                setCardButtonInputListener(card, cardButton);
                cardButton.setVisible(true);
            } else {
                cardButton.setVisible(false);
            }
        }
    }

    public void moveLockedCards() {
        for (int i = 4; i >= 0; i--) {
            Register register = game.mainPlayer.getRegisters().getRegister(i);
            if (!register.isOpen()) {
                ProgramCard card = register.getProgramCard();
                ImageButton cardButton = programCardButtons.get(i + 4);
                cardButton.getStyle().up = cardSkin.getSkins().getDrawable(card.getName());
                setCardButtonInputListener(card, cardButton);
                cardButton.setVisible(true);
            }
        }
    }

    // CONFIRM BUTTON

    public void initializeConfirmButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = game.getActorImages().getDrawable("Confirm ready");
        confirmButton = new ImageButton(style);
        confirmButton.setSize(confirmButtonSize, confirmButtonSize);
        confirmButton.setPosition(screenWidth - confirmButtonSize, 0);
        confirmButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               if (game.readyToConfirm()) {
                   game.confirm();
               }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(confirmButton);
    }

    public void updateConfirm() {
        if (game.readyToConfirm()) {
            confirmButton.getStyle().up = game.actorImages.getSkin().getDrawable("Confirm ready");
        } else {
            confirmButton.getStyle().up = game.actorImages.getSkin().getDrawable("Confirm not ready");
        }
    }

    // POWER DOWN BUTTON

    public void initializePowerDownButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = game.getActorImages().getDrawable("Power down active");
        powerDownButton = new ImageButton(style);
        powerDownButton.setSize(confirmButtonSize, confirmButtonSize);
        powerDownButton.setPosition(screenWidth - confirmButtonSize, confirmButtonSize);
        powerDownButton.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (game.isWaitingForCards() || game.isWaitingForPowerUp()) {
                    game.mainPlayer.togglePowerDownOrUpNextRound();
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(powerDownButton);
    }

    public void updatePowerDownButton() {
        if (game.mainPlayer.isPoweringDown()) {
            powerDownButton.getStyle().up = game.getActorImages().getDrawable("Powering down");
        } else if (game.mainPlayer.isPoweredDown()) {
            if (game.mainPlayer.getPowerUpNextRound()) {
                powerDownButton.getStyle().up = game.getActorImages().getDrawable("Power up inactive");
            } else {
                powerDownButton.getStyle().up = game.getActorImages().getDrawable("Power up active");
            }
        } else {
            if (game.mainPlayer.getPowerDownNextRound()) {
                powerDownButton.getStyle().up = game.getActorImages().getDrawable("Power down inactive");
            } else {
                powerDownButton.getStyle().up = game.getActorImages().getDrawable("Power down active");
            }
        }
    }

    // DAMAGE TOKEN IMAGES

    public void initializeDamageTokens() {
        Image token = new Image(game.getActorImages().getDrawable("Damage token"));
        token.setSize(damageTokenSize, damageTokenSize);
        token.setPosition(mapRightPx, lifeTokenSize);
        stage.addActor(token);
    }

    public void updateDamageTokenLabel() {
        damageTokenLabel.setText("x" + game.mainPlayer.getDamageTokens());
    }

    public void initializeDamageTokenLabel() {
        damageTokenLabel = new Label("", game.getTextSkin());
        damageTokenLabel.setHeight(damageTokenSize);
        damageTokenLabel.setPosition(mapRightPx + damageTokenSize, lifeTokenSize);
        damageTokenLabel.setFontScale(labelFontScale * 2f);
        stage.addActor(damageTokenLabel);
    }

    // LIFE TOKEN IMAGES

    public void initializeLifeTokens() {
        Image token = new Image(game.getActorImages().getDrawable("Life token"));
        token.setSize(lifeTokenSize, lifeTokenSize);
        token.setPosition(mapRightPx, 0);
        stage.addActor(token);
    }
    public void updateLifeTokenLabel() {
        lifeTokenLabel.setText("x" + game.mainPlayer.getLifeTokens());
    }

    public void initializeLifeTokenLabel() {
        lifeTokenLabel = new Label("", game.getTextSkin());
        lifeTokenLabel.setHeight(lifeTokenSize);
        lifeTokenLabel.setPosition(mapRightPx + lifeTokenSize, 0);
        lifeTokenLabel.setFontScale(labelFontScale * 2f);
        stage.addActor(lifeTokenLabel);
    }

    // PRIORITY LABELS

    public void initializePriorityLabels() {
        for (ImageButton button : programCardButtons) {
            Label cardPriority = new Label("", game.getTextSkin(), "lcd", Color.WHITE);
            float height = programCardHeight * .18f;
            float x = button.getX() + programCardWidth * 0.1f;
            float y = button.getY() + programCardHeight + height * 0.5f;
            cardPriority.setWidth(programCardWidth * 0.8f);
            cardPriority.setPosition(x, y);
            cardPriority.setFontScale(labelFontScale * 0.4f);
            cardPriority.setAlignment(Align.right);
            stage.addActor(cardPriority);
            cardPriorityLabels.add(cardPriority);
        }
    }

    public void updatePriorityLabels() {
        updateCardOnHandPriorityLabels();
        updateLockedRegistersPriorityLabels();
    }

    public void updateCardOnHandPriorityLabels() {
        for (int cardIndex = 0; cardIndex < 9; cardIndex++) {
            ArrayList<ProgramCard> cards = game.mainPlayer.getCardsOnHand();
            if (cardIndex < cards.size()) {
                cardPriorityLabels.get(cardIndex).setText(cards.get(cardIndex).getPriority());
                cardPriorityLabels.get(cardIndex).setVisible(true);
            } else {
                cardPriorityLabels.get(cardIndex).setVisible(false);
            }
        }
    }

    public void updateLockedRegistersPriorityLabels() {
        for (int registerIndex = 0; registerIndex < 5; registerIndex++) {
            Register register = game.mainPlayer.getRegisters().getRegister(registerIndex);
            if (!register.isOpen()) {
                cardPriorityLabels.get(registerIndex + 4).setText(register.getProgramCard().getPriority());
                cardPriorityLabels.get(registerIndex + 4).setVisible(true);
            }
        }
    }

    // REGISTER NUMBER LABELS

    public void initializeNumberLabels() {
        for (ImageButton button : programCardButtons) {
            Label numberLabel = new Label("", game.getTextSkin(), "button", Color.RED);
            float height = programCardHeight * .18f;
            float x = button.getX() + programCardWidth * 0.1f;
            float y = button.getY() + programCardHeight + height / 1.65f;
            /*
            float x = button.getX() + programCardWidth * 0.7f;
            float y = button.getY() + programCardHeight*0.2f;
            */
            numberLabel.setPosition(x, y);
            numberLabel.setFontScale(labelFontScale * 0.6f);
            numberLabel.setVisible(false);
            stage.addActor(numberLabel);
            registerNumberLabels.add(numberLabel);
        }
    }

    public void updateRegisterNumberLabels() {
        hideAllRegisterNumberLabels();
        updateOpenRegisterNumberLabels();
        updateLockedRegisterNumberLabels();
    }

    public void hideAllRegisterNumberLabels() {
        for (Label label : registerNumberLabels) {
            label.setText("");
            label.setVisible(false);
        }
    }

    public void updateOpenRegisterNumberLabels() {
        for (Register register : game.mainPlayer.getRegisters().getRegisters()) {
            if (register.isOpen() && register.hasCard()) {
                ProgramCard card = register.getProgramCard();
                int index = game.mainPlayer.getCardsOnHand().indexOf(card);
                registerNumberLabels.get(index).setText(register.getRegisterNumber());
                registerNumberLabels.get(index).setVisible(true);
            }
        }
    }

    public void updateLockedRegisterNumberLabels() {
        for (int cardButtonIndex = 4; cardButtonIndex < 9; cardButtonIndex++) {
            Register register = game.mainPlayer.getRegisters().getRegister(cardButtonIndex - 4);
            if (!register.isOpen()) {
                registerNumberLabels.get(cardButtonIndex).setText(register.getRegisterNumber());
                registerNumberLabels.get(cardButtonIndex).setVisible(true);
            }
        }
    }

    // LOCKED LABELS

    public void initializeLockedLabels() {
        for (int cardButtonIndex = 4; cardButtonIndex < 9; cardButtonIndex++) {
            ImageButton cardButton = programCardButtons.get(cardButtonIndex);
            Label lockedLabel = new Label("LOCKED", game.getTextSkin(), "title", Color.RED);
            Container<Label> wrapper = new Container<>(lockedLabel);
            wrapper.setTransform(true);
            wrapper.setRotation(45);
            float x = cardButton.getX() + programCardWidth * 0.5f;
            float y = cardButton.getY() + programCardHeight * 0.5f;
            wrapper.setPosition(x, y);
            lockedLabel.setFontScale(labelFontScale * 0.8f);
            lockedLabel.setVisible(false);
            stage.addActor(wrapper);
            lockedLabels.add(lockedLabel);
        }
    }

    public void updateLockedLabels() {
        for (int registerIndex = 0; registerIndex < 5; registerIndex++) {
            lockedLabels.get(registerIndex).setVisible(!game.mainPlayer.getRegisters().getRegister(registerIndex).isOpen());
        }
    }

    // INFO LABEL

    public void displayMessage(String text) {
        infoLabel.displayText(text);
    }

    public void stopDisplayingMessage() {
        infoLabel.stopAnimation();
    }

    public void initializeInfoLabel() {
        this.infoLabel = new InfoLabel("Your color is");
        stage.addActor(infoLabel);
    }

    class InfoLabel extends Actor {

        private boolean animated = false;
        private long animationStart;

        private float deltaX;

        private String text;

        private final BitmapFont font = game.getTextSkin().getFont("button");
        private final GlyphLayout layout = new GlyphLayout();
        private float textWidth;
        private float textHeight;
        public InfoLabel(String text) {
            this.text = text;
            font.setColor(Color.RED);
            font.getData().setScale(labelFontScale * .7f);
            layout.setText(font, text);
            textWidth = layout.width;
            textHeight = layout.height;
            setPosition(Gdx.graphics.getWidth(), lifeTokenSize * 2.1f + textHeight);
            setDeltaX(-100);
        }

        public void setDeltaX(float deltaX) {
            this.deltaX = deltaX;
        }

        public void displayText(String text) {
            this.text = text;
            layout.setText(font, text);
            textWidth = layout.width;
            textHeight = layout.height;
            animationStart = System.currentTimeMillis();
            animated = true;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (animated) {

                float elapsed = System.currentTimeMillis() - animationStart;

                if (textWidth + getX() + deltaX * elapsed / 1000f < mapRightPx) {
                    animationStart = System.currentTimeMillis();
                }
                font.draw(batch, text, getX() + deltaX * elapsed / 1000f, getY());
            }
        }

        public void stopAnimation() {
            animated = false;
        }
    }
}
