package com.lorepo.icplayer.client.module.choice;

import com.google.gwt.xml.client.CDATASection;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.lorepo.icf.properties.BasicPropertyProvider;
import com.lorepo.icf.properties.IEventProperty;
import com.lorepo.icf.properties.IHtmlProperty;
import com.lorepo.icf.properties.IProperty;
import com.lorepo.icf.utils.ExtendedRequestBuilder;
import com.lorepo.icf.utils.StringUtils;
import com.lorepo.icf.utils.XMLUtils;
import com.lorepo.icf.utils.i18n.DictionaryWrapper;
import com.lorepo.icplayer.client.utils.Utils;

public class ChoiceOption extends BasicPropertyProvider{

	private String	text;
	private int		value;
	private String feedback = "";
	private String id;
	private String parentId = "";
	private String baseURL;
	private String contentBaseURL;
	
	//::: Restored by DF: 커스텀  필드추가 ::: ▼▼▼ 
	private int x;
	private int y;
	private int absoluteWidth;
	private int absoluteHeight;
	private int width;
	private int height;
	private String layoutID;
	private Element element;	
	
	//::: Restored by DF: 커스텀  인위추가 ::: ▼▼▼ 
	private String defaultLayoutID = "default";
	
	public ChoiceOption(String id){
		
		super(DictionaryWrapper.get("choice_option"));
		
		this.id = id;
		addPropertyValue();
		addPropertyText();
		addPropertyFeedback();
		
		//::: Restored by DF: 커스텀  필드 초기화 추가 ::: ▼▼▼ 
		this.feedback = "";
		this.parentId = "";
		this.x = -1;
		this.y = -1;
		this.absoluteWidth = -1;
		this.absoluteHeight = -1;
		this.width = -1;
		this.height = -1;
		this.layoutID = this.defaultLayoutID;
	}
	
	//::: Restored by DF: 커스텀  메소드 추가 ::: ▼▼▼ 
	public void setLayoutID(String layoutID) {
		this.layoutID = layoutID;
		this.resetXY(this.element);
	}	
	
	public ChoiceOption(String id, String html, int value){

		this(id);
		this.value = value;
		this.text = html;
	}
	
	
	public String getID(){
		return id;
	}
	
	
	public String getText(){
		return text;
	}
	
	
	public int getValue(){
		return value;
	}

	//::: Restored by DF: 커스텀  메소드 추가 ::: ▼▼▼ 
	public int getX() {
		return this.x;
	}

	//::: Restored by DF: 커스텀  메소드 추가 ::: ▼▼▼ 
	public int getY() {
		return this.y;
	}

	//::: Restored by DF: 커스텀  메소드 추가 ::: ▼▼▼ 
	public int getAbsoluteWidth() {
		return this.absoluteWidth;
	}

	//::: Restored by DF: 커스텀  메소드 추가 ::: ▼▼▼ 
	public int getAbsoluteHeight() {
		return this.absoluteHeight;
	}

	//::: Restored by DF: 커스텀  메소드 추가 ::: ▼▼▼ 
	public int getWidth() {
		return this.width;
	}

	//::: Restored by DF: 커스텀  메소드 추가 ::: ▼▼▼ 
	public int getHeight() {
		return this.height;
	}

	public String getFeedback(){
		return feedback;
	}

	public void setParentId(String id) {
		parentId = id;
	}

	public String getParentId() {
		return parentId;
	}

	public boolean isCorrect() {
		return value > 0;
	}

	protected void setFeedback(String feedback){
		this.feedback = feedback;
	}

	private void resetXY(Element element) {
		NodeList textNodes = element.getElementsByTagName("text");

		try {
			Utils.consoleLog("layoutID  : " + this.layoutID);
			if (this.layoutID == this.defaultLayoutID) {
				this.x = XMLUtils.getAttributeAsInt(element, "x");
				this.y = XMLUtils.getAttributeAsInt(element, "y");

				try {
					this.absoluteWidth = XMLUtils.getAttributeAsInt(element, "absoluteWidth");
					this.absoluteHeight = XMLUtils.getAttributeAsInt(element, "absoluteHeight");
				} catch (Exception var7) {
				}

				try {
					this.width = XMLUtils.getAttributeAsInt(element, "width");
					this.height = XMLUtils.getAttributeAsInt(element, "height");
				} catch (Exception var6) {
				}
			} else {
				this.x = XMLUtils.getAttributeAsInt(element, "x_" + this.layoutID);
				this.y = XMLUtils.getAttributeAsInt(element, "y_" + this.layoutID);
				textNodes = element.getElementsByTagName("text_" + this.layoutID);

				try {
					this.absoluteWidth = XMLUtils.getAttributeAsInt(element, "absoluteWidth_" + this.layoutID);
					this.absoluteHeight = XMLUtils.getAttributeAsInt(element, "absoluteHeight_" + this.layoutID);
				} catch (Exception var5) {
				}

				try {
					this.width = XMLUtils.getAttributeAsInt(element, "width_" + this.layoutID);
					this.height = XMLUtils.getAttributeAsInt(element, "height_" + this.layoutID);
				} catch (Exception var4) {
				}
			}

			if (textNodes.getLength() > 0) {
				Element textElement = (Element) textNodes.item(0);
				this.text = XMLUtils.getCharacterDataFromElement(textElement);
				Utils.consoleLog("text : " + this.layoutID + " / " + this.text);
				Utils.consoleLog("text : " + this.layoutID + " / " + this.text);
				if (this.text == null) {
					this.text = XMLUtils.getText(textElement);
					this.text = StringUtils.unescapeXML(this.text);
				}
			}
		} catch (Exception var8) {
			Utils.consoleLog("resetXY e : " + var8);
		}
	}	
	
	
	
