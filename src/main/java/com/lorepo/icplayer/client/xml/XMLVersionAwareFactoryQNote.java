// Source code is decompiled from a .class file using FernFlower decompiler.
package com.lorepo.icplayer.client.xml;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import com.lorepo.icf.utils.JavaScriptUtils;
import com.lorepo.icf.utils.XMLUtils;
import com.lorepo.icplayer.client.utils.RequestsUtils;
import com.lorepo.icplayer.client.utils.Utils;
import java.util.HashMap;

public abstract class XMLVersionAwareFactoryQNote implements IXMLFactory {
   protected HashMap<String, IParser> parsersMap = new HashMap<String, IParser>();
   protected int pagesCount = 1;
   protected int loadedCount = 0;
   protected String[] fetchUrls;
   protected String[] fetchUrlPages;
   protected IProducingLoadingListener listener;

   public XMLVersionAwareFactoryQNote() {
   }

   protected void addParser(IParser parser) {
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote addParser Start 생성자 :::");

      this.parsersMap.put(parser.getVersion(), parser);
   }

   public void load(String fetchUrl, IProducingLoadingListener listener) {
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote load Start :::");
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote load 01 fetchUrs[" + fetchUrl + "] :::");
      this.loadedCount = 0;
      this.listener = listener;
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote load 02 fetchUrl[" + fetchUrl + "] :::");
      this.fetchUrls = Utils.getUrls(fetchUrl);
      this.fetchUrlPages = Utils.getUrlPages(fetchUrl);
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote load 03 fetchUrlPages[" + this.fetchUrlPages + "] :::");
      this.pagesCount = this.fetchUrls.length;
      
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote load 04 fetchUrls[" + this.loadedCount+"][" + fetchUrls[this.loadedCount] + "] :::");
      this.send(this.fetchUrls[this.loadedCount], listener);
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote load End :::");
   }

   public void unload() {
   }

   protected void send(String fetchUrl, IProducingLoadingListener listener) {
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote send Start :::");
      try {
         Utils.consoleLog("::: XMLVersionAwareFactoryQNote send 01 fetchUrl(send)[" + fetchUrl + "] :::");
         RequestsUtils.get(fetchUrl, this.getContentLoadCallback(listener));
      } catch (RequestException e) {
         JavaScriptUtils.log("::: XMLVersionAwareFactoryQNote send 02 Fetching main data content from server has failed: " + e.toString());
      }
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote load End :::");
   }

   //::: Restored by DF: 수정 ::: ▼▼▼ 
   protected RequestFinishedCallback getContentLoadCallback(final IProducingLoadingListener listener) {

       return new RequestFinishedCallback() {
           @Override
           public void onResponseReceived(String fetchURL, Request request, Response response) {
               Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived Start :::");
               Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived 01 : fetchURL[" + fetchURL + "] StatusCode[" + response.getStatusCode() + "]");
               
               if (response.getStatusCode() != 200 && response.getStatusCode() != 0) {
                  Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived 02 : Wrong status: getText[" + response.getText() + "]");
                  listener.onError("Wrong status: " + response.getText());
               } else {
                   Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived 03 : loadedCount[" + fetchUrls[loadedCount] + "]");
                   Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived 04 : getContentLoadCallback [" + response.getText() + "]");
                  Object producedItem = produce(response.getText(), fetchURL);
                  if (loadedCount + 1 < pagesCount) {
                     ++loadedCount;
                     
                     Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived 05 : send loadedCount[" + fetchUrls[loadedCount] + "]");
                     send(fetchUrls[loadedCount], listener);
                  } else {
                     Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived 06 : end loadedCount[" + loadedCount + "] pagesCount[" + pagesCount + "]");
                     listener.onFinishedLoading(producedItem);
                     ++loadedCount;
                  }
               }
               Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived Start :::");
            }
           
            @Override
            public void onError(Request request, Throwable exception) {
               Utils.consoleLog("::: XMLVersionAwareFactoryQNote getContentLoadCallback onResponseReceived onError : " + exception);
               listener.onFinishedLoading((Object)null);
            }
       };
   }
   //::: Restored by DF: 수정 ::: ▲▲▲

