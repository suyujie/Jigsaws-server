package server.node.system.egg;

import gamecore.io.ByteArrayGameOutput;
import server.node.system.player.MoneyType;
import server.node.system.robotPart.Part;

public class EggPart {

	private Integer id;

	private Integer colorId;
	private Integer expPartId;
	private Part part;

	public EggPart() {
	}

	public EggPart(Integer id, Integer colorId, Integer expPartId, Part part) {
		super();
		this.id = id;
		this.colorId = colorId;
		this.expPartId = expPartId;
		this.part = part;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getColorId() {
		return colorId;
	}

	public void setColorId(Integer colorId) {
		this.colorId = colorId;
	}

	public Integer getExpPartId() {
		return expPartId;
	}

	public void setExpPartId(Integer expPartId) {
		this.expPartId = expPartId;
	}

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public byte[] toByteArrayAsEgg(int gold) {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();

		if (getPart() != null) {
			try {
				bago.putInt(MoneyType.GOLD.asCode());
				bago.putInt(gold);
				bago.putLong(getPart().getStoreId());
				bago.putShort(getPart().getMakingId().shortValue());
				bago.putShort((short) getPart().getPartSlotType());
				bago.putShort((short) getPart().getLevel());

				if (getPart().getBufVal() != null && !getPart().getBufVal().isEmpty()) {
					bago.putInt(getPart().getBufVal().size());
					for (Integer bv : getPart().getBufVal()) {
						bago.putInt(bv);
					}
				} else {
					bago.putInt(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (getExpPartId() != null) {
			try {
				bago.putInt(MoneyType.GOLD.asCode());
				bago.putInt(gold);
				bago.putLong(getExpPartId());
				bago.putShort(getExpPartId().shortValue());
				bago.putShort((short) 5);
				bago.putShort((short) 1);

				//buff  当做部件来传输
				bago.putInt(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return bago.toByteArray();
	}

}