	private void addPropertyText() {

		IHtmlProperty property = new IHtmlProperty() {
				
			@Override
			public void setValue(String newValue) {
				text = newValue;
			}
			
			@Override
			public String getValue() {
				return text;
			}
			
			@Override
			public String getName() {
				return DictionaryWrapper.get("choice_item_text");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("choice_item_text");
			}

			@Override
			public boolean isDefault() {
				return false;
			}
		};
		
		addProperty(property);
	}
	
	private void addPropertyValue() {

		IProperty property = new IProperty() {
			
			@Override
			public void setValue(String newValue) {
				value = Integer.parseInt(newValue);
			}
			
			@Override
			public String getValue() {
				return Integer.toString(value);
			}
			
			@Override
			public String getName() {
				return DictionaryWrapper.get("choice_item_value");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("choice_item_value");
			}

			@Override
			public boolean isDefault() {
				return false;
			}
		};
		
		addProperty(property);
	}
	
	private void addPropertyX() {
		IProperty property = new IProperty() {
			@Override
			public void setValue(String newValue) {
				x = Integer.parseInt(newValue);
			}

			@Override
			public String getValue() {
				return Integer.toString(x);
			}

			@Override
			public String getName() {
				return DictionaryWrapper.get("choice_item_x");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("choice_item_x");
			}

			@Override
			public boolean isDefault() {
				return false;
			}
		};
		addProperty(property);
	}

	private void addPropertyY() {
		IProperty property = new IProperty() {
			@Override
			public void setValue(String newValue) {
				y = Integer.parseInt(newValue);
			}

			@Override
			public String getValue() {
				return Integer.toString(y);
			}

			@Override
			public String getName() {
				return DictionaryWrapper.get("choice_item_y");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("choice_item_y");
			}

			@Override
			public boolean isDefault() {
				return false;
			}
		};
		addProperty(property);
	}

	private void addPropertyFeedback() {

		IProperty property = new IEventProperty() {
				
			public void setValue(String newValue) {
				feedback = newValue;
			}
			
			public String getValue() {
				return feedback;
			}
			
			public String getName() {
				return DictionaryWrapper.get("choice_item_event");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("choice_item_event");
			}

			@Override
			public boolean isDefault() {
				return false;
			}
		};
		
		addProperty(property);
	}


