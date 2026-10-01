package es.ucm.fdi.ici.c2627.practica1.grupoYY;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private Random rnd = new Random();

	private final int LIMIT = 15; 

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		moves.clear();

		int pacmanNode = game.getPacmanCurrentNodeIndex();

		boolean pacmanCloseToPPill = isPacmanCloseToPowerPill(game, LIMIT);

		for (GHOST ghost : GHOST.values()) {
			if (!game.doesGhostRequireAction(ghost)) {
				continue;
			}

			int ghostNode = game.getGhostCurrentNodeIndex(ghost);
			MOVE lastMove = game.getGhostLastMoveMade(ghost);

			if (game.isGhostEdible(ghost) || pacmanCloseToPPill) {
				// huye de MsPacMan
				moves.put(ghost, game.getApproximateNextMoveAwayFromTarget(
						ghostNode, 
						pacmanNode, 
						lastMove, 
						DM.PATH));
			} 

			else {
				// persigue
				if (rnd.nextFloat() < 0.9) {
					moves.put(ghost, game.getApproximateNextMoveTowardsTarget(
							ghostNode, 
							pacmanNode, 
							lastMove, 
							DM.PATH));
				} 
				// movimiento aleatorio
				else {
					MOVE[] possibleMoves = game.getPossibleMoves(ghostNode, lastMove);
					MOVE randomMove = possibleMoves[rnd.nextInt(possibleMoves.length)];
					moves.put(ghost, randomMove);
				}
			}
		}

		return moves;
	}

	private boolean isPacmanCloseToPowerPill(Game game, int limit) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		int[] activePowerPills = game.getActivePowerPillsIndices();

		for (int ppNode : activePowerPills) {
			int distance = game.getShortestPathDistance(pacmanNode, ppNode);
			if (distance <= limit) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "Ghosts";
	}
}