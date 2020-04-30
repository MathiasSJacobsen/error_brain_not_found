package inf112.skeleton.app.objects.player;

import java.util.Comparator;

public class PlayerSorter implements Comparator<Player> {

    @Override
    public int compare(Player player1, Player player2) {
        return player2.getRegisters().getCard(0).getPriority() -
                player1.getRegisters().getCard(0).getPriority();
    }
}