   public Object produce(String xmlString, String fetchUrl) {
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote produce Start xmlString[" + xmlString + "]:::");
      
      if (!Utils.isQNote && xmlString.indexOf("Completion_Progress1") < 0) {
         xmlString = this.addCompletionProgress1(xmlString);
         Utils.consoleLog("::: XMLVersionAwareFactoryQNote produce 01 produce[" + xmlString + "]");
      }

      if (Utils.isQNote) {
         xmlString = this.addIntroTTSText(xmlString);
         Utils.consoleLog("::: XMLVersionAwareFactoryQNote produce 02 produce[" + xmlString + "]");
      }

      Element xml = XMLParser.parse(xmlString).getDocumentElement();
      String version = XMLUtils.getAttributeAsString(xml, "version", "1");
      
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote produce 03 version[" + version + "]");
      
      Object producedContent = ((IParser)this.parsersMap.get(version)).parse(xml);
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote produce End :::");
      return producedContent;
   }

   private String addIntroTTSText(String xmlString) {
       Utils.consoleLog("::: XMLVersionAwareFactoryQNote addIntroTTSText Start :::");
      try {
         String[] sss = xmlString.split("<modules>");
         return sss[0] + "<modules><textModule id=\"Text99\" isTabindexEnabled=\"true\" shouldOmitInKeyboardNavigation=\"false\" shouldOmitInTTS=\"true\" ttsTitle=\"\">\n<styles>\n<inlineStyles>\n<inlineStyle value=\"opacity:0;\" layoutID=\"default\"/>\n</inlineStyles>\n</styles>\n<layouts isVisible=\"true\">\n<layout isLocked=\"false\" isModuleVisibleInEditor=\"true\" id=\"default\">\n<relative type=\"LTWH\">\n<left relative=\"\" property=\"left\"/>\n<top relative=\"\" property=\"top\"/>\n<right relative=\"\" property=\"right\"/>\n<bottom relative=\"\" property=\"bottom\"/>\n</relative>\n<absolute left=\"530\" right=\"0\" top=\"0\" bottom=\"0\" width=\"314\" height=\"28\"/>\n</layout>\n<layout isLocked=\"false\" isModuleVisibleInEditor=\"true\" id=\"995FC857-844A-450E-983A-00ADDE32873C\">\n<relative type=\"LTWH\">\n<left relative=\"\" property=\"left\"/>\n<top relative=\"\" property=\"top\"/>\n<right relative=\"\" property=\"right\"/>\n<bottom relative=\"\" property=\"bottom\"/>\n</relative>\n<absolute left=\"190\" right=\"0\" top=\"0\" bottom=\"0\" width=\"357\" height=\"30\"/>\n</layout>\n</layouts>\n<text draggable=\"false\" math=\"false\" gapMaxLength=\"0\" gapWidth=\"80\" isActivity=\"true\" isIgnorePunctuation=\"false\" isKeepOriginalOrder=\"false\" isClearPlaceholderOnFocus=\"false\" isDisabled=\"false\" isSection=\"false\" isSplitInPrintBlocked=\"false\" ignoreDefaultPlaceholderWhenCheck=\"false\" isCaseSensitive=\"false\" useNumericKeyboard=\"false\" openLinksinNewTab=\"true\" blockWrongAnswers=\"false\" userActionEvents=\"false\" useEscapeCharacterInGap=\"false\" allAnswersGapSizeCalculationStyle=\"true\" valueType=\"All\" printable=\"No\" number=\"\" gap=\"\" dropdown=\"\" correct=\"\" wrong=\"\" empty=\"\" insert=\"\" removed=\"\" link=\"\"><![CDATA[ <span data-sheets-root=\"1\" data-sheets-value=\"{&quot;1&quot;:2,&quot;2&quot;:&quot;\\\\alt{Ctrl 와 Enter를 눌러 TTS를 활성화하세요|Ctrl 와 Enter를 눌러 TTS를 활성화하세요}[lang ko-KR]&quot;}\" data-sheets-userformat=\"{&quot;2&quot;:4737,&quot;3&quot;:{&quot;1&quot;:0},&quot;10&quot;:1,&quot;12&quot;:0,&quot;15&quot;:&quot;Arial&quot;}\" data-sheets-formula=\"=R7C6&amp;R[0]C[-1]&amp;&quot;|&quot;&amp;R[0]C[-1]&amp;R8C6\" style=\"font-size: 10pt; font-family: Arial;\">\\alt{Ctrl 와 Enter를 눌러 TTS를 활성화하세요|Ctrl 와 Enter를 눌러 TTS를 활성화하세요}[lang ko-KR]</span> ]]></text>\n</textModule>" + sss[1];
      } catch (Error e) {
          Utils.consoleLog("::: XMLVersionAwareFactoryQNote addIntroTTSText Error End:::");
         return xmlString;
      }
   }

