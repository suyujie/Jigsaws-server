package server.node.system.task;

import java.io.Serializable;

import server.node.system.task.task.TaskAttackWinNum;
import server.node.system.task.task.TaskCupNum;
import server.node.system.task.task.TaskDefenceWinNum;
import server.node.system.task.task.TaskFriendNum;
import server.node.system.task.task.TaskGainCashNum;
import server.node.system.task.task.TaskGivePowerNum;
import server.node.system.task.task.TaskHireRobotNum;
import server.node.system.task.task.TaskHitNum;
import server.node.system.task.task.TaskHomeLevel;
import server.node.system.task.task.TaskRentRobotNum;
import server.node.system.task.task.TaskRobotQualityNum;
import server.node.system.task.task.TaskStarNum;
import server.node.system.task.task.battle.TaskWinWithWeapon1Num;
import server.node.system.task.task.battle.TaskWinWithWeapon2Num;
import server.node.system.task.task.battle.TaskWinWithWeapon3Num;
import server.node.system.task.task.battle.TaskWinWithWeapon4Num;
import server.node.system.task.task.battle.TaskWinWithWeapon5Num;
import server.node.system.task.task.paint.TaskPaintNum;
import server.node.system.task.task.paint.TaskPaintPartQuality0Num;
import server.node.system.task.task.paint.TaskPaintPartQuality1Num;
import server.node.system.task.task.paint.TaskPaintPartQuality2Num;
import server.node.system.task.task.paint.TaskPaintPartQuality3Num;
import server.node.system.task.task.paint.TaskPaintPartQuality4Num;
import server.node.system.task.task.partLevel.TaskPartLevel;
import server.node.system.task.task.partLevel.TaskPartQuality0Level;
import server.node.system.task.task.partLevel.TaskPartQuality1Level;
import server.node.system.task.task.partLevel.TaskPartQuality2Level;
import server.node.system.task.task.partLevel.TaskPartQuality3Level;
import server.node.system.task.task.partLevel.TaskPartQuality4Level;
import server.node.system.task.task.partNum.TaskPartNum;
import server.node.system.task.task.partNum.TaskPartQuality2Num;
import server.node.system.task.task.partNum.TaskPartQuality3Num;
import server.node.system.task.task.partNum.TaskPartQuality4Num;
import server.node.system.task.task.pvp.TaskPvpBeatQuality2RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatQuality3RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatQuality4RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatQualityRobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatRobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatSuitRobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon1RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon2RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon3RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon4RobotNum;
import server.node.system.task.task.pvp.TaskPvpBeatWeapon5RobotNum;
import server.node.system.task.task.pvp.TaskPvpGetCash;
import server.node.system.task.task.pvp.TaskPvpWinLevel;

/**
 * 任务task 包  实体
 */
public class TaskBagPO implements Serializable {

	private static final long serialVersionUID = 7964478076057292753L;

	public TaskHomeLevel taskHomeLevel;
	public TaskStarNum taskStarNum;
	public TaskPartNum taskPartNum;
	public TaskPartQuality2Num taskPartQuality2Num;
	public TaskPartQuality3Num taskPartQuality3Num;
	public TaskPartQuality4Num taskPartQuality4Num;
	public TaskPartLevel taskPartLevel;
	public TaskPartQuality0Level taskPartQuality0Level;
	public TaskPartQuality1Level taskPartQuality1Level;
	public TaskPartQuality2Level taskPartQuality2Level;
	public TaskPartQuality3Level taskPartQuality3Level;
	public TaskPartQuality4Level taskPartQuality4Level;
	public TaskCupNum taskCupNum;
	public TaskAttackWinNum taskAttackWinNum;
	public TaskDefenceWinNum taskDefenceWinNum;
	public TaskPvpBeatRobotNum taskPvpBeatRobotNum;
	public TaskPvpBeatSuitRobotNum taskPvpBeatSuitRobotNum;
	public TaskPvpBeatQualityRobotNum taskPvpBeatQualityRobotNum;
	public TaskPvpBeatQuality2RobotNum taskPvpBeatQuality2RobotNum;
	public TaskPvpBeatQuality3RobotNum taskPvpBeatQuality3RobotNum;
	public TaskPvpBeatQuality4RobotNum taskPvpBeatQuality4RobotNum;