	//::: Restored by DF: 커스텀 수정::: load ▼▼▼ 
	//	public void load(Element element, String baseUrl) {
	//		
	//		value = XMLUtils.getAttributeAsInt(element, "value");
	//		String rawFeedback = "";
	//		
	//		NodeList textNodes = element.getElementsByTagName("text"); 
	//		if(textNodes.getLength() > 0){
	//			Element textElement = (Element) textNodes.item(0);
	//			text = XMLUtils.getCharacterDataFromElement(textElement);
	//			if(text == null){
	//				text = XMLUtils.getText(textElement);
	//				text = StringUtils.unescapeXML(text);
	//			}
	//			if(baseUrl != null){
	//				text = StringUtils.updateLinks(text, baseUrl);
	//			}
	//			
	//			NodeList feedbackNodes = element.getElementsByTagName("feedback");
	//			if(feedbackNodes.getLength() > 0){
	//				rawFeedback = XMLUtils.getText((Element) feedbackNodes.item(0));
	//			}
	//		}
	//		else{
	//			text = XMLUtils.getText((Element) element);
	//		}
	//		
	//		if(!rawFeedback.isEmpty()){
	//			feedback = StringUtils.unescapeXML(rawFeedback);
	//		}
	//
	//		text = text.replaceAll("<!--[\\s\\S]*?-->", "");
	//	}
	public void load(Element element, String baseUrl) {
		this.element = element;
		this.value = XMLUtils.getAttributeAsInt(element, "value");
		String rawFeedback = "";
		NodeList textNodes = element.getElementsByTagName("text");
		if (Utils.isQNote) {
			try {
				if (this.layoutID == this.defaultLayoutID) {
					this.x = XMLUtils.getAttributeAsInt(element, "x");
					this.y = XMLUtils.getAttributeAsInt(element, "y");
				} else {
					this.x = XMLUtils.getAttributeAsInt(element, "x_" + this.layoutID);
					this.y = XMLUtils.getAttributeAsInt(element, "y_" + this.layoutID);
					textNodes = element.getElementsByTagName("text_" + this.layoutID);
				}
			} catch (Exception var8) {
			}

			try {
				if (this.layoutID == this.defaultLayoutID) {
					this.absoluteWidth = XMLUtils.getAttributeAsInt(element, "absoluteWidth");
					this.absoluteHeight = XMLUtils.getAttributeAsInt(element, "absoluteHeight");
				} else {
					this.absoluteWidth = XMLUtils.getAttributeAsInt(element, "absoluteWidth_" + this.layoutID);
					this.absoluteHeight = XMLUtils.getAttributeAsInt(element, "absoluteHeight_" + this.layoutID);
				}
			} catch (Exception var7) {
			}
		}

		if (textNodes.getLength() > 0) {
			Element textElement = (Element) textNodes.item(0);
			this.text = XMLUtils.getCharacterDataFromElement(textElement);
			if (this.text == null) {
				this.text = XMLUtils.getText(textElement);
				this.text = StringUtils.unescapeXML(this.text);
			}

			if (baseUrl != null || this.contentBaseURL != null) {
				this.text = StringUtils.updateLinks(this.text, baseUrl, this.contentBaseURL);
			}

			NodeList feedbackNodes = element.getElementsByTagName("feedback");
			if (feedbackNodes.getLength() > 0) {
				rawFeedback = XMLUtils.getText((Element) feedbackNodes.item(0));
			}
		} else {
			this.text = XMLUtils.getText(element);
		}

		if (!rawFeedback.isEmpty()) {
			this.feedback = StringUtils.unescapeXML(rawFeedback);
		}

		this.text = this.text.replaceAll("<!--[\\s\\S]*?-->", "");
	}
	
	
	//::: Restored by DF: 커스텀 수정::: load ▼▼▼ 
	//	public Element toXML() {
	//		Element optionElement = XMLUtils.createElement("option");
	//		XMLUtils.setIntegerAttribute(optionElement, "value", value);
	//		
	//		Element textElement = XMLUtils.createElement("text");
	//		CDATASection cdataText = XMLUtils.createCDATASection(text);
	//		textElement.appendChild(cdataText);
	//		
	//		Element feedbackElement = XMLUtils.createElement("feedback");		
	//		feedbackElement.appendChild(XMLUtils.createTextNode(StringUtils.escapeHTML(feedback)));
	//
	//		optionElement.appendChild(textElement);
	//		optionElement.appendChild(feedbackElement);
	//		
	//		return optionElement;
	//	}
	public Element toXML() {
		Element optionElement = XMLUtils.createElement("option");
		
		if (this.x > 0) {
			XMLUtils.setIntegerAttribute(optionElement, "x", this.x);
		}

		if (this.y > 0) {
			XMLUtils.setIntegerAttribute(optionElement, "y", this.y);
		}

		if (this.absoluteWidth > 0) {
			XMLUtils.setIntegerAttribute(optionElement, "absoluteWidth", this.absoluteWidth);
		}

		if (this.absoluteHeight > 0) {
			XMLUtils.setIntegerAttribute(optionElement, "absoluteHeight", this.absoluteHeight);
		}
		
		XMLUtils.setIntegerAttribute(optionElement, "value", value);
		
		Element textElement = XMLUtils.createElement("text");
		CDATASection cdataText = XMLUtils.createCDATASection(text);
		textElement.appendChild(cdataText);
		
		Element feedbackElement = XMLUtils.createElement("feedback");		
		feedbackElement.appendChild(XMLUtils.createTextNode(StringUtils.escapeHTML(feedback)));

		optionElement.appendChild(textElement);
		optionElement.appendChild(feedbackElement);
		
		return optionElement;
	}

	public String getBaseURL() {
		return this.baseURL;
	}

	public void setContentBaseURL(String baseURL) {
		this.contentBaseURL = baseURL;
	}

	public String getContentBaseURL() {
		return this.contentBaseURL;
	}

}
