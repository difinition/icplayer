// Source code is decompiled from a .class file using FernFlower decompiler.
package com.lorepo.icplayer.client.utils;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.lorepo.icplayer.client.module.api.player.IPlayerServices;
import com.lorepo.icplayer.client.module.api.player.IScoreService;
import com.lorepo.icplayer.client.module.text.GapInfo;
import java.util.HashMap;
import java.util.Iterator;

public class Utils {
   public static String delemiter = "!@#";
   private static int moduleCnt = 0;
   private static String userAgent = "";
   public static boolean isCrossWalkWebview = true;
   public static boolean isSafari = false;
   public static String[] exceptFonts = new String[]{"VAGRounded BT", "VAGRoundedBT"};
   public static boolean isExistsExceptFont = false;
   public static double fontSize = 0.0;
   public static String xmlBRReplacer = "[xmlBRReplacer]";
   public static String imgBRReplacer = "[xmlImgReplacer]";
   public static String xmPReplacer = ">[xmlPReplacer]</p>";
   public static boolean isQNote = true;
   public static boolean isLoadSeperate = true;
   
   // load2에서 호출하는 7번째 변수 isLoad2Val7
   public static boolean isLoad2Val7 = false;
   
   public static String baseURL = "";
   public static String tts_number = "숫자 입력";
   public static String tts_gap = "단답형 입력";
   public static String tts_dropdown = "드랍다운 선택";
   public static String tts_correct = "옳은";
   public static String tts_wrong = "잘못된";
   public static String tts_empty = "비어 있는";
   public static String tts_insert = "삽입됨";
   public static String tts_removed = "제거됨";
   public static String tts_link = "링크";
   public static String tts_selected = "선택";
   public static String tts_deselected = "해제";
   public static String tts_incorrect = "오답";
   public static int minMaxlength = 7;
   public static String currentPageHref = "";
   public static String prevPageHref = "";

   public Utils() {
   }

   public static native void consoleLog(String strLog)/*-{
     console.log(strLog);
   }-*/;

   public static native String getAppName()/*-{
     return ($wnd.navigator && $wnd.navigator.userAgent) ? $wnd.navigator.userAgent : "unknown";
   }-*/;
   
//   public static void consoleLog(String var0) { };
//   public static String getAppName() {
//	   return "test mode";
//   };
   
   public static String[] getUrls(String url) {

       consoleLog("::: Utils.java getUrls 패치URL취득시 호출됨 Start  ::: ");
	   
      try {
         consoleLog("::: Utils.java getUrls 01 url[" + url + "] :::");
         JSONValue parsed = JSONParser.parseStrict(url);
         JSONArray jsonArray = parsed.isArray();
         String[] arr = new String[jsonArray.size()];

         for(int i = 0; i < arr.length; ++i) {
            try {
               arr[i] = jsonArray.get(i).toString().replace("\"", "");
               if (arr[i].indexOf("/pages/") > -1) {
                  if (arr[i].split("pages/")[1].contains("token=")) {
                     arr[i] = arr[i].split("pages/")[0] + "pages/main.xml?token=" + arr[i].split("pages/")[1].split("token=")[1];
                  } else {
                     arr[i] = arr[i].split("pages/")[0] + "pages/main.xml";
                  }
               }

               consoleLog("::: Utils.java getUrls 02 arr["+i+"]:[" + arr[i] + "] :::");
            } catch (Exception e) {
               consoleLog("::: Utils.java getUrls 03 e : " + e);
            }
         }

         consoleLog("::: Utils.java getUrls 04 arr.length[" + arr.length + "] :::");
         return arr;
      } catch (Exception var7) {
         return new String[]{url};
      }
   }

   public static String[] getUrlPages(String url) {
       consoleLog("::: Utils.java  getUrlPages Start :::");
       
      try {
         consoleLog("::: Utils.java getUrlPages 01 url[" + url + "] :::");
         JSONValue parsed = JSONParser.parseStrict(url);
         JSONArray jsonArray = parsed.isArray();
         String[] arr = new String[jsonArray.size()];

         for(int i = 0; i < arr.length; ++i) {
            try {
               arr[i] = jsonArray.get(i).toString().replace("\"", "");
               if (arr[i].indexOf("/pages/") > -1) {
                  arr[i] = arr[i].split("/pages/")[1];
                  arr[i] = arr[i].split(".xml?")[0] + ".xml";
               }

               consoleLog("::: Utils.java getUrlPages 02 arr["+i+"]:[" + arr[i] + "] :::");
            } catch (Exception e) {
               consoleLog("::: Utils.java getUrlPages 03 e : " + e);
            }
         }

         consoleLog("★★★★★ ::: Utils.java  getUrlPages : arr[" + arr + "] :::");
         return arr;
      } catch (Exception var7) {
         return new String[]{url};
      }
   }

