package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import java.awt.Color;
import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class MsPacMan extends PacmanController {

	@Override
	public MOVE getMove(Game game, long timeDue) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		int limit = 20;

		// comprobar fantasmas peligrosos
		GHOST nearestChasingGhost = getNearestChasingGhost(game, limit);
		if (nearestChasingGhost != null) {
			int ghostNode = game.getGhostCurrentNodeIndex(nearestChasingGhost);

			GameView.addLines(game, Color.RED, pacmanNode, ghostNode);

			return game.getApproximateNextMoveAwayFromTarget(
					pacmanNode, 
					ghostNode, 
					game.getPacmanLastMoveMade(), 
					DM.PATH);
		}

		// comprobar fantasmas comestibles
		GHOST nearestEdibleGhost = getNearestEdibleGhost(game, limit);
		if (nearestEdibleGhost != null) {
			int ghostNode = game.getGhostCurrentNodeIndex(nearestEdibleGhost);

			GameView.addLines(game, Color.GREEN, pacmanNode, ghostNode);

			return game.getApproximateNextMoveTowardsTarget(
					pacmanNode, 
					ghostNode, 
					game.getPacmanLastMoveMade(), 
					DM.PATH);
		}

		// ir a la pildora mas cercana
		int nearestPillNode = getNearestPill(game);
		if (nearestPillNode != -1) {
			return game.getApproximateNextMoveTowardsTarget(
					pacmanNode, 
					nearestPillNode, 
					game.getPacmanLastMoveMade(), 
					DM.PATH);
		}

		// movimiento por defecto
		return game.getPacmanLastMoveMade();
	}

	private GHOST getNearestChasingGhost(Game game, int limit) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		GHOST closestGhost = null;
		int minDistance = Integer.MAX_VALUE;

		for (GHOST ghost : GHOST.values()) {
			// Solo fantasmas fuera de la cárcel y no comestibles
			if (game.getGhostLairTime(ghost) == 0 && !game.isGhostEdible(ghost)) {
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				int distance = game.getShortestPathDistance(pacmanNode, ghostNode);

				if (distance <= limit && distance < minDistance) {
					minDistance = distance;
					closestGhost = ghost;
				}
			}
		}

		return closestGhost;
	}

	private GHOST getNearestEdibleGhost(Game game, int limit) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		GHOST closestGhost = null;
		int minDistance = Integer.MAX_VALUE;

		for (GHOST ghost : GHOST.values()) {
			// Solo fantasmas comestibles
			if (game.isGhostEdible(ghost)) {
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				int distance = game.getShortestPathDistance(pacmanNode, ghostNode);

				if (distance <= limit && distance < minDistance) {
					minDistance = distance;
					closestGhost = ghost;
				}
			}
		}

		return closestGhost;
	}

	private int getNearestPill(Game game) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		int nearestPillNode = -1;
		int minDistance = Integer.MAX_VALUE;

		int[] activePills = game.getActivePillsIndices();
		for (int pillNode : activePills) {
			int distance = game.getShortestPathDistance(pacmanNode, pillNode);
			if (distance < minDistance) {
				minDistance = distance;
				nearestPillNode = pillNode;
			}
		}

		// tienen prioridad las pills de poder
		int[] activePowerPills = game.getActivePowerPillsIndices();
		for (int powerPillNode : activePowerPills) {
			int distance = game.getShortestPathDistance(pacmanNode, powerPillNode);
			if (distance < minDistance) {
				minDistance = distance;
				nearestPillNode = powerPillNode;
			}
		}

		return nearestPillNode;
	}

	@Override
	public String getName() {
		return "MsPacMan";
	}

}