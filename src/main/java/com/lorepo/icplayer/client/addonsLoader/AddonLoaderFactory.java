package com.lorepo.icplayer.client.addonsLoader;

import com.lorepo.icf.utils.URLUtils;
import com.lorepo.icplayer.client.addonsLoader.IAddonLoader;
import com.lorepo.icplayer.client.addonsLoader.LocalAddonsLoader;
import com.lorepo.icplayer.client.addonsLoader.PrivateAddonLoader;
import com.lorepo.icplayer.client.model.addon.AddonDescriptor;
import com.lorepo.icplayer.client.model.addon.AddonDescriptorFactory;
import com.lorepo.icplayer.client.utils.Utils;

public class AddonLoaderFactory {
	private AddonDescriptorFactory localAddons;
	private String baseUrl;
	private String contentBaseURL;

	public AddonLoaderFactory(String baseUrl, String contentBaseURL) {
		Utils.consoleLog("::: AddonLoaderFactory AddonLoaderFactory 생성자 01 Start !! baseUrl["+baseUrl+"] contentBaseURL["+contentBaseURL+"]:::");
		this.baseUrl = baseUrl;
		this.contentBaseURL = contentBaseURL;
		this.localAddons = AddonDescriptorFactory.getInstance();
	}
	
	public AddonLoaderFactory(String baseUrl) {
		Utils.consoleLog("::: AddonLoaderFactory AddonLoaderFactory 생성자 02 Start !! baseUrl["+baseUrl+"] :::");
		this.baseUrl = baseUrl;
		this.localAddons = AddonDescriptorFactory.getInstance();
	}

	public IAddonLoader getAddonLoader(final AddonDescriptor descriptor) {
		Utils.consoleLog("::: AddonLoaderFactory getAddonLoader Start AddonId[" + descriptor.getAddonId() + "]:::");
		IAddonLoader loader;
		if(localAddons.isLocalAddon(descriptor.getAddonId())) {
			Utils.consoleLog("::: AddonLoaderFactory getAddonLoader 01 LocalAddonsLoader  :::");
			LocalAddonsLoader localLoader = LocalAddonsLoader.getInstance();
			localLoader.setAddonDescriptor(descriptor);
			loader = localLoader;
		} else {
			String url = URLUtils.resolveURL(this.baseUrl, descriptor.getHref(), this.contentBaseURL);
			Utils.consoleLog("::: AddonLoaderFactory getAddonLoader 02 PrivateAddonLoader  :::");
			loader = new PrivateAddonLoader(descriptor, url);
		}

		return loader;
	}
}