	public TaskPvpBeatWeapon1RobotNum taskPvpBeatWeapon1RobotNum;
	public TaskPvpBeatWeapon2RobotNum taskPvpBeatWeapon2RobotNum;
	public TaskPvpBeatWeapon3RobotNum taskPvpBeatWeapon3RobotNum;
	public TaskPvpBeatWeapon4RobotNum taskPvpBeatWeapon4RobotNum;
	public TaskPvpBeatWeapon5RobotNum taskPvpBeatWeapon5RobotNum;

	public TaskPvpWinLevel taskPvpWinLevel;
	public TaskHitNum taskHitNum;
	public TaskPvpGetCash taskPvpGetCash;
	public TaskRentRobotNum taskRentRobotNum;
	public TaskHireRobotNum taskHireRobotNum;
	public TaskFriendNum taskFriendNum;
	public TaskGainCashNum taskGainCashNum;
	public TaskGivePowerNum taskGivePowerNum;

	public TaskWinWithWeapon3Num taskWinWithWeapon3Num;
	public TaskWinWithWeapon2Num taskWinWithWeapon2Num;
	public TaskWinWithWeapon5Num taskWinWithWeapon5Num;
	public TaskWinWithWeapon4Num taskWinWithWeapon4Num;
	public TaskWinWithWeapon1Num taskWinWithWeapon1Num;

	public TaskPaintNum taskPaintNum;
	public TaskPaintPartQuality0Num taskPaintPartQuality0Num;
	public TaskPaintPartQuality1Num taskPaintPartQuality1Num;
	public TaskPaintPartQuality2Num taskPaintPartQuality2Num;
	public TaskPaintPartQuality3Num taskPaintPartQuality3Num;
	public TaskPaintPartQuality4Num taskPaintPartQuality4Num;

	public TaskRobotQualityNum taskRobotQualityNum;

	public TaskBagPO() {
	}

	public TaskPvpWinLevel getTaskPvpWinLevel() {
		return taskPvpWinLevel;
	}

	public void setTaskPvpWinLevel(TaskPvpWinLevel taskPvpWinLevel) {
		this.taskPvpWinLevel = taskPvpWinLevel;
	}

	public TaskHireRobotNum getTaskHireRobotNum() {
		return taskHireRobotNum;
	}

	public void setTaskHireRobotNum(TaskHireRobotNum taskHireRobotNum) {
		this.taskHireRobotNum = taskHireRobotNum;
	}

	public TaskHomeLevel getTaskHomeLevel() {
		return taskHomeLevel;
	}

	public void setTaskHomeLevel(TaskHomeLevel taskHomeLevel) {
		this.taskHomeLevel = taskHomeLevel;
	}

	public TaskStarNum getTaskStarNum() {
		return taskStarNum;
	}

	public void setTaskStarNum(TaskStarNum taskStarNum) {
		this.taskStarNum = taskStarNum;
	}

	public TaskPartNum getTaskPartNum() {
		return taskPartNum;
	}

	public void setTaskPartNum(TaskPartNum taskPartNum) {
		this.taskPartNum = taskPartNum;
	}

	public TaskPartQuality2Num getTaskPartQuality2Num() {
		return taskPartQuality2Num;
	}

	public void setTaskPartQuality2Num(TaskPartQuality2Num taskPartQuality2Num) {
		this.taskPartQuality2Num = taskPartQuality2Num;
	}

	public TaskPartQuality3Num getTaskPartQuality3Num() {
		return taskPartQuality3Num;
	}

	public void setTaskPartQuality3Num(TaskPartQuality3Num taskPartQuality3Num) {
		this.taskPartQuality3Num = taskPartQuality3Num;
	}

	public TaskPartQuality4Num getTaskPartQuality4Num() {
		return taskPartQuality4Num;
	}

	public void setTaskPartQuality4Num(TaskPartQuality4Num taskPartQuality4Num) {
		this.taskPartQuality4Num = taskPartQuality4Num;
	}

	public TaskPartLevel getTaskPartLevel() {
		return taskPartLevel;
	}

