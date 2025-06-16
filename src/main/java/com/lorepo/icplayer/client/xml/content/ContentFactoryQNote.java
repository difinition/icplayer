// Source code is decompiled from a .class file using FernFlower decompiler.
package com.lorepo.icplayer.client.xml.content;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.lorepo.icf.utils.XMLUtils;
import com.lorepo.icplayer.client.model.Content;
import com.lorepo.icplayer.client.model.page.Page;
import com.lorepo.icplayer.client.model.page.PageList;
import com.lorepo.icplayer.client.module.api.player.IContentNode;
import com.lorepo.icplayer.client.utils.Utils;
import com.lorepo.icplayer.client.xml.IParser;
import com.lorepo.icplayer.client.xml.IProducingLoadingListener;
import com.lorepo.icplayer.client.xml.IXMLFactory;
import com.lorepo.icplayer.client.xml.RequestFinishedCallback;
import com.lorepo.icplayer.client.xml.XMLVersionAwareFactoryQNote;
import com.lorepo.icplayer.client.xml.content.parsers.ContentParser_v0;
import com.lorepo.icplayer.client.xml.content.parsers.ContentParser_v1;
import com.lorepo.icplayer.client.xml.content.parsers.ContentParser_v2;
import com.lorepo.icplayer.client.xml.content.parsers.ContentParser_v3;
import com.lorepo.icplayer.client.xml.content.parsers.ContentParser_v4;
import com.lorepo.icplayer.client.xml.content.parsers.IContentParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ContentFactoryQNote extends XMLVersionAwareFactoryQNote {
   private ArrayList<Integer> pagesSubset;
   private Content mContent;
   private static String mainFetchURL;

   protected ContentFactoryQNote(ArrayList<Integer> pagesSubset) {
	   
      Utils.consoleLog("::: ContentFactoryQNote ContentFactoryQNote Start 생성자 ::: ");

      this.setPagesSubset(pagesSubset);
      this.addParser(new ContentParser_v0());
      this.addParser(new ContentParser_v1());
      this.addParser(new ContentParser_v2());
      this.addParser(new ContentParser_v3());
      this.addParser(new ContentParser_v4());
   }

   public void setPagesSubset(ArrayList<Integer> pagesSubset) {
      Utils.consoleLog("::: ContentFactoryQNote setPagesSubset Start ::: ");
      this.pagesSubset = pagesSubset;
   }

   private void addParser(IContentParser parser) {
      Utils.consoleLog("::: ContentFactoryQNote addParser Start ::: ");
      parser.setPagesSubset(this.pagesSubset);
      super.addParser(parser);
   }

   public static IXMLFactory getInstance(ArrayList<Integer> pagesSubset) {
   Utils.consoleLog("::: ContentFactoryQNote getInstance Start ::: ");
      return new ContentFactoryQNote(pagesSubset);
   }

   public static IXMLFactory getInstanceWithAllPages() {
      Utils.consoleLog("::: ContentFactoryQNote getInstanceWithAllPages Start ::: ");
      return getInstance(new ArrayList());
   }

   /* (non-Javadoc)
    * 
    * @see
    * com.lorepo.icplayer.client.xml.XMLVersionAwareFactoryQNote#getContentLoadCallback(com.lorepo.icplayer.client.xml.
    * IProducingLoadingListener) 
    * 
    * 
    *    복원된 필드/메서드                 역할
    *    loadedCount                 현재 로딩 중인 페이지 인덱스
    *    pagesCount                  전체 페이지 수
    *    fetchUrlPages[]             개별 fetch URL 리스트
    *    fetchUrls[]                 전체 fetch 대상 URL
    *    mContent                    최종 Content 객체
    *    produce(...)                XML → Content 변환기
    *    send(...)                   다음 페이지 요청 보내기
    *    setAddonsFromContent(...)   Addon 설정
    *    setAssetsFromContent(...)   Assets 병합
    *    setCSSFromContent(...)      스타일 병합
    *    addPage(...)                페이지 추가
    * 
    * 
    * */
   protected RequestFinishedCallback getContentLoadCallback(final IProducingLoadingListener listener) {
		return new RequestFinishedCallback() {

			@Override
			public void onResponseReceived(String fetchURL, Request request, Response response) {
				Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 01 : fetchURL[" + fetchURL + "] StatusCode[" + response.getStatusCode() + "]");

				if (response.getStatusCode() != 200 && response.getStatusCode() != 0) {
					Utils.consoleLog( "::: ContentFactoryQNote getContentLoadCallback onResponseReceived 02 : Wrong status[" + response.getText() + "]");
					listener.onError("Wrong status: " + response.getText());
				} else {

					Content content;
					if (loadedCount == 0) {
						mContent = produce(response.getText(), fetchURL);
						mainFetchURL = fetchURL;

						Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 03 : main xml [" + response.getText() + "]");
					} else {
						Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 04 : mContent1[" + response.getText() + "]");

						try {
							content = produce(response.getText(), fetchURL);

							if (loadedCount == 1 && !Utils.isQNote) {
								setAddonsFromContent(response.getText(), fetchURL);
							}

							if (!Utils.isQNote) {
								setAssetsFromContent(response.getText(), fetchURL);
								setCSSFromContent(response.getText(), fetchURL);
							}

							// addPage(content.getTableOfContents(), content.getCommonTableOfContents(),
							// pagesSubset);
							addPage(content.getTableOfContents(), content.getCommonTableOfContents(), loadedCount);
						} catch (Exception e) {
							Utils.consoleLog("trace e : " + e);
							listener.onFinishedLoading(null);
						}
					}

					Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 05 : fetchURL[" + fetchURL + "] StatusCode[" + response.getStatusCode() + "]");

					if (loadedCount + 1 < pagesCount) {
						loadedCount++;

						// ::: Restored by DF: fetchUrlPages >> fetchUrls 로 수정
						// send(fetchUrlPages[loadedCount], listener);
						Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 06 : send loadedCount[" + fetchUrls[loadedCount] + "]");
						send(fetchUrls[loadedCount], listener);
					} else if (loadedCount + 1 == pagesCount) {
						content = produce(response.getText(), fetchURL);
						
						Utils.consoleLog( "::: ContentFactoryQNote getContentLoadCallback onResponseReceived 07 : 왜?? fetchUrls[0][" + fetchUrls[0] + "]");
						
						mContent = produce(mContent.toXML(), fetchUrls[0]);
						Utils.consoleLog( "::: ContentFactoryQNote getContentLoadCallback onResponseReceived 08 : mContent.toXML()[" + mContent.toXML() + "]");
						listener.onFinishedLoading(mContent);
						loadedCount++;
					}
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				listener.onFinishedLoading(null);
			}
		};
	}

   private void setAddonsFromContent(String sContentMainXML, String sContentURL) {
      Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 01 : sContentMainXML[" + sContentMainXML + "] sContentURL[" + sContentURL + "]");

      Document mainXML = XMLParser.parse(this.mContent.toXML());
      Document contentMainXML = XMLParser.parse(sContentMainXML);
      HashMap<String, Boolean> essentialAddon = new HashMap();
      essentialAddon.put("Completion_Progress", true);
      essentialAddon.put("Controller_KR", true);
      NodeList addonDescriptor = contentMainXML.getElementsByTagName("addon-descriptor");

      for(int i = 0; i < addonDescriptor.getLength(); ++i) {
         Node node = addonDescriptor.item(i);
         Element ele = (Element)node;
         String addonId = ele.getAttribute("addonId");

         Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 02 : i[" + i + "] addonId[" + addonId + "]");

         if (essentialAddon.containsKey(addonId)) {
            essentialAddon.put(addonId, false);
         }

         String tmpHref = "../icplayer/addons/" + addonId + ".xml";

         Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 03 : i[" + i + "] href[" + tmpHref + "]");

         ele.setAttribute("href", tmpHref);
      }

      try {
    	  Iterator<String> it = essentialAddon.keySet().iterator();

         while(it.hasNext()) {
            String addonID = (String)it.next();
            Document xmlDocument = XMLParser.createDocument();
            Element xmlElement = xmlDocument.createElement("addon-descriptor");
            if ((Boolean)essentialAddon.get(addonID)) {
               xmlElement.setAttribute("addonId", addonID);
               xmlElement.setAttribute("href", "../icplayer/addons/" + addonID + ".xml");
               addonDescriptor.item(0).getParentNode().appendChild(xmlElement);
            }
         }
      } catch (Exception ignored) {
      }

      NodeList addons = mainXML.getElementsByTagName("addons");
      NodeList contentAddons = contentMainXML.getElementsByTagName("addons");

      try {
         addons.item(0).getParentNode().replaceChild(contentAddons.item(0), addons.item(0));
         Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 04 : addonsStr[" + addons.toString() + "]");
      } catch (Exception var11) {
      }

      String xmlString = mainXML.toString();
      Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 05 : mainFetchURL[" + mainFetchURL + "]");
      Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 06 : xmlString[" + xmlString + "]");
      this.mContent = this.produce(xmlString, mainFetchURL);
      
      Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent End ::: ");
      
   }

   private void setAssetsFromContent(String sContentMainXML, String sContentURL) {

    Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent Start ::: ");

	try {
         Document mainXML = XMLParser.parse(this.mContent.toXML());
         Document contentMainXML = XMLParser.parse(sContentMainXML);
         NodeList assets = mainXML.getElementsByTagName("assets");
         NodeList contentAssets = contentMainXML.getElementsByTagName("asset");
         String prefixURL = sContentURL.split("/pages/")[0];
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 01 assets[" + assets.toString() + "]");
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 02 contentAssets[" + contentAssets.toString() + "]");
         Node node = assets.item(0);
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 03 newNode.getChildNodes().getLength()[" + contentAssets.getLength() + "]");
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 04 newNode.getChildNodes().toString()[" + contentAssets + "]");

         while(contentAssets.getLength() > 0) {
            Node asset = contentAssets.item(0);
            Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 05 newNode.getChildNodes().item(i) asset[" + asset + " : " + contentAssets.getLength() + "]");
            node.appendChild(asset);
         }

         String xmlString = mainXML.toString();
         this.mContent = this.produce(xmlString, mainFetchURL);
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 06 mainXML [" + mainXML.toString() + "]");
      } catch (Exception var10) {
      }

   }

   private void setCSSFromContent(String sContentMainXML, String sContentURL) {
	  Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent Start ::: ");

      Document mainXML = XMLParser.parse(this.mContent.toXML());
      Document contentMainXML = XMLParser.parse(sContentMainXML);
      NodeList styles = mainXML.getElementsByTagName("styles");
      NodeList contentStyles = contentMainXML.getElementsByTagName("styles");
      String prefixURL = sContentURL.split("/pages/")[0];
      Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent 01 sContentURL[" + sContentURL + "]");
      Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent 02 prefixURL[" + prefixURL + "]");
      Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent 03 styles[" + styles.toString() + "]");

      for(int i = 0; i < styles.getLength(); ++i) {
         Node node = styles.item(i);
         Node newNode = contentStyles.item(0);
         node.getParentNode().replaceChild(newNode, node);
      }

      String xmlString = mainXML.toString().replaceAll("\\.\\./resources/", prefixURL + "/resources/");
      Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent 04 xmlString[" + xmlString + "]");
      this.mContent = this.produce(xmlString, mainFetchURL);
      Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent 05 mainXML[" + mainXML.toString() + "]");
   }

   private void addPage(IContentNode pageNode, IContentNode commonNode, int index) {
      Utils.consoleLog("::: ContentFactoryQNote addPage Start :::");

      Utils.consoleLog("::: ContentFactoryQNote addPage 01 fetchUrlPages[" + this.fetchUrlPages[index] + "]");
      Utils.consoleLog("::: ContentFactoryQNote addPage 02 pageNode[" + pageNode.toXML() + "]");
      String strPages = "<pages>" + pageNode.toXML() + "</pages>";
      Document xmlPages = XMLParser.parse(strPages);
      Utils.consoleLog("::: ContentFactoryQNote addPage 04 fetchUrlPages[loadedCount][" + this.fetchUrlPages[this.loadedCount] + "]");
      Utils.consoleLog("::: ContentFactoryQNote addPage 05 XMLParser.parse(pageNode.toXML())[" + xmlPages.toString() + "]");
      Element xml = null;
      NodeList nodeList;
      if (this.fetchUrlPages[this.loadedCount] != "main.xml") {
         nodeList = xmlPages.getElementsByTagName("page");

         for(int i = 0; i < nodeList.getLength(); ++i) {
            Element page = (Element)nodeList.item(i);
            if (page.getAttribute("href") == this.fetchUrlPages[this.loadedCount]) {
               Utils.consoleLog("::: ContentFactoryQNote addPage 06 page[" + page.toString() + "]");
               Utils.consoleLog("::: ContentFactoryQNote addPage 07 href[" + page.getAttribute("href") + "]");
               xml = XMLParser.parse(page.toString()).getDocumentElement();
               break;
            }
         }
      } else {
         nodeList = xmlPages.getElementsByTagName("page");
         Element page = (Element)nodeList.item(0);
         Utils.consoleLog("::: ContentFactoryQNote addPage 08 addPage page[" + page.toString() + "]");
         Utils.consoleLog("::: ContentFactoryQNote addPage 09 addPage href[" + page.getAttribute("href") + "]");
         xml = XMLParser.parse(page.toString()).getDocumentElement();
      }

      Utils.consoleLog("::: ContentFactoryQNote addPage 11 xml[" + xml + "]");
      Utils.consoleLog("::: ContentFactoryQNote addPage 12 pageNode[" + pageNode + "]");
      String href = XMLUtils.getAttributeAsString(xml, "href", "");
      Utils.consoleLog("::: ContentFactoryQNote addPage 13 href[" + href + "]");
      
      Utils.currentPageHref = href;
      String urlPath = "../../../" + Utils.getPath(this.fetchUrls[index]);
      if (this.fetchUrls[index].startsWith("http") || this.fetchUrls[index].startsWith("HTTP")) {
         urlPath = Utils.getPath(this.fetchUrls[index]);
      }

      Utils.consoleLog("::: ContentFactoryQNote addPage 14 fetchUrls[index][" + index + ", " + this.fetchUrls[index] + ", " + urlPath + "]");
      xml.setAttribute("href", urlPath + href);
      PageList page = new PageList();
      Page p = page.loadPage(xml);
      if (commonNode != null) {
         PageList pageList = (PageList)commonNode;
         PageList commonPageList = new PageList();

         for(int i = 0; i < pageList.size(); ++i) {
            String pageName = ((Page)pageList.get(i)).getName();
            if (pageName == "header") {
               String commonPage = pageList.get(i).toXML();
               xml = XMLParser.parse(commonPage).getDocumentElement();
               Utils.consoleLog("::: ContentFactoryQNote addPage 15-1i["+i+"] commonPage[" + commonPage + "]");
               String commonHref = ((Page)pageList.get(i)).getHref();
               Utils.consoleLog("::: ContentFactoryQNote addPage 15-2i["+i+"] commonHref[" + commonHref + "]");
               xml.setAttribute("href", urlPath + commonHref);
               Page cp = page.loadPage(xml);
               commonPageList.add(cp);
            }
         }

         this.mContent.setCommonPages(commonPageList);
      }

      this.mContent.addPage(p);
      
      Utils.consoleLog("::: ContentFactoryQNote addPage End :::");
   }

   public Content produce(String xmlString, String fetchUrl) {
      Utils.consoleLog("::: ContentFactoryQNote produce Start :::");
      Utils.consoleLog("::: ContentFactoryQNote produce 01 fetchUrl["+fetchUrl+"] :::");
      Utils.consoleLog("::: ContentFactoryQNote produce 02 xmlString["+xmlString+"] :::");
      
      try {
         Element xml = XMLParser.parse(xmlString).getDocumentElement();
         String version = XMLUtils.getAttributeAsString(xml, "version", "1");
         Utils.consoleLog("::: ContentFactoryQNote produce 03 version[" + version+"] :::");
         Content producedContent = (Content)((IParser)this.parsersMap.get(version)).parse(xml);
         producedContent.setBaseUrl(fetchUrl);
         Utils.consoleLog("::: ContentFactoryQNote produce End 01 :::");
         return producedContent;
      } catch (Exception e) {
         Utils.consoleLog("::: ContentFactoryQNote produce 04 produce xmlString[" + xmlString+"] :::");
         Utils.consoleLog("::: ContentFactoryQNote produce 05 produce fetchUrl[" + fetchUrl+"] :::");
         Utils.consoleLog("::: ContentFactoryQNote produce 06 produce e[" + e +"] :::");
         Utils.consoleLog("::: ContentFactoryQNote produce End 02 :::");
         return null;
      }
   }

   public void unload() {
      Utils.consoleLog("::: ContentFactoryQNote unload Start :::");
      try {
         Utils.consoleLog("unload");
      } catch (Exception e) {
         Utils.consoleLog("unload e : " + e);
      }
      Utils.consoleLog("::: ContentFactoryQNote unload End :::");
   }
}
