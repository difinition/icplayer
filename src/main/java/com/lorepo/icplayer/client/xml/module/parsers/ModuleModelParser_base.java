package com.lorepo.icplayer.client.xml.module.parsers;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.lorepo.icf.utils.StringUtils;
import com.lorepo.icf.utils.UUID;
import com.lorepo.icf.utils.XMLUtils;
import com.lorepo.icplayer.client.dimensions.ModuleDimensions;
import com.lorepo.icplayer.client.metadata.IMetadata;
import com.lorepo.icplayer.client.metadata.Metadata;
import com.lorepo.icplayer.client.module.text.TextModel;
import com.lorepo.icplayer.client.utils.Utils;

public abstract class ModuleModelParser_base implements IModuleModelParser {

	protected String version;
	protected IModuleModelBuilder module;
	protected String defaultLayoutID = "default";
	
	public ModuleModelParser_base() {}
	
	@Override
	public void setModule(IModuleModelBuilder module) {
		this.module = module;
	}

	@Override
	public String getVersion() {
		return this.version;
	}
	
	@Override
	public void setDefaultLayoutID(String layoutID) {
		defaultLayoutID = layoutID;
	}
	
	@Override
	public Object parse(Element xml) {
		
		Utils.consoleLog("::: ■■ Goal -14 ModuleModelParser_base parse Start :::");
		
		this.parsePosition(xml);
		this.parseModuleAttributes(xml);
		this.parseModuleStyleAttributes(xml);
		
		NodeList nodes = xml.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			Node childNode = nodes.item(i);
			
			if(childNode.getNodeName().compareTo("button") == 0 && childNode instanceof Element) {
				this.module.setButtonType(StringUtils.unescapeXML(((Element) childNode).getAttribute("type")));
			}else if(childNode.getNodeName().compareTo("layout") == 0 && childNode instanceof Element) {
				this.module.loadLayout((Element) childNode);
			} else if(childNode.getNodeName().compareTo("layouts") == 0 && childNode instanceof Element) {
				this.parseLayouts((Element) childNode);
			} else if(childNode.getNodeName().compareTo("styles") == 0 && childNode instanceof Element) {
				this.parseStyles((Element) childNode);
			} else if(childNode.getNodeName().compareTo("metadata") == 0 && childNode instanceof Element) {
				this.parseMetadata((Element) childNode);
			}
		}
		
		return this.module;
	}

	private void parseMetadata(Element childNode) {
		IMetadata metadata = new Metadata();
		metadata.parse(childNode);
		this.module.setMetadata(metadata);
	}

	protected void parseStyles(Element childNode) {}

	protected void parseLayouts(Element childNode) {}

	protected void parsePosition(Element xml) {
		int left = XMLUtils.getAttributeAsInt(xml, "left");
		int top = XMLUtils.getAttributeAsInt(xml, "top");
		int right = XMLUtils.getAttributeAsInt(xml, "right");
		int bottom = XMLUtils.getAttributeAsInt(xml, "bottom");
		int width = XMLUtils.getAttributeAsInt(xml, "width");
		int height = XMLUtils.getAttributeAsInt(xml, "height");

		ModuleDimensions dimensions = new ModuleDimensions(left, right, top, bottom, height, width);
		this.module.addSemiResponsiveDimensions("default", dimensions);
	}
	
	protected void parseModuleAttributes(Element xml) {
		
		Utils.consoleLog("::: ◆◆◆■■ Goal -15 ModuleModelParser_base parseModuleAttributes Start Parser module hash["+System.identityHashCode(this.module)+"]:::");
		
		String id = parseModuleID(xml);
		
		Utils.consoleLog("::: ■■ Goal -15 ModuleModelParser_base parseModuleAttributes 01 id["+id+"] :::");
		
		
		this.module.setID(id);
		this.module.setIsVisible(XMLUtils.getAttributeAsBoolean(xml, "isVisible", true));
		this.module.setIsLocked(XMLUtils.getAttributeAsBoolean(xml, "isLocked", false));
		this.module.setModuleInEditorVisibility(XMLUtils.getAttributeAsBoolean(xml, "isModuleVisibleInEditor", true));
		
		Utils.consoleLog("::: ■■ Goal -15 ModuleModelParser_base parseModuleAttributes 02 isTabindexEnabled["+XMLUtils.getAttributeAsBoolean(xml, "isTabindexEnabled", false)+"] :::");
		
		this.module.setIsTabindexEnabled(XMLUtils.getAttributeAsBoolean(xml, "isTabindexEnabled", false));
		this.module.setOmitInKeyboardNavigation(XMLUtils.getAttributeAsBoolean(xml, "shouldOmitInKeyboardNavigation", false));
		this.module.setOmitInTTS(XMLUtils.getAttributeAsBoolean(xml, "shouldOmitInTTS", false));
		String title = parseModuleTitle(xml);
		this.module.setTTSTitle(title);
		
		Utils.consoleLog("::: ◆◆◆■■ Goal -15 ModuleModelParser_base parseModuleAttributes 04 Parser module hash["+System.identityHashCode(this.module)+"]:::");
	}
	
	protected void parseModuleStyleAttributes(Element xml) {
		String style = StringUtils.unescapeXML(xml.getAttribute("style"));
		String styleClass = StringUtils.unescapeXML(xml.getAttribute("class"));
		
		
		if(style != null && style.trim().compareTo("") != 0) {
			this.module.setInlineStyle(style);			
		}

		if(styleClass != null && styleClass.trim().compareTo("") != 0) {
			this.module.setStyleClass(styleClass);	
		}
	}

	protected String parseModuleID(Element xml) {
		String id = xml.getAttribute("id");
		if (id == null || id.compareTo("null") == 0) {
			id = UUID.uuid(6);
		} else {
			id = StringUtils.unescapeXML(id);
		}
		return id;
	}

	protected String parseModuleTitle(Element xml) {
		String title = xml.getAttribute("ttsTitle");
		if (title == null || title.compareTo("null") == 0) {
			title = "";
		} else {
			title = StringUtils.unescapeXML(title);
		}
		return title;
	}
}