	public void setTaskPartLevel(TaskPartLevel taskPartLevel) {
		this.taskPartLevel = taskPartLevel;
	}

	public TaskPartQuality0Level getTaskPartQuality0Level() {
		return taskPartQuality0Level;
	}

	public void setTaskPartQuality0Level(TaskPartQuality0Level taskPartQuality0Level) {
		this.taskPartQuality0Level = taskPartQuality0Level;
	}

	public TaskPartQuality1Level getTaskPartQuality1Level() {
		return taskPartQuality1Level;
	}

	public void setTaskPartQuality1Level(TaskPartQuality1Level taskPartQuality1Level) {
		this.taskPartQuality1Level = taskPartQuality1Level;
	}

	public TaskPartQuality2Level getTaskPartQuality2Level() {
		return taskPartQuality2Level;
	}

	public void setTaskPartQuality2Level(TaskPartQuality2Level taskPartQuality2Level) {
		this.taskPartQuality2Level = taskPartQuality2Level;
	}

	public TaskPartQuality3Level getTaskPartQuality3Level() {
		return taskPartQuality3Level;
	}

	public void setTaskPartQuality3Level(TaskPartQuality3Level taskPartQuality3Level) {
		this.taskPartQuality3Level = taskPartQuality3Level;
	}

	public TaskPartQuality4Level getTaskPartQuality4Level() {
		return taskPartQuality4Level;
	}

	public void setTaskPartQuality4Level(TaskPartQuality4Level taskPartQuality4Level) {
		this.taskPartQuality4Level = taskPartQuality4Level;
	}

	public TaskCupNum getTaskCupNum() {
		return taskCupNum;
	}

	public void setTaskCupNum(TaskCupNum taskCupNum) {
		this.taskCupNum = taskCupNum;
	}

	public TaskAttackWinNum getTaskAttackWinNum() {
		return taskAttackWinNum;
	}

	public void setTaskAttackWinNum(TaskAttackWinNum taskAttackWinNum) {
		this.taskAttackWinNum = taskAttackWinNum;
	}

	public TaskDefenceWinNum getTaskDefenceWinNum() {
		return taskDefenceWinNum;
	}

	public void setTaskDefenceWinNum(TaskDefenceWinNum taskDefenceWinNum) {
		this.taskDefenceWinNum = taskDefenceWinNum;
	}

	public TaskPvpBeatRobotNum getTaskPvpBeatRobotNum() {
		return taskPvpBeatRobotNum;
	}

	public void setTaskPvpBeatRobotNum(TaskPvpBeatRobotNum taskPvpBeatRobotNum) {
		this.taskPvpBeatRobotNum = taskPvpBeatRobotNum;
	}

	public TaskHitNum getTaskHitNum() {
		return taskHitNum;
	}

	public void setTaskHitNum(TaskHitNum taskHitNum) {
		this.taskHitNum = taskHitNum;
	}

	public TaskPvpGetCash getTaskPvpGetCash() {
		return taskPvpGetCash;
	}

	public void setTaskPvpGetCash(TaskPvpGetCash taskPvpGetCash) {
		this.taskPvpGetCash = taskPvpGetCash;
	}

	public TaskRentRobotNum getTaskRentRobotNum() {
		return taskRentRobotNum;
	}

	public void setTaskRentRobotNum(TaskRentRobotNum taskRentRobotNum) {
		this.taskRentRobotNum = taskRentRobotNum;
	}

	public TaskFriendNum getTaskFriendNum() {
		return taskFriendNum;
	}

	public void setTaskFriendNum(TaskFriendNum taskFriendNum) {
		this.taskFriendNum = taskFriendNum;
	}

	public TaskGainCashNum getTaskGainCashNum() {
		return taskGainCashNum;
	}

	public void setTaskGainCashNum(TaskGainCashNum taskGainCashNum) {
		this.taskGainCashNum = taskGainCashNum;
	}

	public TaskGivePowerNum getTaskGivePowerNum() {
		return taskGivePowerNum;
	}

	public void setTaskGivePowerNum(TaskGivePowerNum taskGivePowerNum) {
		this.taskGivePowerNum = taskGivePowerNum;
	}

