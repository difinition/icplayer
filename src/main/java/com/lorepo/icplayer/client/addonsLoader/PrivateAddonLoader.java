package com.lorepo.icplayer.client.addonsLoader;

import com.lorepo.icf.utils.ILoadListener;
import com.lorepo.icf.utils.IXMLSerializable;
import com.lorepo.icf.utils.XMLLoader;
import com.lorepo.icplayer.client.utils.Utils;

public class PrivateAddonLoader extends XMLLoader implements IAddonLoader{
	
	private String url;
	private String xml;

	public PrivateAddonLoader(IXMLSerializable model, String url) {
		super(model);
		this.url = url;
	}
	
	public void load(ILoadListener listener) {
		Utils.consoleLog("::: PrivateAddonLoader load Start : url[" + url + "] :::");
		this.load(this.url, listener);
	}

	public String getXML() {
		Utils.consoleLog("::: PrivateAddonLoader getXML Start :::");
	    return xml;
	}
	
	@Override
	protected void successCallback(String xmlString, String resolvedURL) {
		this.xml = xmlString;
		super.successCallback(xmlString, resolvedURL);
	}
}
