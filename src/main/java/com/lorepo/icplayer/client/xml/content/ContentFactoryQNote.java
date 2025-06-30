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
	   
      Utils.consoleLog("::: ■▲ ContentFactoryQNote ContentFactoryQNote Start 생성자 ::: ");

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
   Utils.consoleLog("::: ■▲ ContentFactoryQNote getInstance Start ::: ");
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
				//첫번째는 eng.xml
				//두번째는 main.xml
				Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 01 : fetchURL[" + fetchURL + "] StatusCode[" + response.getStatusCode() + "]");

				if (response.getStatusCode() != 200 && response.getStatusCode() != 0) {
					//첫번째는 eng.xml
					Utils.consoleLog( "::: ContentFactoryQNote getContentLoadCallback onResponseReceived 02 : Wrong status[" + response.getText() + "]");
					listener.onError("Wrong status: " + response.getText());
				} else {

					Content content;
					if (loadedCount == 0) {
						//첫번째는 eng.xml
						mContent = produce(response.getText(), fetchURL);
						mainFetchURL = fetchURL;

						//로그내용 : eng.xml가 들어있는 Content를 만들어냄
						Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 03 : mContent.xml["+mContent.toXML()+"]:::");
					} else {
						//두번째는 main.xml
						Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 04 : mContent1[" + response.getText() + "]");

						try {
							//로그내용 : main.xml가 들어있는 Content를 만들어냄 :: 오호 여기서 메타를...
							//TODO: 흠 여기에서 메타가 아직 안들어 왔네 확인하자 아니다 여기는 mContent아니라 content이지!!
							content = produce(response.getText(), fetchURL);
							
							//여기는 loadedCount == 1 이면서 Utils.isQNote가 false이니 무조건 true
							if (loadedCount == 1 && !Utils.isQNote) {
								
								// 결국 여기서 하는일은 mContent(eng.xml내용)에  main.xml에 있는 <metadata>를 추가함 
								setMetadataFromContent(response.getText(), fetchURL);
								
								// main.xml 내용과 main.xml url을 가지고 들어감  
								// 결국 여기서 하는일은 mContent(eng.xml내용)에 애드온을 main.xml에 있는 애드온만 및 필수애드온을 추려서 빼고 갱신 하고 있음 
								setAddonsFromContent(response.getText(), fetchURL);
							}

							if (!Utils.isQNote) {
								// main.xml 내용과 main.xml url을 가지고 들어감 
								// 결국 여기서 하는일은 mContent(eng.xml내용)에  main.xml에 있는 <asset>를 추가하고 있음 
								setAssetsFromContent(response.getText(), fetchURL);
								
								// main.xml 내용과 main.xml url을 가지고 들어감 
								// 처음에 저장한 eng.xml인 mainXML의 <styles>노드에 입력:main.xml으로 contentMainXML의 <styles> 내용으로 교체(replace)
								setCSSFromContent(response.getText(), fetchURL);
							}

							// addPage(content.getTableOfContents(), content.getCommonTableOfContents(),
							// pagesSubset);
							addPage(content.getTableOfContents(), content.getCommonTableOfContents(), loadedCount);
							
						   //  mContent(eng.xml내용) 내용 <page>노드가 거의 끝에 있는데 로그값에 잘 들어가는것 확인함
							Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 04-1 mContent.xml["+mContent.toXML()+"]:::");
							
						} catch (Exception e) {
							listener.onFinishedLoading(null);
						}
					}

					//첫번째는 eng.xml
					//두번째는 main.xml
					Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 05 : fetchURL[" + fetchURL + "] StatusCode[" + response.getStatusCode() + "]");

					if (loadedCount + 1 < pagesCount) {
						//첫번째는 eng.xml
						loadedCount++;

						// ::: Restored by DF: fetchUrlPages >> fetchUrls 로 수정
						// send(fetchUrlPages[loadedCount], listener);
						Utils.consoleLog("::: ContentFactoryQNote getContentLoadCallback onResponseReceived 06 : send loadedCount[" + loadedCount + "]");
						send(fetchUrls[loadedCount], listener);
					} else if (loadedCount + 1 == pagesCount) {
						//두번째는 main.xml 마지막
						//content = produce(response.getText(), fetchURL);
						
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

   // 결국 여기서 하는일은 mContent(eng.xml내용)에  main.xml에 있는 <asset>를 추가하고 있음 
   private void setMetadataFromContent(String sContentMainXML, String sContentURL) {

    // main.xml 내용과 main.xml url을 가지고 들어옮  
    Utils.consoleLog("::: ContentFactoryQNote setMetadataFromContent Start ::: ");

	try {
         // 처음에 저장한 eng.xml 콘텐츠가 들어있는 내용
         Document mainXML = XMLParser.parse(this.mContent.toXML());
         
         // 입력 : main.xml 내용이 들어가 있음
         Document contentMainXML = XMLParser.parse(sContentMainXML);
         
         NodeList metaDatas = mainXML.getElementsByTagName("metadata");
         NodeList contentMetaDatas = contentMainXML.getElementsByTagName("metadata");

         Utils.consoleLog("::: ContentFactoryQNote setMetadataFromContent 01 assets[" + metaDatas.toString() + "]");
         Utils.consoleLog("::: ContentFactoryQNote setMetadataFromContent 02 contentAssets[" + contentMetaDatas.toString() + "]");
         Node node = metaDatas.item(0);
         Utils.consoleLog("::: ContentFactoryQNote setMetadataFromContent 03 newNode.getChildNodes().getLength()[" + contentMetaDatas.getLength() + "]");
         Utils.consoleLog("::: ContentFactoryQNote setMetadataFromContent 04 newNode.getChildNodes().toString()[" + contentMetaDatas + "]");

         // 처음에 저장한 eng.xml인 mainXML의 빈<metadata/>노드에 입력:main.xml으로 contentMainXML의 <metadata> 내용을 추가(빈노드에 추가)
         for (int i = 0; i < contentMetaDatas.getLength(); i++) {
             Node metadata = contentMetaDatas.item(i);
             Node importedNode = mainXML.importNode(metadata, true);
             Utils.consoleLog("::: ContentFactoryQNote setMetadataFromContent 05 newNode.getChildNodes().item(i) asset[" + metadata + " : " + contentMetaDatas.getLength() + "]");
             node.appendChild(importedNode);
         }

          String xmlString = mainXML.toString();

          this.mContent = this.produce(xmlString, mainFetchURL);
          // <asset>노드가 거의 끝에 있는데 로그값에 잘 들어가는것 확인함
          Utils.consoleLog("::: ContentFactoryQNote setMetadataFromContent 06 mainXML [" + mainXML.toString() + "]");
      } catch (Exception var10) {
      }

   }   
   
   // 결국 여기서 하는일은 mContent(eng.xml내용)에 애드온을 main.xml에 있는 애드온만 및 필수애드온을 추려서 빼고 갱신 하고 있음 
   private void setAddonsFromContent(String sContentMainXML, String sContentURL) {
      // main.xml 내용과 main.xml url을 가지고 들어옮  
      Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 01 : sContentMainXML[" + sContentMainXML + "] sContentURL[" + sContentURL + "]");

      // 처음에 저장한 eng.xml 콘텐츠가 들어있는 내용
      Document mainXML = XMLParser.parse(this.mContent.toXML());
      
      // 입력 : main.xml 내용이 들어가 있음
      Document contentMainXML = XMLParser.parse(sContentMainXML);
      
      //필수 애드온 준비
      HashMap<String, Boolean> essentialAddon = new HashMap();
      essentialAddon.put("Completion_Progress", true);
      essentialAddon.put("Controller_KR", true);
      
      // 입력 : main.xml의 addon-descriptor 처리 처리
      NodeList addonDescriptor = contentMainXML.getElementsByTagName("addon-descriptor");

      // 입력 : main.xml 
      for(int i = 0; i < addonDescriptor.getLength(); ++i) {
         Node node = addonDescriptor.item(i);
         Element ele = (Element)node;
         String addonId = ele.getAttribute("addonId");

         Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 02 : i[" + i + "] addonId[" + addonId + "]");

         // 필수 애드온에 포함되어 있으면 false로 변경 (이미 존재함을 표시)
         if (essentialAddon.containsKey(addonId)) {
            essentialAddon.put(addonId, false);
         }

         // ※애초 파일로부터의 eng.xml에는 아래와 같이 기본으로 되어있는데 같은걸 갈아끼우고 있었네
         String tmpHref = "../icplayer/addons/" + addonId + ".xml";

         Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 03 : i[" + i + "] href[" + tmpHref + "]");

         // href 속성을 ../icplayer/addons/{addonId}.xml 로 수정
         ele.setAttribute("href", tmpHref);
      }

      try {
         // 필수 addon 누락 시 추가
         Iterator<String> it = essentialAddon.keySet().iterator();

         while(it.hasNext()) {
            String addonID = (String)it.next();
            Document xmlDocument = XMLParser.createDocument();
            Element xmlElement = xmlDocument.createElement("addon-descriptor");
            // 필수 addon 중 누락된 것은 새 addon-descriptor 노드를 생성해 XML에 추가
            if ((Boolean)essentialAddon.get(addonID)) {
               xmlElement.setAttribute("addonId", addonID);
               xmlElement.setAttribute("href", "../icplayer/addons/" + addonID + ".xml");
               addonDescriptor.item(0).getParentNode().appendChild(xmlElement);
            }
         }
      } catch (Exception ignored) {
      }

      // 처음에 저장한 eng.xml인 mainXML의 addons
      NodeList addons = mainXML.getElementsByTagName("addons");
      // 입력 : main.xml으로 들어온 addons
      NodeList contentAddons = contentMainXML.getElementsByTagName("addons");

      try {
         // 처음에 저장한 eng.xml인 mainXML의 <addons>노드를 입력:main.xml으로 contentMainXML의 <addons> 노드로 교체
         addons.item(0).getParentNode().replaceChild(contentAddons.item(0), addons.item(0));
         Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 04 : addonsStr[" + addons.toString() + "]");
      } catch (Exception var11) {
      }

      String xmlString = mainXML.toString();
      Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 05 : mainFetchURL[" + mainFetchURL + "]");
      Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent 06 : xmlString[" + xmlString + "]");
      this.mContent = this.produce(xmlString, mainFetchURL);
      
      // 
      Utils.consoleLog("::: ContentFactoryQNote setAddonsFromContent End ::: ");
      
   }

   // 결국 여기서 하는일은 mContent(eng.xml내용)에  main.xml에 있는 <asset>를 추가하고 있음 
   private void setAssetsFromContent(String sContentMainXML, String sContentURL) {

    // main.xml 내용과 main.xml url을 가지고 들어옮  
    Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent Start ::: ");

	try {
         // 처음에 저장한 eng.xml 콘텐츠가 들어있는 내용
         Document mainXML = XMLParser.parse(this.mContent.toXML());
         
         // 입력 : main.xml 내용이 들어가 있음
         Document contentMainXML = XMLParser.parse(sContentMainXML);
         
         NodeList assets = mainXML.getElementsByTagName("assets");
         NodeList contentAssets = contentMainXML.getElementsByTagName("asset");
         String prefixURL = sContentURL.split("/pages/")[0];
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 01 assets[" + assets.toString() + "]");
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 02 contentAssets[" + contentAssets.toString() + "]");
         Node node = assets.item(0);
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 03 newNode.getChildNodes().getLength()[" + contentAssets.getLength() + "]");
         Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 04 newNode.getChildNodes().toString()[" + contentAssets + "]");

         // 처음에 저장한 eng.xml인 mainXML의 빈<asset/>노드에 입력:main.xml으로 contentMainXML의 <asset> 내용을 추가(빈노드에 추가)
         for (int i = 0; i < contentAssets.getLength(); i++) {
             Node asset = contentAssets.item(i);
             Node importedNode = mainXML.importNode(asset, true);
             Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 05 newNode.getChildNodes().item(i) asset[" + asset + " : " + contentAssets.getLength() + "]");
             node.appendChild(importedNode);
         }

          String xmlString = mainXML.toString();

          this.mContent = this.produce(xmlString, mainFetchURL);
          // <asset>노드가 거의 끝에 있는데 로그값에 잘 들어가는것 확인함
          Utils.consoleLog("::: ContentFactoryQNote setAssetsFromContent 06 mainXML [" + mainXML.toString() + "]");
      } catch (Exception var10) {
      }

   }

   // 처음에 저장한 eng.xml인 mainXML의 <styles>노드에 입력:main.xml으로 contentMainXML의 <styles> 내용으로 교체(replace)
   private void setCSSFromContent(String sContentMainXML, String sContentURL) {

      // main.xml 내용과 main.xml url을 가지고 들어옮  
	  Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent Start ::: ");
	  
      // 처음에 저장한 eng.xml 콘텐츠가 들어있는 내용
      Document mainXML = XMLParser.parse(this.mContent.toXML());
      
      // 입력 : main.xml 내용이 들어가 있음
      Document contentMainXML = XMLParser.parse(sContentMainXML);
      
      NodeList styles = mainXML.getElementsByTagName("styles");
      NodeList contentStyles = contentMainXML.getElementsByTagName("styles");
      String prefixURL = sContentURL.split("/pages/")[0];
      Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent 01 sContentURL[" + sContentURL + "]");
      Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent 02 prefixURL[" + prefixURL + "]");
      Utils.consoleLog("::: ContentFactoryQNote setCSSFromContent 03 styles[" + styles.toString() + "]");

//      for(int i = 0; i < styles.getLength(); ++i) {
//         Node node = styles.item(i);
//         Node newNode = contentStyles.item(0);
//         node.getParentNode().replaceChild(newNode, node);
//      }
      // 처음에 저장한 eng.xml인 mainXML의 <styles>노드에 입력:main.xml으로 contentMainXML의 <styles> 내용으로 교체(replace)
      for (int i = 0; i < styles.getLength(); ++i) {
          Node oldNode = styles.item(i);
          Node newNode = contentStyles.item(0);

          if (newNode != null) {
              // 반드시 같은 Document로 가져오기
              Node importedNode = mainXML.importNode(newNode, true);
              oldNode.getParentNode().replaceChild(importedNode, oldNode);
          }
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
            if (page.getAttribute("href").equals(this.fetchUrlPages[this.loadedCount])) {
            //if (page.getAttribute("href") == this.fetchUrlPages[this.loadedCount]) {
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
      //  mContent(eng.xml내용) 내용 확인하자
      Utils.consoleLog("::: ContentFactoryQNote addPage 16 End :::");
   }

   //
   public Content produce(String xmlString, String fetchUrl) {
		//첫번째는 eng.xml
		//두번째는 main.xml
      Utils.consoleLog("::: ContentFactoryQNote produce Start :::");
      Utils.consoleLog("::: ContentFactoryQNote produce 01 fetchUrl["+fetchUrl+"] :::");
      Utils.consoleLog("::: ContentFactoryQNote produce 02 xmlString["+xmlString+"] :::");
      
      try {
         Element xml = XMLParser.parse(xmlString).getDocumentElement();
         String version = XMLUtils.getAttributeAsString(xml, "version", "1");
         Utils.consoleLog("::: ContentFactoryQNote produce 03 version[" + version+"] :::");
         
         //두번째는 main.xml이 들어올때는  Metadata parse를 수정하고 있음
         Content producedContent = (Content)((IParser)this.parsersMap.get(version)).parse(xml);
         producedContent.setBaseUrl(fetchUrl);
         
         Utils.consoleLog("::: ContentFactoryQNote produce 04 결과 ▼▼▼:::");
         Utils.consoleLog("::: ContentFactoryQNote produce 05 fetchUrl["+fetchUrl+"] :::");
         Utils.consoleLog("::: ContentFactoryQNote produce 06 producedContent["+producedContent+"] :::");
         Utils.consoleLog("::: ContentFactoryQNote produce 07 결과 ▲▲▲ End :::");
         
         
         return producedContent;
      } catch (Exception e) {
         Utils.consoleLog("::: ContentFactoryQNote produce 08 produce xmlString[" + xmlString+"] :::");
         Utils.consoleLog("::: ContentFactoryQNote produce 09 produce fetchUrl[" + fetchUrl+"] :::");
         Utils.consoleLog("::: ContentFactoryQNote produce 10 produce e[" + e +"] :::");
         Utils.consoleLog("::: ContentFactoryQNote produce 11 End :::");
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