	public TaskPvpBeatSuitRobotNum getTaskPvpBeatSuitRobotNum() {
		return taskPvpBeatSuitRobotNum;
	}

	public void setTaskPvpBeatSuitRobotNum(TaskPvpBeatSuitRobotNum taskPvpBeatSuitRobotNum) {
		this.taskPvpBeatSuitRobotNum = taskPvpBeatSuitRobotNum;
	}

	public TaskPvpBeatQualityRobotNum getTaskPvpBeatQualityRobotNum() {
		return taskPvpBeatQualityRobotNum;
	}

	public void setTaskPvpBeatQualityRobotNum(TaskPvpBeatQualityRobotNum taskPvpBeatQualityRobotNum) {
		this.taskPvpBeatQualityRobotNum = taskPvpBeatQualityRobotNum;
	}

	public TaskPvpBeatQuality2RobotNum getTaskPvpBeatQuality2RobotNum() {
		return taskPvpBeatQuality2RobotNum;
	}

	public void setTaskPvpBeatQuality2RobotNum(TaskPvpBeatQuality2RobotNum taskPvpBeatQuality2RobotNum) {
		this.taskPvpBeatQuality2RobotNum = taskPvpBeatQuality2RobotNum;
	}

	public TaskPvpBeatQuality3RobotNum getTaskPvpBeatQuality3RobotNum() {
		return taskPvpBeatQuality3RobotNum;
	}

	public void setTaskPvpBeatQuality3RobotNum(TaskPvpBeatQuality3RobotNum taskPvpBeatQuality3RobotNum) {
		this.taskPvpBeatQuality3RobotNum = taskPvpBeatQuality3RobotNum;
	}

	public TaskPvpBeatQuality4RobotNum getTaskPvpBeatQuality4RobotNum() {
		return taskPvpBeatQuality4RobotNum;
	}

	public void setTaskPvpBeatQuality4RobotNum(TaskPvpBeatQuality4RobotNum taskPvpBeatQuality4RobotNum) {
		this.taskPvpBeatQuality4RobotNum = taskPvpBeatQuality4RobotNum;
	}

	public TaskPvpBeatWeapon1RobotNum getTaskPvpBeatWeapon1RobotNum() {
		return taskPvpBeatWeapon1RobotNum;
	}

	public void setTaskPvpBeatWeapon1RobotNum(TaskPvpBeatWeapon1RobotNum taskPvpBeatWeapon1RobotNum) {
		this.taskPvpBeatWeapon1RobotNum = taskPvpBeatWeapon1RobotNum;
	}

	public TaskPvpBeatWeapon2RobotNum getTaskPvpBeatWeapon2RobotNum() {
		return taskPvpBeatWeapon2RobotNum;
	}

	public void setTaskPvpBeatWeapon2RobotNum(TaskPvpBeatWeapon2RobotNum taskPvpBeatWeapon2RobotNum) {
		this.taskPvpBeatWeapon2RobotNum = taskPvpBeatWeapon2RobotNum;
	}

	public TaskPvpBeatWeapon3RobotNum getTaskPvpBeatWeapon3RobotNum() {
		return taskPvpBeatWeapon3RobotNum;
	}

	public void setTaskPvpBeatWeapon3RobotNum(TaskPvpBeatWeapon3RobotNum taskPvpBeatWeapon3RobotNum) {
		this.taskPvpBeatWeapon3RobotNum = taskPvpBeatWeapon3RobotNum;
	}

	public TaskPvpBeatWeapon4RobotNum getTaskPvpBeatWeapon4RobotNum() {
		return taskPvpBeatWeapon4RobotNum;
	}

	public void setTaskPvpBeatWeapon4RobotNum(TaskPvpBeatWeapon4RobotNum taskPvpBeatWeapon4RobotNum) {
		this.taskPvpBeatWeapon4RobotNum = taskPvpBeatWeapon4RobotNum;
	}

	public TaskPvpBeatWeapon5RobotNum getTaskPvpBeatWeapon5RobotNum() {
		return taskPvpBeatWeapon5RobotNum;
	}

