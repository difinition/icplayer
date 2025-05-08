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
      this.setPagesSubset(pagesSubset);
      this.addParser(new ContentParser_v0());
      this.addParser(new ContentParser_v1());
      this.addParser(new ContentParser_v2());
      this.addParser(new ContentParser_v3());
      this.addParser(new ContentParser_v4());
   }

   public void setPagesSubset(ArrayList<Integer> pagesSubset) {
      this.pagesSubset = pagesSubset;
   }

   private void addParser(IContentParser parser) {
      parser.setPagesSubset(this.pagesSubset);
      super.addParser(parser);
   }

   public static IXMLFactory getInstance(ArrayList<Integer> pagesSubset) {
      Utils.consoleLog("ContentFactoryQNote getInstance");
      return new ContentFactoryQNote(pagesSubset);
   }

   public static IXMLFactory getInstanceWithAllPages() {
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
               Utils.consoleLog("getContentLoadCallback response.getStatusCode() : " + fetchURL + ", " + response.getStatusCode());

               if (response.getStatusCode() != 200 && response.getStatusCode() != 0) {
                   listener.onError("Wrong status: " + response.getText());
                   return;
               }

               try {
                   Content content;
                   if (loadedCount == 0) {
                       mContent = produce(response.getText(), fetchURL);
                       mainFetchURL = fetchURL;
                       Utils.consoleLog("main xml : " + response.getText());
                   } else {
                       Utils.consoleLog("mContent1 : " + response.getText());

                       content = produce(response.getText(), fetchURL);

                       if (loadedCount == 1 && !Utils.isQNote) {
                           setAddonsFromContent(response.getText(), fetchURL);
                       }

                       if (!Utils.isQNote) {
                           setAssetsFromContent(response.getText(), fetchURL);
                           setCSSFromContent(response.getText(), fetchURL);
                       }

                       //addPage(content.getTableOfContents(), content.getCommonTableOfContents(), pagesSubset);
                       addPage(content.getTableOfContents(), content.getCommonTableOfContents(), loadedCount);
                   }

                   Utils.consoleLog("load loadedCount : " + loadedCount + ", " + pagesCount + ", " + fetchUrlPages[loadedCount]);

                   if (loadedCount + 1 < pagesCount) {
                       loadedCount++;
                       
                       //kslee fetchUrlPages >> fetchUrls 로 수정
                       //send(fetchUrlPages[loadedCount], listener);
                       send(fetchUrls[loadedCount], listener);
                   } else if (loadedCount + 1 == pagesCount) {
                       content = produce(response.getText(), fetchURL);
                       mContent = produce(mContent.toXML(), fetchUrls[0]);
                       Utils.consoleLog("getContentLoadCallback mContent.toXML() : " + mContent.toXML());
                       listener.onFinishedLoading(mContent);
                       loadedCount++;
                   }

               } catch (Exception e) {
                   Utils.consoleLog("trace e : " + e);
                   listener.onFinishedLoading(null);
               }
           }

           @Override
           public void onError(Request request, Throwable exception) {
               listener.onFinishedLoading(null);
           }
       };
   }

   private void setAddonsFromContent(String sContentMainXML, String sContentURL) {
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
         if (essentialAddon.containsKey(addonId)) {
            essentialAddon.put(addonId, false);
         }

         ele.setAttribute("href", "../icplayer/addons/" + addonId + ".xml");
      }

      try {
         Iterator var13 = essentialAddon.keySet().iterator();

         while(var13.hasNext()) {
            String addonID = (String)var13.next();
            Document xmlDocument = XMLParser.createDocument();
            Element xmlElement = xmlDocument.createElement("addon-descriptor");
            if ((Boolean)essentialAddon.get(addonID)) {
               xmlElement.setAttribute("addonId", addonID);
               xmlElement.setAttribute("href", "../icplayer/addons/" + addonID + ".xml");
               addonDescriptor.item(0).getParentNode().appendChild(xmlElement);
            }
         }
      } catch (Exception var12) {
      }

      NodeList addons = mainXML.getElementsByTagName("addons");
      NodeList contentAddons = contentMainXML.getElementsByTagName("addons");

      try {
         addons.item(0).getParentNode().replaceChild(contentAddons.item(0), addons.item(0));
         Utils.consoleLog("setAddonsFromContent  : " + addons.toString());
      } catch (Exception var11) {
      }

      String xmlString = mainXML.toString();
      Utils.consoleLog("setAddonsFromContent xmlString : " + xmlString);
      this.mContent = this.produce(xmlString, mainFetchURL);
      Utils.consoleLog("setAddonsFromContent mainXML : " + mainXML.toString());
   }

   private void setAssetsFromContent(String sContentMainXML, String sContentURL) {
      try {
         Document mainXML = XMLParser.parse(this.mContent.toXML());
         Document contentMainXML = XMLParser.parse(sContentMainXML);
         NodeList assets = mainXML.getElementsByTagName("assets");
         NodeList contentAssets = contentMainXML.getElementsByTagName("asset");
         String prefixURL = sContentURL.split("/pages/")[0];
         Utils.consoleLog("setAssetsFromContent assets : " + assets.toString());
         Utils.consoleLog("setAssetsFromContent contentAssets : " + contentAssets.toString());
         Node node = assets.item(0);
         Utils.consoleLog("setAssetsFromContent newNode.getChildNodes().getLength() : " + contentAssets.getLength());
         Utils.consoleLog("setAssetsFromContent newNode.getChildNodes().toString() : " + contentAssets);

         while(contentAssets.getLength() > 0) {
            Node asset = contentAssets.item(0);
            Utils.consoleLog("setAssetsFromContent newNode.getChildNodes().item(i) : " + asset + " : " + contentAssets.getLength());
            node.appendChild(asset);
         }

         String xmlString = mainXML.toString();
         this.mContent = this.produce(xmlString, mainFetchURL);
         Utils.consoleLog("setAssetsFromContent mainXML : " + mainXML.toString());
      } catch (Exception var10) {
      }

   }

   private void setCSSFromContent(String sContentMainXML, String sContentURL) {
      Document mainXML = XMLParser.parse(this.mContent.toXML());
      Document contentMainXML = XMLParser.parse(sContentMainXML);
      NodeList styles = mainXML.getElementsByTagName("styles");
      NodeList contentStyles = contentMainXML.getElementsByTagName("styles");
      String prefixURL = sContentURL.split("/pages/")[0];
      Utils.consoleLog("setCSSFromContent sContentURL : " + sContentURL);
      Utils.consoleLog("setCSSFromContent prefixURL : " + prefixURL);
      Utils.consoleLog("setCSSFromContent styles : " + styles.toString());

      for(int i = 0; i < styles.getLength(); ++i) {
         Node node = styles.item(i);
         Node newNode = contentStyles.item(0);
         node.getParentNode().replaceChild(newNode, node);
      }

      String xmlString = mainXML.toString().replaceAll("\\.\\./resources/", prefixURL + "/resources/");
      Utils.consoleLog("setCSSFromContent xmlString : " + xmlString);
      this.mContent = this.produce(xmlString, mainFetchURL);
      Utils.consoleLog("setCSSFromContent mainXML : " + mainXML.toString());
   }

   private void addPage(IContentNode pageNode, IContentNode commonNode, int index) {
      Utils.consoleLog("addPage fetchUrlPages: " + this.fetchUrlPages[index]);
      Utils.consoleLog("addPage pageNode: " + pageNode.toXML());
      String strPages = "<pages>" + pageNode.toXML() + "</pages>";
      Document xmlPages = XMLParser.parse(strPages);
      Utils.consoleLog("addPage fetchUrlPages[loadedCount] : " + this.fetchUrlPages[this.loadedCount]);
      Utils.consoleLog("addPage XMLParser.parse(pageNode.toXML()): " + xmlPages.toString());
      Element xml = null;
      NodeList nodeList;
      if (this.fetchUrlPages[this.loadedCount] != "main.xml") {
         nodeList = xmlPages.getElementsByTagName("page");

         for(int i = 0; i < nodeList.getLength(); ++i) {
            Element page = (Element)nodeList.item(i);
            if (page.getAttribute("href") == this.fetchUrlPages[this.loadedCount]) {
               Utils.consoleLog("addPage page: " + page.toString());
               Utils.consoleLog("addPage page href: " + page.getAttribute("href"));
               xml = XMLParser.parse(page.toString()).getDocumentElement();
               break;
            }
         }
      } else {
         nodeList = xmlPages.getElementsByTagName("page");
         Element page = (Element)nodeList.item(0);
         Utils.consoleLog("addPage page: " + page.toString());
         Utils.consoleLog("addPage page href: " + page.getAttribute("href"));
         xml = XMLParser.parse(page.toString()).getDocumentElement();
      }

      Utils.consoleLog("addPage xml: " + xml);
      Utils.consoleLog("addPage xml pageNode: " + pageNode);
      String href = XMLUtils.getAttributeAsString(xml, "href", "");
      Utils.consoleLog("addPage href : " + href);
      Utils.currentPageHref = href;
      String urlPath = "../../../" + Utils.getPath(this.fetchUrls[index]);
      if (this.fetchUrls[index].startsWith("http") || this.fetchUrls[index].startsWith("HTTP")) {
         urlPath = Utils.getPath(this.fetchUrls[index]);
      }

      Utils.consoleLog("fetchUrls[index]t : " + index + ", " + this.fetchUrls[index] + ", " + urlPath);
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
               Utils.consoleLog("commonPage: " + commonPage);
               String commonHref = ((Page)pageList.get(i)).getHref();
               Utils.consoleLog("commonPage commonHref: " + commonHref);
               xml.setAttribute("href", urlPath + commonHref);
               Page cp = page.loadPage(xml);
               commonPageList.add(cp);
            }
         }

         this.mContent.setCommonPages(commonPageList);
      }

      this.mContent.addPage(p);
   }

   public Content produce(String xmlString, String fetchUrl) {
      try {
         Element xml = XMLParser.parse(xmlString).getDocumentElement();
         String version = XMLUtils.getAttributeAsString(xml, "version", "1");
         Utils.consoleLog("version : " + version);
         Content producedContent = (Content)((IParser)this.parsersMap.get(version)).parse(xml);
         producedContent.setBaseUrl(fetchUrl);
         return producedContent;
      } catch (Exception var6) {
         Utils.consoleLog("produce xmlString :" + xmlString);
         Utils.consoleLog("produce fetchUrl :" + fetchUrl);
         Utils.consoleLog("produce e :" + var6);
         return null;
      }
   }

   public void unload() {
      try {
         Utils.consoleLog("unload");
      } catch (Exception var2) {
         Utils.consoleLog("unload e : " + var2);
      }

   }
}
