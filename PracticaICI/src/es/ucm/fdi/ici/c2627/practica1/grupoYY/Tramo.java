package es.ucm.fdi.ici.c2627.practica1.grupoYY;

import pacman.game.Constants.MOVE;
			// TODO esta clase la dejo package protected o la meto privada en MsPacMan???
class Tramo {

		final MOVE initialMove;
		final int finalNode;
		final MOVE finalMove;
		final int steps;
		final int pills;
		final int powerPills;

		Tramo(MOVE initialMove, int finalNode, MOVE finalMove,
				int steps, int pills, int powerPills) {
			this.initialMove = initialMove;
			this.finalNode = finalNode;
			this.finalMove = finalMove;
			this.steps = steps;
			this.pills = pills;
			this.powerPills = powerPills;
		}
	}