	public void setTaskPvpBeatWeapon5RobotNum(TaskPvpBeatWeapon5RobotNum taskPvpBeatWeapon5RobotNum) {
		this.taskPvpBeatWeapon5RobotNum = taskPvpBeatWeapon5RobotNum;
	}

	public TaskRobotQualityNum getTaskRobotQualityNum() {
		return taskRobotQualityNum;
	}

	public void setTaskRobotQualityNum(TaskRobotQualityNum taskRobotQualityNum) {
		this.taskRobotQualityNum = taskRobotQualityNum;
	}

	public TaskWinWithWeapon3Num getTaskWinWithWeapon3Num() {
		return taskWinWithWeapon3Num;
	}

	public void setTaskWinWithWeapon3Num(TaskWinWithWeapon3Num taskWinWithWeapon3Num) {
		this.taskWinWithWeapon3Num = taskWinWithWeapon3Num;
	}

	public TaskWinWithWeapon2Num getTaskWinWithWeapon2Num() {
		return taskWinWithWeapon2Num;
	}

	public void setTaskWinWithWeapon2Num(TaskWinWithWeapon2Num taskWinWithWeapon2Num) {
		this.taskWinWithWeapon2Num = taskWinWithWeapon2Num;
	}

	public TaskWinWithWeapon5Num getTaskWinWithWeapon5Num() {
		return taskWinWithWeapon5Num;
	}

	public void setTaskWinWithWeapon5Num(TaskWinWithWeapon5Num taskWinWithWeapon5Num) {
		this.taskWinWithWeapon5Num = taskWinWithWeapon5Num;
	}

	public TaskWinWithWeapon4Num getTaskWinWithWeapon4Num() {
		return taskWinWithWeapon4Num;
	}

	public void setTaskWinWithWeapon4Num(TaskWinWithWeapon4Num taskWinWithWeapon4Num) {
		this.taskWinWithWeapon4Num = taskWinWithWeapon4Num;
	}

	public TaskWinWithWeapon1Num getTaskWinWithWeapon1Num() {
		return taskWinWithWeapon1Num;
	}

	public void setTaskWinWithWeapon1Num(TaskWinWithWeapon1Num taskWinWithWeapon1Num) {
		this.taskWinWithWeapon1Num = taskWinWithWeapon1Num;
	}

	public TaskPaintNum getTaskPaintNum() {
		return taskPaintNum;
	}

	public void setTaskPaintNum(TaskPaintNum taskPaintNum) {
		this.taskPaintNum = taskPaintNum;
	}

	public TaskPaintPartQuality0Num getTaskPaintPartQuality0Num() {
		return taskPaintPartQuality0Num;
	}

	public void setTaskPaintPartQuality0Num(TaskPaintPartQuality0Num taskPaintPartQuality0Num) {
		this.taskPaintPartQuality0Num = taskPaintPartQuality0Num;
	}

	public TaskPaintPartQuality1Num getTaskPaintPartQuality1Num() {
		return taskPaintPartQuality1Num;
	}

	public void setTaskPaintPartQuality1Num(TaskPaintPartQuality1Num taskPaintPartQuality1Num) {
		this.taskPaintPartQuality1Num = taskPaintPartQuality1Num;
	}

	public TaskPaintPartQuality2Num getTaskPaintPartQuality2Num() {
		return taskPaintPartQuality2Num;
	}

	public void setTaskPaintPartQuality2Num(TaskPaintPartQuality2Num taskPaintPartQuality2Num) {
		this.taskPaintPartQuality2Num = taskPaintPartQuality2Num;
	}

	public TaskPaintPartQuality3Num getTaskPaintPartQuality3Num() {
		return taskPaintPartQuality3Num;
	}

	public void setTaskPaintPartQuality3Num(TaskPaintPartQuality3Num taskPaintPartQuality3Num) {
		this.taskPaintPartQuality3Num = taskPaintPartQuality3Num;
	}

	public TaskPaintPartQuality4Num getTaskPaintPartQuality4Num() {
		return taskPaintPartQuality4Num;
	}

	public void setTaskPaintPartQuality4Num(TaskPaintPartQuality4Num taskPaintPartQuality4Num) {
		this.taskPaintPartQuality4Num = taskPaintPartQuality4Num;
	}

}
