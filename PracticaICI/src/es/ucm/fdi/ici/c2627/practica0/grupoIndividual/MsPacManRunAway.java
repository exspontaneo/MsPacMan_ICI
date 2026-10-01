package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManRunAway extends PacmanController {

    @Override
    public MOVE getMove(Game game, long timeDue) {
        int pacmanNode = game.getPacmanCurrentNodeIndex();
        
        int closestGhostNode = -1;
        int minDistance = Integer.MAX_VALUE;
        
        for (GHOST ghost : GHOST.values()) {

            if (game.getGhostLairTime(ghost) == 0) {
                int ghostNode = game.getGhostCurrentNodeIndex(ghost);
                int distance = game.getShortestPathDistance(pacmanNode, ghostNode);
                
                if (distance < minDistance) {
                    minDistance = distance;
                    closestGhostNode = ghostNode;
                }
            }
        }
        
        if (closestGhostNode != -1) {
            return game.getApproximateNextMoveAwayFromTarget(
                    pacmanNode, 
                    closestGhostNode, 
                    game.getPacmanLastMoveMade(),
                    DM.PATH);
        }
        
        return game.getPacmanLastMoveMade();
    }

    public String getName() {
        return "MsPacManRunAway";
    }
}