   public static int getUrlCount(String url) {
       consoleLog("::: Utils.java getUrlCount : [" + url + "] :::");
       
      return url.split(delemiter).length;
   }

   public static String addUrl(String url, String addUrl) {
       consoleLog("::: Utils.java addUrl : addUrl[" + addUrl + "] :::");
       
      return addUrl + delemiter + url;
   }

   public static String getPath(String url) {
      consoleLog("::: Utils.java getPath : url[" + url + "] :::");

      String[] urls = url.split("/");
      if (urls.length > 1) {
         String retValue = "";

         for(int i = 0; i < urls.length - 1; ++i) {
            if (!urls[i].equals("..")) {
               retValue = retValue + urls[i] + "/";
            }
         }

         consoleLog("::: Utils.java getPath 01 : retValue url[" + url + "] :::");
         return retValue;
      } else {
          consoleLog("::: Utils.java getPath 02 : retValue url[" + url + "] :::");
         return url;
      }
   }

   public static String getFileName(String url) {
      consoleLog("::: Utils.java getFileName : url[" + url + "] :::");

      String[] urls = url.split("/");
      if (urls.length > 1) {
         String retValue = "";

         for(int i = 0; i < urls.length - 1; ++i) {
            if (!urls[i].equals("..")) {
               retValue = retValue + urls[i] + "/";
            }
         }

         return urls[urls.length - 1];
      } else {
         return url;
      }
   }

   public static String format(String format, String... args) {
      
      String[] split = format.split("%s");
      consoleLog("split :" + split.length);
      StringBuffer msg = new StringBuffer();

      for(int pos = 0; pos < split.length - 1; ++pos) {
         consoleLog("pos : " + pos);
         consoleLog("split[pos] : " + split[pos]);
         consoleLog("args[pos] : " + args[pos]);
         msg.append(split[pos]);
         msg.append(args[pos]);
      }

      msg.append(split[split.length - 1]);
      
      consoleLog("::: Utils.java  format : msg.toString()[" + msg.toString() + "] :::");
      
      return msg.toString();
   }

   public static String getModuleID() {
      
      ++moduleCnt;
      consoleLog("::: Utils.java  getModuleID : ModuleID[" + "value_" + moduleCnt  + "] :::");
      
      return "value_" + moduleCnt;
   }

   public static int getNumericText(String text) {
      String numericText = "";
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < text.length(); ++i) {
         char c = text.charAt(i);
         if (GapInfo.isDigit(c)) {
            sb.append(c);
         }
      }

