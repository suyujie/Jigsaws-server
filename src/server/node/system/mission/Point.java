package server.node.system.mission;

import gamecore.io.ByteArrayGameOutput;

public class Point {

	private Integer makingId;//小关卡的制作id
	private Integer star;//目前的显示星级
	private Integer passStar;//已通过星级

	public Point() {
	}

	public Point(Integer makingId, Integer star, Integer passStar) {
		super();
		this.makingId = makingId;
		this.star = star;
		this.passStar = passStar;
	}

	public Integer getMakingId() {
		return makingId;
	}

	public void setMakingId(Integer makingId) {
		this.makingId = makingId;
	}

	public Integer getStar() {
		return star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	public Integer getPassStar() {
		return passStar;
	}

	public void setPassStar(Integer passStar) {
		this.passStar = passStar;
	}

	public byte[] toByteArray(PointFlushBag pointFlushBag) {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putInt(makingId);
			bago.putInt(star);
			bago.putInt(passStar);

			if (pointFlushBag == null) {
				bago.put((byte) 0);
			} else {
				PointFlush pointFlush = pointFlushBag.readPointFlush(makingId);
				if (pointFlushBag == null || pointFlush == null) {
					bago.put((byte) 0);
				} else {
					bago.put((byte) pointFlush.getFlushNum());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bago.toByteArray();
	}

}
