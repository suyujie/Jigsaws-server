package server.node.system.gameEvents.chipDeathWheel.makingData;

import gamecore.util.Utils;
import javolution.util.FastTable;

public class DeathWheelBattleMapMaking {

	private String name;
	private String field;

	private FastTable<String> fieldTable;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public FastTable<String> getFieldTable() {
		return fieldTable;
	}

	public void setFieldTable(FastTable<String> fieldTable) {
		this.fieldTable = fieldTable;
	}

	public void setTable() {
		if (field != null) {
			String[] ss = field.split("\\|");
			fieldTable = new FastTable<>();
			for (int i = 0; i < ss.length; i++) {
				fieldTable.add(ss[i].trim());
			}
		}

	}

	public String getOneField() {
		return fieldTable.get(Utils.randomInt(0, fieldTable.size() - 1));
	}
}