   private String addCompletionProgress1(String xmlString) {
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote addCompletionProgress1 Start :::");
      try {
         String[] sss = xmlString.split("</modules>");
         return sss[0] + "<addonModule addonId=\"Completion_Progress\" id=\"Completion_Progress1\" isVisible=\"false\" isLocked=\"false\" isModuleVisibleInEditor=\"false\">\n\t\t\t<layouts>\n\t\t\t\t<layout isLocked=\"false\" isModuleVisibleInEditor=\"true\" id=\"default\" isVisible=\"false\">\n\t\t\t\t\t<relative type=\"LTWH\">\n\t\t\t\t\t\t<left relative=\"\" property=\"left\"/>\n\t\t\t\t\t\t<top relative=\"\" property=\"top\"/>\n\t\t\t\t\t\t<right relative=\"\" property=\"right\"/>\n\t\t\t\t\t\t<bottom relative=\"\" property=\"bottom\"/>\n\t\t\t\t\t</relative>\n\t\t\t\t\t<absolute left=\"0\" right=\"0\" top=\"-70\" bottom=\"0\" width=\"45\" height=\"45\" rotate=\"0\"/>\n\t\t\t\t</layout>\n\t\t\t\t<layout isLocked=\"false\" isModuleVisibleInEditor=\"true\" id=\"vertical\" isVisible=\"false\">\n\t\t\t\t\t<relative type=\"LTWH\">\n\t\t\t\t\t\t<left relative=\"\" property=\"left\"/>\n\t\t\t\t\t\t<top relative=\"\" property=\"top\"/>\n\t\t\t\t\t\t<right relative=\"\" property=\"right\"/>\n\t\t\t\t\t\t<bottom relative=\"\" property=\"bottom\"/>\n\t\t\t\t\t</relative>\n\t\t\t\t\t<absolute left=\"0\" right=\"0\" top=\"-70\" bottom=\"0\" width=\"45\" height=\"45\" rotate=\"0\"/>\n\t\t\t\t</layout>\n\t\t\t</layouts>\n\t\t\t<properties>\n\t\t\t\t<property name=\"Turn off automatic counting\" type=\"boolean\" value=\"true\"/>\n\t\t\t</properties>\n\t\t</addonModule></modules>" + sss[1];
      } catch (Error e) {
          Utils.consoleLog("::: XMLVersionAwareFactoryQNote addCompletionProgress1 Error :::");
         return xmlString;
      }
   }

   private String addResetButton1(String xmlString) {
      Utils.consoleLog("::: XMLVersionAwareFactoryQNote addResetButton1 Start :::");
      try {
         String[] sss = xmlString.split("</modules>");
         return sss[0] + "<buttonModule id=\"ResetButton1\" isTabindexEnabled=\"false\">\n\t\t\t<layouts>\n\t\t\t\t<layout isLocked=\"false\" isModuleVisibleInEditor=\"false\" id=\"default\" isVisible=\"false\">\n\t\t\t\t\t<relative type=\"LTWH\">\n\t\t\t\t\t\t<left relative=\"\" property=\"left\"/>\n\t\t\t\t\t\t<top relative=\"\" property=\"top\"/>\n\t\t\t\t\t\t<right relative=\"\" property=\"right\"/>\n\t\t\t\t\t\t<bottom relative=\"\" property=\"bottom\"/>\n\t\t\t\t\t</relative>\n\t\t\t\t\t<absolute left=\"0\" right=\"0\" top=\"0\" bottom=\"0\" width=\"1\" height=\"0\" rotate=\"0\"/>\n\t\t\t\t</layout>\n\t\t\t\t<layout isLocked=\"false\" isModuleVisibleInEditor=\"false\" id=\"vertical\" isVisible=\"false\">\n\t\t\t\t\t<relative type=\"LTWH\">\n\t\t\t\t\t\t<left relative=\"\" property=\"left\"/>\n\t\t\t\t\t\t<top relative=\"\" property=\"top\"/>\n\t\t\t\t\t\t<right relative=\"\" property=\"right\"/>\n\t\t\t\t\t\t<bottom relative=\"\" property=\"bottom\"/>\n\t\t\t\t\t</relative>\n\t\t\t\t\t<absolute left=\"0\" right=\"0\" top=\"0\" bottom=\"0\" width=\"1\" height=\"0\" rotate=\"0\"/>\n\t\t\t\t</layout>\n\t\t\t</layouts>\n\t\t\t<button onclick=\"\" type=\"reset\" text=\"\" confirmReset=\"false\" confirmInfo=\"\" confirmYesInfo=\"\" confirmNoInfo=\"\"/>\n\t\t</buttonModule></modules>" + sss[1];
      } catch (Error var3) {
          Utils.consoleLog("::: XMLVersionAwareFactoryQNote addResetButton1 Error :::");
         return xmlString;
      }
   }
}
