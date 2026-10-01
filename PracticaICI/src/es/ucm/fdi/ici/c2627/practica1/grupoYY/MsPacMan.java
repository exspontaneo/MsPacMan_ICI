package es.ucm.fdi.ici.c2627.practica1.grupoYY;

import java.awt.Color;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class MsPacMan extends PacmanController {

	private static final int DANGER_DISTANCE = 20;
	private static final boolean DEBUG = true;

	@Override
	public MOVE getMove(Game game, long timeDue) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		
		// En un pasillo no hay ninguna decisión que tomar.
		if (!game.isJunction(pacmanNode))
			return null;
		
		// PARA HACER DEBUG: Obtener la información de cada tramo que sale del cruce.
		Tramo[] tramos = getTramos(game, pacmanNode);

		if (DEBUG)
			printTramos(tramos);

		// Comprobar fantasmas peligrosos.
		GHOST nearestChasingGhost = getNearestChasingGhost(game, DANGER_DISTANCE);

		if (nearestChasingGhost != null) {
			int ghostNode = game.getGhostCurrentNodeIndex(nearestChasingGhost);

			if (DEBUG)
				GameView.addLines(game, Color.RED, pacmanNode, ghostNode);

			return game.getApproximateNextMoveAwayFromTarget(
					pacmanNode,
					ghostNode,
					game.getPacmanLastMoveMade(),
					DM.PATH
					);
		}


		// Comprobar fantasmas comestibles.
		GHOST nearestEdibleGhost = getNearestEdibleGhost(game, DANGER_DISTANCE);

		if (nearestEdibleGhost != null) {
			int ghostNode = game.getGhostCurrentNodeIndex(nearestEdibleGhost);

			if (DEBUG)
				GameView.addLines(game, Color.GREEN, pacmanNode, ghostNode);

			return game.getApproximateNextMoveTowardsTarget(
					pacmanNode,
					ghostNode,
					game.getPacmanLastMoveMade(),
					DM.PATH
					);
		}


		// Ir a la píldora más cercana.
		int nearestPillNode = getNearestPill(game);

		if (nearestPillNode != -1) {
			if (DEBUG)
				GameView.addLines(game, Color.BLUE, pacmanNode, nearestPillNode);

			return game.getNextMoveTowardsTarget(
					pacmanNode,
					nearestPillNode,
					game.getPacmanLastMoveMade(),
					DM.PATH
					);
		}


		// Movimiento por defecto.
		return game.getPacmanLastMoveMade();
	}

	private GHOST getNearestChasingGhost(Game game, int limit) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();

		GHOST closestGhost = null;
		int minDistance = Integer.MAX_VALUE;

		for (GHOST ghost : GHOST.values()) {
			// Solo fantasmas fuera de la cárcel y no comestibles.
			if (game.getGhostLairTime(ghost) == 0 && !game.isGhostEdible(ghost)) {
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				int distance = game.getShortestPathDistance(pacmanNode, ghostNode);

				if (distance >= 0 && distance <= limit && distance < minDistance) {
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
			// Solo fantasmas comestibles.
			if (game.isGhostEdible(ghost)) {
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				int distance = game.getShortestPathDistance(pacmanNode, ghostNode);

				if (distance >= 0 && distance <= limit && distance < minDistance) {
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

		// Píldoras normales.
		for (int pillNode : game.getActivePillsIndices()) {
			int distance = game.getShortestPathDistance(pacmanNode, pillNode);

			if (distance >= 0 && distance < minDistance) {
				minDistance = distance;
				nearestPillNode = pillNode;
			}
		}

		// Por ahora tratamos las píldoras de poder como una píldora más.
		for (int powerPillNode : game.getActivePowerPillsIndices()) {
			int distance = game.getShortestPathDistance(pacmanNode, powerPillNode);

			if (distance >= 0 && distance < minDistance) {
				minDistance = distance;
				nearestPillNode = powerPillNode;
			}
		}

		return nearestPillNode;
	}

	private Tramo[] getTramos(Game game, int pacmanNode) {
		MOVE[] possibleMoves = game.getPossibleMoves(pacmanNode, game.getPacmanLastMoveMade());

		Tramo[] tramos = new Tramo[possibleMoves.length];

		int[] activePills = game.getActivePillsIndices();
		int[] activePowerPills = game.getActivePowerPillsIndices();

		for (int i = 0; i < possibleMoves.length; i++)
			tramos[i] = recorrerTramo(game, pacmanNode, possibleMoves[i], 
					activePills, activePowerPills);

		return tramos;
	}

	private Tramo recorrerTramo(Game game, int startNode, MOVE initialMove,
			int[] activePills, int[] activePowerPills) {

		int currentNode = startNode;
		MOVE currentMove = initialMove;

		int steps = 0;
		int pills = 0;
		int powerPills = 0;
		
		boolean stillGoing = true;
		while (stillGoing) {
			int nextNode = game.getNeighbour(currentNode, currentMove);

			if (nextNode == -1)
				stillGoing = false;

			currentNode = nextNode;
			steps++;

			if (containsNode(activePills, currentNode))
				pills++;

			if (containsNode(activePowerPills, currentNode))
				powerPills++;

			// Hemos llegado al siguiente cruce.
			if (game.isJunction(currentNode))
				stillGoing = false;

			// Dentro del pasillo solo hay una salida que no sea retroceder.
			MOVE[] possibleMoves = game.getPossibleMoves(currentNode, currentMove);

			if (possibleMoves.length == 0)
				stillGoing = false;

			currentMove = possibleMoves[0];
		}

		return new Tramo(initialMove, currentNode, currentMove, steps, pills, powerPills);
	}

	private boolean containsNode(int[] nodes, int searchedNode) {
		for (int node : nodes)
			if (node == searchedNode)
				return true;

		return false;
	}

	private void printTramos(Tramo[] tramos) {
		System.out.println("NUEVO CRUCE");

		for (Tramo tramo : tramos) {
			System.out.println(
					"Salida: " + tramo.initialMove
					+ " | Cruce: " + tramo.finalNode
					+ " | Llegada: " + tramo.finalMove
					+ " | Pasos: " + tramo.steps
					+ " | Pills: " + tramo.pills
					+ " | Power pills: " + tramo.powerPills
					);
		}
	}

	@Override
	public String getName() {
		return "MsPacMan";
	}
}