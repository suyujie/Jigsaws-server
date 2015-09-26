package common.language;

/** 
 * 语言类型
 */
public enum LangType {

	//	　中文 : zh_CN  英文 en_US  西班牙  es_ES
	zh_CN("zh_CN"), zh_TW("zh_TW"), en_US("en_US"), es_ES("es_ES");

	private String sc;

	private LangType(String code) {
		this.sc = code;
	}

	public String asCode() {
		return this.sc;
	}

	public static LangType asEnum(String code) {
		for (LangType type : LangType.values()) {
			if (type.asCode().equals(code)) {
				return type;
			}
		}
		return null;
	}

}
