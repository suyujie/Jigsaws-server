package server.node.system.robot;

import java.util.List;

import gamecore.trigger.TopicMessage;
import server.node.system.player.Player;
import server.node.system.robotPart.Part;

/**
 * robot messsage
 */
public final class RobotMessage extends TopicMessage {

	public static final String ROBOT_PART_LEVELUP = "robot_part_level_up";//升级
	public static final String ROBOT_PART_EVOLUTION = "robot_part_evolution";//进化

	public static final String ROBOT_READ_BAG = "robot_read_bag";//读取robotBag

	public static final String ROBOT_PAINT = "robot_paint";

	public static final String ROBOT_CHANGE_PART = "robot_change_part";

	public static final String ROBOT_GET_NEW = "robot_get_new";

	public static final String ROBOT_REPAIRE = "robot_repaire";
	//直接用钻修
	public static final String ROBOT_REPAIREOK_WITH_GOLD_NO_BEGIN = "robot_repaireok_with_gold_no_begin";
	//已经开始修理了,用钻加速到完成
	public static final String ROBOT_REPAIREOK_WITH_GOLD_HAVE_BEGIN = "robot_repaireok_with_gold_have_begin";

	private Player player;
	private Part part;
	private RobotBag robotBag;
	private Robot robot;
	private List<Part> paintParts;
	private boolean levelUp;
	private long repairAllEndTime;//所有机器人修理完成后的时间点

	public RobotMessage(String name, Player player) {
		super(name);
		this.player = player;
	}

	public RobotMessage(String name, Player player, long repairAllEndTime) {
		super(name);
		this.player = player;
		this.repairAllEndTime = repairAllEndTime;
	}

	public RobotMessage(String name, Player player, Robot robot) {
		super(name);
		this.player = player;
		this.robot = robot;
	}

	public RobotMessage(String name, Player player, List<Part> paintParts) {
		super(name);
		this.player = player;
		this.paintParts = paintParts;
	}

	public RobotMessage(String name, Player player, Robot robot, Part part, boolean levelUp) {
		super(name);
		this.player = player;
		this.robot = robot;
		this.part = part;
		this.levelUp = levelUp;
	}

	public RobotMessage(String name, Player player, RobotBag robotBag) {
		super(name);
		this.player = player;
		this.robotBag = robotBag;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public RobotBag getRobotBag() {
		return robotBag;
	}

	public void setRobotBag(RobotBag robotBag) {
		this.robotBag = robotBag;
	}

	public List<Part> getPaintParts() {
		return paintParts;
	}

	public void setPaintParts(List<Part> paintParts) {
		this.paintParts = paintParts;
	}

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public boolean isLevelUp() {
		return levelUp;
	}

	public void setLevelUp(boolean levelUp) {
		this.levelUp = levelUp;
	}

	public long getRepairAllEndTime() {
		return repairAllEndTime;
	}

	public void setRepairAllEndTime(long repairAllEndTime) {
		this.repairAllEndTime = repairAllEndTime;
	}

}
