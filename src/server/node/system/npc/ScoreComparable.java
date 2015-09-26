package server.node.system.npc;

import java.util.Comparator;

public class ScoreComparable implements Comparator<NpcRobot> {

	@Override
	public int compare(NpcRobot r1, NpcRobot r2) {
		Integer s1 = r1.getScore();
		Integer s2 = r2.getScore();
		return s1.compareTo(s2);
	}

}