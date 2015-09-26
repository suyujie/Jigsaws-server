package server.node.system.pay.platform;

import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.Provider;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.security.cert.X509Certificate;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import server.node.system.Root;
import server.node.system.log.PayLog;
import server.node.system.pay.AbstractPaid;
import server.node.system.pay.PayChannel;
import server.node.system.pay.PayStatus;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.rechargePackage.ItemType;
import server.node.system.rechargePackage.RechargePackage;

import common.coin.CoinType;

public class WinPhonePaid extends AbstractPaid {

	/**
	 * 服务器去微软验证订单可用性，
	 * 已用过的订单依然可用，需自行判断是否重复
	 */
	public SystemResult buyGold(Player player, RechargePackage rechargePackage, String winPhoneReceipts, CoinType coinType) {
		SystemResult result = new SystemResult();

		try {
			String certificateId = validateXmlAndGetCertificateId(winPhoneReceipts);//null 表示非法
			String orderId = getOrderId(winPhoneReceipts);//订单号

			if (orderId != null) {
				PayLog payLog = readPayLog(PayChannel.Wp, orderId);

				if (payLog == null) {//第一次使用这个订单号

					if (certificateId != null) {//验证通过
						//加钻
						int buyGold = rechargePackage.getItem().get(ItemType.GOLD);
						Root.playerSystem.changeGold(player, buyGold, GoldType.BUY, true);
						payLog = new PayLog(player.getId(), PayChannel.Wp, orderId, rechargePackage.getId(), coinType, rechargePackage.getCoin().get(coinType), winPhoneReceipts,
								PayStatus.OK);
						result.setCode(ErrorCode.NO_ERROR);
					} else {//验证失败
						payLog = new PayLog(player.getId(), PayChannel.Wp, orderId, rechargePackage.getId(), coinType, rechargePackage.getCoin().get(coinType), winPhoneReceipts,
								PayStatus.VALID_ERROR);
						result.setCode(ErrorCode.RECHARGE_VALID_ERROR);
					}
					addPayLog(payLog);
				} else {//这个订单号 已经出现过，也就是说这次是 前段循环第二次之后的，也有可能是之前验证失败的
					//已补偿   或者  已加钻
					if (payLog.getPayStatus() == PayStatus.COMPENSATE_DONE || payLog.getPayStatus() == PayStatus.OK) {//已经用过的订单号，玩家已经加上钻石了
						result.setCode(ErrorCode.RECHARGE_REPEAT_ERROR);//重复了
					} else {//之前应该是验证失败的
						if (certificateId != null) {//这次验证通过
							//加钻，改订单状态
							int buyGold = rechargePackage.getItem().get(ItemType.GOLD);
							Root.playerSystem.changeGold(player, buyGold, GoldType.BUY, true);
							payLog.setPayStatus(PayStatus.OK);
							updatePayLogStatus(payLog);//更新数据库的支付状态
							result.setCode(ErrorCode.NO_ERROR);
						} else {
							if (payLog.getPayStatus() != PayStatus.VALID_ERROR) {
								payLog.setPayStatus(PayStatus.VALID_ERROR);
								updatePayLogStatus(payLog);//更新数据库的支付状态
							}
							result.setCode(ErrorCode.RECHARGE_VALID_ERROR);
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	private X509Certificate accessMicrosoft(String certificateId) throws Exception {

		String url = "https://go.microsoft.com/fwlink/?LinkId=246509&cid=" + certificateId;

		URL myURL = new URL(url);
		// 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
		HttpsURLConnection httpsConn = (HttpsURLConnection) myURL.openConnection();
		// 取得该连接的输入流，以读取响应内容
		InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());
		// 读取服务器的响应内容并显示
		int respInt = insr.read();

		StringBuffer temp = new StringBuffer();

		while (respInt != -1) {
			temp.append((char) respInt);
			respInt = insr.read();
		}

		return javax.security.cert.X509Certificate.getInstance(temp.toString().getBytes());

	}

	private String validateXmlAndGetCertificateId(String winPhoneReceipts) throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		Document doc = (Document) dbf.newDocumentBuilder().parse(new ByteArrayInputStream(winPhoneReceipts.getBytes()));

		Element root = doc.getDocumentElement();

		String certificateId = root.getAttribute("CertificateId");

		X509Certificate certificate = accessMicrosoft(certificateId);

		certificate.checkValidity(); // Validates OK!

		PublicKey pk = certificate.getPublicKey();

		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) {
			throw new Exception("Cannot find Signature element");
		}

		DOMValidateContext valContext = new DOMValidateContext(pk, nl.item(0));

		String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(providerName).newInstance());

		XMLSignature signature = fac.unmarshalXMLSignature(valContext);
		boolean valid = signature.validate(valContext);

		if (valid) {
			return certificateId;
		}

		return null;
	}

	private String getOrderId(String winPhoneReceipts) throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		Document doc = (Document) dbf.newDocumentBuilder().parse(new ByteArrayInputStream(winPhoneReceipts.getBytes()));

		Element root = doc.getDocumentElement();

		String orderId = getAttr(root, "ProductReceipt", "Id");

		return orderId;
	}

	private ArrayList<Node> findNodes(Element root, String key) {
		NodeList nodes = root.getElementsByTagName(key);
		ArrayList<Node> nodeList = new ArrayList<Node>();
		for (int i = 0; i < nodes.getLength(); i++) {
			nodeList.add(i, (Node) nodes.item(i));
		}
		return nodeList;
	}

	private String getAttr(Element root, String name, String attrName) {
		String result = "";
		ArrayList<Node> resultlist = findNodes(root, name);
		if ((resultlist != null) && (resultlist.size() > 0)) {
			for (int i = 0; i < resultlist.size(); i++) {
				Node node = (Node) resultlist.get(i);
				if (node instanceof Element) {
					if ((node != null) && (node.getNodeName() != null) && (node.getNodeName().equals(name))) {
						//遍历整个xml某节点指定的属性
						NamedNodeMap attrs = node.getAttributes();
						if (attrs.getLength() > 0 && attrs != null) {
							Node attr = attrs.getNamedItem(attrName);
							result = attr.getNodeValue();

						}

					}
				}
			}
		}
		return result;
	}
}