      numericText = sb.toString();
      return Integer.parseInt(numericText);
   }

   public static String getPageGroupID(int currentPageIdx, String groupID) {
	      consoleLog("::: Utils.java  getPageGroupID : currentPageIdx[" + currentPageIdx + "] groupID[" + groupID + "]:::");
      
      String id = currentPageIdx + "_" + groupID;
      consoleLog("::: Utils.java getPageGroupID : " + id);
      return id;
   }

   public static boolean checkSameAnswerInGroup(IPlayerServices playerService, String moduleID, String enteredValue) {
      consoleLog("::: Utils.java  checkSameAnswerInGroup : moduleID[" + moduleID + "] enteredValue[" + enteredValue + "]:::");
      
      try {
         IScoreService scoreService = playerService.getScoreService();
         String groupID = (String)scoreService.getTextGroupID().get(moduleID);
         consoleLog("::: Utils.java checkSameAnswerInGroup groupID : " + groupID);
         if (groupID != null && groupID.length() != 0) {
            HashMap<String, String> modules = (HashMap)scoreService.getGroupTexts().get(groupID);
            if (modules == null) {
               return false;
            } else {
               consoleLog("::: Utils.java checkSameAnswerInGroup modules : " + modules);
               Iterator var6 = modules.keySet().iterator();

               while(var6.hasNext()) {
                  String key = (String)var6.next();
                  consoleLog("::: Utils.java checkSameAnswerInGroup key : " + key + ", moduleID : " + moduleID);
                  if (getNumericText(key) < getNumericText(moduleID)) {
                     consoleLog("::: Utils.java checkSameAnswerInGroup key numeric : " + getNumericText(key) + ", Utils.getNumericText(moduleID) : " + getNumericText(moduleID));
                     consoleLog("::: Utils.java checkSameAnswerInGroup modules.get(key) : " + (String)modules.get(key) + ", moduleID : " + enteredValue);
                     if (((String)modules.get(key)).equals(enteredValue)) {
                        return true;
                     }
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      } catch (Exception var8) {
         return false;
      }
   }

   public static String removeTag(String value) {
      try {
         HTML html = new HTML(value);
         value = html.getText();
      } catch (Exception var2) {
      }

      consoleLog("::: Utils.java  removeTag : value[" + value + "] :::");
      
      return value;
   }

	public static boolean isCrossWalkWebView() {
      
		if (userAgent.equals("")) {
			userAgent = getAppName();
			if (userAgent == null) {
				userAgent = "";
			}
			isCrossWalkWebview = userAgent.contains("Crosswalk");
		}

		consoleLog("::: Utils.java  isCrossWalkWebView : isCrossWalkWebview[" + isCrossWalkWebview + "] :::");

		return isCrossWalkWebview;
	}

	public static boolean isSafari() {

		if (userAgent.equals("")) {
			userAgent = getAppName();
			if (userAgent == null) {
				userAgent = "";
			}
			consoleLog("::: Utils.java isSafari userAgent : " + userAgent);
			isSafari = userAgent.contains("Safari") && !userAgent.contains("Chrome");
		}

		return isSafari;
	}

   private static String convertDocumentToString(Element doc) {
      consoleLog("::: Utils.java convertDocumentToString : doc[" + doc + "] :::");
      
      String str = doc.toString();
      consoleLog("::: Utils.java convertDocumentToString before : " + str);
      str = str.replace("&lt;", "<");
      str = str.replace("&gt;", ">");
      str = str.replace("&amp;", "&");
      str = str.replace(xmlBRReplacer, "<br>");
      str = str.replace(xmPReplacer, "></p>");
      str = str.replaceAll("<ul[^>]*>/ul>", "");
      str = str.replaceAll("<ul[^>]*>*/>", "");
      consoleLog("::: Utils.java convertDocumentToString after : " + str);
      return str;
   }

   private static Element convertStringToDocument(String xmlString) {
      consoleLog("::: Utils.java  convertStringToDocument : xmlString[" + xmlString + "] :::");
      
      Element xml = null;

      try {
         consoleLog("::: Utils.java xmlString begin : " + xmlString);
         xmlString = xmlString.replaceAll("<br>", xmlBRReplacer);
         xmlString = xmlString.replaceAll("<br([^>]+)>", xmlBRReplacer);
         xmlString = xmlString.replaceAll("&nbsp;", "&#160;");
         xmlString = xmlString.replaceAll("></p>", xmPReplacer);
         consoleLog("::: Utils.java xmlString after : " + xmlString);
         xml = XMLParser.parse(xmlString).getDocumentElement();
      } catch (Exception var3) {
      }

      return xml;
   }

   public static void hasExceptFont(Node node, boolean isKeepfontSize) {
      consoleLog("::: Utils.java  hasExceptFont : node[" + node + "] isKeepfontSize[" + isKeepfontSize + "]:::");
      
      isExistsExceptFont = false;
      consoleLog("hasExceptFont node : " + node);
      if (node != null) {
         consoleLog("::: Utils.java hasExceptFont getNodeType : " + node.getNodeType());
         if (node.getNodeType() != 1) {
            hasExceptFont(node.getParentNode(), fontSize != 0.0);
         } else {
            try {
               Element element = (Element)node;
               String style = "";
               String font = "";
               String dataFontSrc = "";
               if (element.hasAttribute("style")) {
                  style = element.getAttribute("style");
                  consoleLog("::: Utils.java hasExceptFont style : " + style);
                  String sfSize = "";

                  try {
                     if (style.indexOf("font-size:") > -1) {
                        sfSize = style.split("font-size:")[1];
                     }
                  } catch (Exception var8) {
                  }

                  consoleLog("::: Utils.java hasExceptFont sfSize : " + sfSize);
                  consoleLog("::: Utils.java hasExceptFont fontSize : " + fontSize);
               }

               if (element.hasAttribute("face")) {
                  font = element.getAttribute("face");
               }

               consoleLog("::: Utils.java hasExceptFont style : " + style);
               consoleLog("::: Utils.java hasExceptFont face : " + font);
               consoleLog("::: Utils.java hasExceptFont dataFontSrc : " + dataFontSrc);
               int i = 0;

               while(true) {
                  if (i >= exceptFonts.length) {
                     if ((style == "" || style.indexOf("font-family") < 0) && font == "" && dataFontSrc == "") {
                        consoleLog("::: Utils.java hasExceptFont element : " + element);
                        consoleLog("::: Utils.java hasExceptFont getParentNode : " + element.getParentNode());
                        hasExceptFont(element.getParentNode(), fontSize != 0.0);
                     }
                     break;
                  }

                  if (style.indexOf(exceptFonts[i]) > -1 || font.indexOf(exceptFonts[i]) > -1 || dataFontSrc.indexOf(exceptFonts[i]) > -1) {
                     isExistsExceptFont = true;
                     return;
                  }

                  ++i;
               }
            } catch (Exception var9) {
               consoleLog("::: Utils.java hasExceptFont e : " + var9);
               consoleLog("::: Utils.java hasExceptFont e getParentNode  : " + node.getParentNode());
               if (node.getParentNode() != null) {
                  hasExceptFont(node.getParentNode(), fontSize != 0.0);
               }
            }

            consoleLog("::: Utils.java hasExceptFont : " + isExistsExceptFont);
         }
      }
   }

   public static void getFontSize(Node node) {
      consoleLog("::: Utils.java  getFontSize : node[" + node + "] :::");
      
      if (node != null) {
         if (node.getNodeType() != 1) {
            getFontSize(node.getParentNode());
         } else {
            try {
               Element element = (Element)node;
               String style = "";
               String font = "";
               String dataFontSrc = "";
               if (element.hasAttribute("style")) {
                  style = element.getAttribute("style");
                  consoleLog("::: Utils.java getFontSize style : " + style);
                  String sfSize = "";

                  try {
                     if (style.indexOf("font-size:") > -1) {
                        sfSize = style.split("font-size:")[1];
                     }
                  } catch (Exception var7) {
                  }

                  consoleLog("::: Utils.java getFontSize sfSize : " + sfSize);
                  if (sfSize.length() > 0) {
                     fontSize = (double)Integer.parseInt(sfSize.split("px;")[0].trim());
                  }

                  consoleLog("::: Utils.java getFontSize fontSize : " + fontSize);
               }

               if (element.hasAttribute("face")) {
                  font = element.getAttribute("face");
               }

               consoleLog("::: Utils.java getFontSize style : " + style);
               consoleLog("::: Utils.java getFontSize face : " + font);
               if (fontSize == 0.0 && node.getParentNode() != null) {
                  consoleLog("::: Utils.java hasExceptFont element : " + element);
                  consoleLog("::: Utils.java hasExceptFont getParentNode : " + element.getParentNode());
                  getFontSize(element.getParentNode());
               }
            } catch (Exception var8) {
               consoleLog("::: Utils.java hasExceptFont e : " + var8);
               consoleLog("::: Utils.java hasExceptFont e getParentNode  : " + node.getParentNode());
               if (fontSize == 0.0 && node.getParentNode() != null) {
                  getFontSize(node.getParentNode());
               }
            }

            consoleLog("::: Utils.java hasExceptFont : " + isExistsExceptFont);
         }
      }
   }

   public static void replaceNBSP(Node cNode) {
      consoleLog("::: Utils.java  replaceNBSP : cNode[" + cNode + "] :::");

      consoleLog("::: Utils.java replaceNBSP ---------------------------------");
      isExistsExceptFont = false;
      fontSize = 0.0;
      getFontSize(cNode);
      hasExceptFont(cNode, false);
      NodeList nodes = cNode.getChildNodes();
      String parseText = "";

      for(int i = 0; i < nodes.getLength(); ++i) {
         Node currentNode = nodes.item(i);

         try {
            if (currentNode.getNodeType() != 1) {
               consoleLog("::: Utils.java replaceNBSP ---------------------------------------------------------------------------------------------");
               fontSize = 0.0;
               getFontSize(cNode);
               hasExceptFont(currentNode, false);
               consoleLog("::: Utils.java replaceNBSP index : " + i + " : " + isExistsExceptFont + " : " + currentNode.getNodeValue());
               double spaceSize = (double)Math.round(fontSize * 100.0 / 3.0) / 100.0;
               if (isExistsExceptFont) {
                  consoleLog("::: Utils.java replaceNBSP before : " + currentNode.getNodeValue());
                  consoleLog("::: Utils.java replaceNBSP fontSize : " + fontSize);
                  parseText = currentNode.getNodeValue().replaceAll(" ", "<span style=\"word-spacing: " + spaceSize + "px;\">&nbsp;</span>");
                  consoleLog("::: Utils.java replaceNBSP after : " + parseText);
               } else {
                  parseText = currentNode.getNodeValue().replaceAll(" ", "&nbsp;");
               }

               currentNode.setNodeValue(parseText);
            }
         } catch (Exception var7) {
         }
      }

   }

   public static void doSomething(Element element, boolean isStart) {
	      consoleLog("::: Utils.java  doSomething : element[" + element + "] isStart[" + isStart + "] :::");
	      
      NodeList nodes = element.getChildNodes();
      if (isStart) {
         replaceNBSP(element);
         if (element.getNodeType() == 1) {
            doSomething((Element)element, false);
         }
      } else {
         for(int i = 0; i < nodes.getLength(); ++i) {
            Node currentNode = nodes.item(i);
            replaceNBSP(currentNode);
            if (currentNode.getNodeType() == 1) {
               doSomething((Element)currentNode, false);
            }
         }
      }

   }

   public static String parseNBSP(String str) {
      consoleLog("::: Utils.java parseNBSP : str[" + str + "] :::");

      if (!isSafari()) {
         return str;
      } else {
    	  consoleLog("::: Utils.java parseNBSP isSafari : " + isSafari());
         for(int i = 0; i < exceptFonts.length; ++i) {
            consoleLog("exceptFonts : " + str.contains(exceptFonts[i]));
            if (str.contains(exceptFonts[i])) {
               isExistsExceptFont = false;
               fontSize = 0.0;
               Element element = convertStringToDocument(str);
               consoleLog("parseNBSP: " + element);
               doSomething(element, true);
               str = convertDocumentToString(element);
               break;
            }
         }

         return str;
      }
   }

   public static String addWordSpacing(String str) {
      consoleLog("::: Utils.java  addWordSpacing : str[" + str + "] :::");

      if (!isSafari()) {
         return str;
      } else {
         String[] fonts = new String[]{"font-family:VAGRounded BT;"};

         for(int i = 0; i < fonts.length; ++i) {
            if (str.contains(fonts[i])) {
               try {
                  String[] arrText = str.split("font-size:");
                  String result = arrText[0];

                  for(int j = 1; j < arrText.length; ++j) {
                     int fSize = Integer.parseInt(arrText[j].split("px;")[0].trim());
                     double fontSize = (double)Math.round((double)(fSize * 10) / 3.08) / 10.0;
                     String aaa = arrText[j];
                     aaa = aaa.replaceAll("&nbsp;", "<span style=\"word-spacing: " + fontSize + "px;\">&nbsp;</span>");
                     aaa = aaa.replaceAll("> <", ">&nbsp;<");
                     arrText[j] = aaa;
                     result = result + "font-size:" + aaa;
                  }

                  str = result;
               } catch (Exception var10) {
               }

               consoleLog("::: Utils.java  addWordSpacing str[" + str + "] :::");
            }
         }

         return str;
      }
   }

   public static String fromUnicode(String unicode) {
      consoleLog("::: Utils.java  fromUnicode : unicode[" + unicode + "] :::");
      
      String str = unicode.replace("\\", "");
      String[] arr = str.split("u");
      StringBuffer text = new StringBuffer();

      for(int i = 1; i < arr.length; ++i) {
         int hexVal = Integer.parseInt(arr[i], 16);
         text.append(Character.toChars(hexVal));
      }

      consoleLog("::: Utils.java  fromUnicode : text[" + text.toString() + "] :::");
      return text.toString();
   }

   public static String toUnicode(String text) {
      consoleLog("::: Utils.java  toUnicode : text[" + text + "] :::");
      
      StringBuffer sb = new StringBuffer();

      for(int i = 0; i < text.length(); ++i) {
         int codePoint = text.codePointAt(i);
         if (codePoint > 65535) {
            ++i;
         }

         String hex = Integer.toHexString(codePoint);
         sb.append("\\u");

         for(int j = 0; j < 4 - hex.length(); ++j) {
            sb.append("0");
         }

         sb.append(hex);
      }

      consoleLog("::: Utils.java  toUnicode : sb[" + sb.toString() + "] :::");
      return sb.toString();
   }

   public static native int getCalculatedGapWidthQNote(String strId)/*-{
      
   	 console.log("::: Utils.java  getCalculatedGapWidthQNote : strId[" + strId + "] :::");
   	 
    var elem = $doc.getElementById(strId);
    if (elem) {
        // SVG 요소일 경우
        
        if (elem.getBBox) {
            return Math.round(elem.getBBox().width);
        }
        // 일반 HTML 요소일 경우
        if (elem.getBoundingClientRect) {
            return Math.round(elem.getBoundingClientRect().width);
        }
    }
    return 0;
   }-*/;
}
