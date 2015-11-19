package server.node.action;

import common.coin.CoinType;
import common.language.LangType;
import gamecore.action.IAction;
import gamecore.io.GameInput;
import gamecore.message.GameRequest;
import gamecore.message.GameResponse;
import gamecore.message.RequestJson;
import gamecore.message.ResponseJson;
import gamecore.security.SHA256;
import gamecore.servlet.AbstractHttpServlet;
import gamecore.util.DataUtils;
import server.node.system.Root;
import server.node.system.player.Player;
import server.node.system.session.Session;

public abstract class AbstractAction extends AbstractHttpServlet implements IAction {

	private static final long serialVersionUID = -8930418998280305692L;

	public static final short SC_OK = 0;
	public static final short SC_ERROR = 1;
	public static final short SC_DISCONNECT = 2;
	public static final short SC_BLACKLIST = 20;

	private static final byte[] salt = { 1, 2, 3 };

	public Session getSession(String sessionId) {
		Session session = Root.sessionSystem.getSession(sessionId);
		return session;
	}

	public Player getPlayer(String sessionId) {

		Session session = getSession(sessionId);
		if (session != null && session.getPlayerId() != null) {
			// 更新session
			Root.sessionSystem.updateOrSaveSession(session);
			try {
				return Root.playerSystem.getPlayer(session.getPlayerId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 验证hashcode加密
	 */
	public boolean checkHashCode(GameInput in, byte[] security) {

		byte[] hc = in.getBytes();
		byte[] bs = in.getCopyRemainBytes();

		bs = DataUtils.arrayConcat(bs, DataUtils.arrayConcat(security, salt));
		byte[] shc = SHA256.Encrypt(bs);

		if (hc.length == shc.length) {
			for (int i = 0; i < shc.length; i++) {
				if (hc[i] != shc[i]) {
					return false;
				}
			}
		} else {
			return false;
		}

		return true;
	}

	public LangType getLangType(String langStr) {
		LangType langType = LangType.en_US;
		if (langStr.equals("Spanish")) {
			langType = LangType.es_ES;
		}
		if (langStr.equals("Chinese")) {
			langType = LangType.zh_CN;
		}
		if (langStr.equals("English")) {
			langType = LangType.en_US;
		}
		return langType;
	}

	public CoinType getCoinType(String coinStr) {
		CoinType coinType = CoinType.asEnum(coinStr);
		if (coinType == null) {
			coinType = CoinType.USD;
		}
		return coinType;
	}

	@Override
	public GameResponse execute(GameRequest msg) {
		return null;
	}

	@Override
	public ResponseJson execute(RequestJson json) {
		return null;
	}

}
