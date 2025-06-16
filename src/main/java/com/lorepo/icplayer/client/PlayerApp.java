package com.lorepo.icplayer.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.RootPanel;
import com.lorepo.icf.utils.ExtendedRequestBuilder;
import com.lorepo.icf.utils.ILoadListener;
import com.lorepo.icf.utils.JSONUtils;
import com.lorepo.icf.utils.JavaScriptUtils;
import com.lorepo.icf.utils.URLUtils;
import com.lorepo.icf.utils.dom.DOMInjector;
import com.lorepo.icplayer.client.metadata.ScoreWithMetadata;
import com.lorepo.icplayer.client.model.Content;
import com.lorepo.icplayer.client.model.CssStyle;
import com.lorepo.icplayer.client.model.asset.ScriptAsset;
import com.lorepo.icplayer.client.model.page.Page;
import com.lorepo.icplayer.client.module.api.player.IPage;
import com.lorepo.icplayer.client.module.api.player.IPlayerServices;
import com.lorepo.icplayer.client.module.api.player.IScoreService;
import com.lorepo.icplayer.client.module.api.player.PageOpenActivitiesScore;
import com.lorepo.icplayer.client.printable.PrintableContentParser;
import com.lorepo.icplayer.client.printable.PrintableOrderParser;
import com.lorepo.icplayer.client.printable.PrintableParams;
import com.lorepo.icplayer.client.ui.PlayerView;
import com.lorepo.icplayer.client.utils.Utils;
import com.lorepo.icplayer.client.xml.IProducingLoadingListener;
import com.lorepo.icplayer.client.xml.IXMLFactory;
import com.lorepo.icplayer.client.xml.content.ContentFactory;
import com.lorepo.icplayer.client.xml.content.ContentFactoryQNote;
import com.lorepo.icplayer.client.xml.page.PageFactory;
import com.lorepo.icplayer.client.xml.page.PageFactoryQNote;
import com.lorepo.icplayer.client.printable.PrintableOrderParser;

public class PlayerApp {

	private String divId;
	private Content contentModel;
	private PlayerController playerController;
	private PlayerConfig playerConfig = new PlayerConfig();
	/** Score service impl */
	private PlayerEntryPoint entryPoint;
	private int startPageIndex = 0;
	private HashMap<String, String> loadedState;
	private HashMap<String, PageOpenActivitiesScore> pagesOpenActivitiesScores;
	private boolean bookMode = false;
	private boolean showCover = false;
	private String analyticsId = null;
	private ArrayList<Integer> pagesSubset = null;
	private boolean isStaticHeader = false;
	private static boolean isAnimationRunning = false;
	private String lastSentLayoutID = "";
	private boolean isContentModelLoaded = false;
	private static JavaScriptObject iframeHandlers = null;
	private HashMap<String, List<String>> printableOrder = new HashMap<String, List<String>>();
	private PrintableContentParser printableParser;

	//::: Restored by DF: 커스텀 추가  ::: contentModels 필드 추가됨 
	private ArrayList<Content> contentModels;
	//::: Restored by DF: 커스텀 추가  ::: areStaticScaledElementsFixed 필드 추가됨 
	private static boolean areStaticScaledElementsFixed = false;
	//::: Restored by DF: 커스텀 추가  ::: loadedCnt 필드 추가됨 
	private int loadedCnt = 0;
	//::: Restored by DF: 커스텀 추가  ::: type 필드 추가됨 
	private String type = "question";
	
//::: Restored by DF: 커스텀 변경  ::: 생성자 type이 추가되며 인수3개로 변경 ▼▼▼
//	public PlayerApp(String id, PlayerEntryPoint entryPoint) {
//		this.divId = id;
//		this.entryPoint = entryPoint;
//	}
	public PlayerApp(String id, PlayerEntryPoint entryPoint, String type) {
		
        Utils.consoleLog("::: PlayerApp PlayerApp Start 생성자  id["+ id +"] type["+ type +"] ::: ");
		
		this.divId = id;
		this.entryPoint = entryPoint;
		this.type = type;
	}
//::: Restored by DF: 커스텀 변경  ::: 생성자 type이 추가되며 인수3개로 변경 ▲▲▲

	public static native int getIFrameSize(boolean isCommonPage, PlayerApp instance) /*-{
		var frameSizesHandler = function(event) {
			var data = event.data;

			if ((typeof data == 'string' || data instanceof String)
					&& data.indexOf('I_FRAME_SIZES:') === 0) {
				$wnd.iframeSize = JSON
						.parse(data.replace('I_FRAME_SIZES:', ''));
			}
		};
		$wnd.addEventListener('message', frameSizesHandler, false);
		if (!@com.lorepo.icplayer.client.PlayerApp::iframeHandlers) @com.lorepo.icplayer.client.PlayerApp::iframeHandlers = [];
		@com.lorepo.icplayer.client.PlayerApp::iframeHandlers.push(frameSizesHandler);
		$wnd.isFrameInDifferentDomain = false;
		$wnd.iframeSize = $wnd.iframeSize || {
			offsetTop : 0,
			height : 0,
			frameOffset : 64,
			frameScale : 1.0,
			windowInnerHeight : 0,
			isEditorPreview : false
		};
	}-*/;

	/**
	 * Get global score service
	 * 
	 * @return
	 */
	public IScoreService getScoreService() {
        Utils.consoleLog("::: PlayerApp getScoreService Start ::: ");
		return playerController.getScoreService();
	}

	public boolean isContentModelLoaded() {
        Utils.consoleLog("::: PlayerApp isContentModelLoaded Start isContentModelLoaded["+ isContentModelLoaded +"]::: ");
		return isContentModelLoaded;
	}

//::: Restored by DF: 커스텀 변경  ::: loadPage 메소드 isQNote가 추가되며 인수4개로 변경 ▼▼▼
	/**
	 * Load content from given URL
	 * 
	 * @param url
	 * @param pageIndex
	 * @param isCommonPage
	 */
//	private void loadPage(String url, int pageIndex, final boolean isCommonPage) {
//		startPageIndex = pageIndex;
//
//		IXMLFactory contentFactory = ContentFactory.getInstance(this.pagesSubset);
//		isContentModelLoaded = false;
//		contentFactory.load(url, new IProducingLoadingListener() {
//			public void onFinishedLoading(Object content) {
//				contentModel = (Content) content;
//				isContentModelLoaded = true;
//				initPlayer(isCommonPage);
//			}
//
//			public void onError(String error) {
//				JavaScriptUtils.log("Can't load:" + error);
//			}
//		});
//	}
	private void loadPage(final String url, int pageIndex, final boolean isCommonPage, boolean isQNote) {
		Utils.consoleLog("::: PlayerApp loadPage Start ::: ");
		
		this.startPageIndex = pageIndex;
		Utils.consoleLog("::: PlayerApp loadPage loadPage url 2 :" + url);
		Utils.consoleLog("::: PlayerApp loadPage isQNote :" + isQNote);
		Utils.consoleLog("::: PlayerApp loadPage isCommonPage :" + isCommonPage);
		Utils.consoleLog("::: PlayerApp loadPage isLoadSeperate :" + Utils.isLoadSeperate);
		IXMLFactory contentFactory = Utils.isLoadSeperate ? ContentFactoryQNote.getInstance(this.pagesSubset) : ContentFactory.getInstance(this.pagesSubset);
		Utils.consoleLog("::: PlayerApp loadPage contentFactory :" + contentFactory);
		int toCnt = Utils.getUrlCount(url);
		this.loadedCnt = 0;
		this.isContentModelLoaded = false;
		contentFactory.load(url, new IProducingLoadingListener() {
			public void onFinishedLoading(Object content) {
				Utils.consoleLog("::: PlayerApp loadPage loadPage onFinishedLoading url: " + url);
				// PlayerApp.this.contentModel = (Content) content; 와 같은 방식으로 필요한 필드에 직접 접근해야 함
				contentModel = (Content) content;
				isContentModelLoaded = true;
				initPlayer(isCommonPage); // 예시 메서드, 실제 구현에 맞게 수정 필요
			}
			public void onError(String error) {
				JavaScriptUtils.log("Can't load:" + error);
			}
		});
	}
//::: Restored by DF: 커스텀 변경  ::: loadPage 메소드 isQNote가 추가되며 인수4개로 변경 ▲▲▲
	
	/**
	 * Load content from given URL
	 * 
	 * @param url
	 * @param pageIndex
	 */
//::: Restored by DF: 커스텀 변경  ::: load 메소드 loadPage호출실 isQNote가 추가되며 인수4개로 변경 ▼▼▼
//	public void load(String url, int pageIndex) {
//		loadPage(url, pageIndex, false);
//	}
	public void load(String url, int pageIndex) {
		Utils.consoleLog("::: PlayerApp load Start url["+ url +"] pageIndex["+ pageIndex +"] isQNote["+ Utils.isQNote +"] ::: ");
		loadPage(url, pageIndex, false, Utils.isQNote);
	}
//::: Restored by DF: 커스텀 변경  ::: load 메소드 loadPage호출실 isQNote가 추가되며 인수4개로 변경 ▲▲▲

//::: Restored by DF: 커스텀 추가  ::: loadLearnetic 메소드 추가 ▼▼▼
	public void loadLearnetic(String url, int pageIndex) {
		Utils.consoleLog("::: PlayerApp loadLearnetic Start url["+ url +"] pageIndex["+ pageIndex +"] isCommonPage=fix false isQNote["+ Utils.isQNote +"]=fix false ::: ");
		loadPage(url, pageIndex, false, false);
	}

//::: Restored by DF: 커스텀 추가  ::: unload 메소드 추가 ▼▼▼
	public void unload() {
		removeIframe(this);
	}

	/**
	 * Load common page from given URL
	 * 
	 * @param url
	 * @param pageIndex
	 */
//::: Restored by DF: 커스텀 변경  ::: loadCommonPage 메소드 loadPage호출실 isQNote가 추가되며 인수4개로 변경 ▼▼▼
//	public void loadCommonPage(String url, int pageIndex) {
//		loadPage(url, pageIndex, true);
//	}
	public void loadCommonPage(String url, int pageIndex) {
		Utils.consoleLog("::: PlayerApp loadCommonPage Start url["+ url +"] pageIndex["+ pageIndex +"] isCommonPage=fix true isQNote["+ Utils.isQNote +"]=fix false ::: ");

		loadPage(url, pageIndex, true, false);
	}
//::: Restored by DF: 커스텀 변경  ::: loadCommonPage 메소드 loadPage호출실 isQNote가 추가되며 인수4개로 변경 ▲▲▲

	public void setPages(String pagesSub) {
		Utils.consoleLog("::: PlayerApp setPages Start pagesSub["+ pagesSub +"] ::: ");
		if (pagesSub == null || pagesSub.isEmpty()) {
			Utils.consoleLog("::: PlayerApp setPages 01 error ::: ");
			throw new IllegalArgumentException();
		}

		ArrayList<Integer> selectedPages = new ArrayList<Integer>();
		for (String page : pagesSub.split(",")) {
			Utils.consoleLog("::: PlayerApp setPages 02 page["+ page +"] ::: ");

			selectedPages.add(Integer.valueOf(page));
		}

		if (selectedPages.size() > 0) {
			Utils.consoleLog("::: PlayerApp setPages 03 ::: ");
			pagesSubset = selectedPages;
		}
	}

	public void setAnalytics(String id) {
		Utils.consoleLog("::: PlayerApp setAnalytics Start analyticsId["+ id +"]::: ");
		analyticsId = id;
	}

	public static native int getScreenHeight() /*-{
		if (!$wnd.isFrameInDifferentDomain) {
			var offsetIframe = $wnd.iframeSize.offsetTop;
			return $wnd.parent.innerHeight - offsetIframe;
		} else {
			// innerHeight can be unreliable on orientation change
			// i.e. https://bugs.chromium.org/p/chromium/issues/detail?id=231319
			return $wnd.outerHeight || $wnd.innerHeight;
		}
	}-*/;

	public static native int getPageHeight() /*-{
		return $wnd.$('table.ic_player').css('height').replace('px', '');
	}-*/;

//::: Restored by DF: 커스텀 추가  ::: removeIframe native 메소드 추가 ▼▼▼
	public static native void removeIframe(PlayerApp instance)/*-{
		console.log("Restored by DF: removeIframe :::");
		if ($wnd.playerIFrame) {
			// 실제 DOM에서 iframe 제거
			$wnd.playerIFrame.remove();

			// 참조 삭제
			$wnd.playerIFrame = null;

			// iframe 관련 상태 초기화
			$wnd.isInIframe = false;
			$wnd.isFrameInDifferentDomain = false;
		}
	}-*/;

	public static native void removeStaticFooter() /*-{
		var footer = $wnd.document.getElementsByClassName("ic_static_footer");
		if (footer.length > 0) {
			footer[0].style.removeProperty('top');
			footer[0].style.removeProperty('position');
			footer[0].style.removeProperty('bottom');
			footer[0].classList.remove("ic_static_footer");
		}
	}-*/;

	public static native void registerGetIframe(PlayerApp instance) /*-{
		$wnd.get_iframe = function() {
			if ($wnd.playerIFrame) {
				return $wnd.playerIFrame;
			}

			try {
				var currentLocation = $wnd.location.href;

				$wnd.parent.$('iframe').each(function() {
					if (this.contentWindow === $wnd) {
						$wnd.playerIFrame = $wnd.$(this);
					}
				});
				$wnd.isFrameInDifferentDomain = false;

				$wnd.isInIframe = ($wnd.location != $wnd.parent.location) ? true
						: false;

				return $wnd.playerIFrame;
			} catch (e) {
				$wnd.isFrameInDifferentDomain = true;
			}
		}
	}-*/;

	public static native void setPageTopAndStaticHeader(int top) /*-{

		console.log("::: PlayerApp native setPageTopAndStaticHeader headerHeight[" + headerHeight + "]::: ");

		var page = $wnd.$(".ic_page");
		var pagePanel = page.parent();
		page.css("top", top);

		$wnd.$(".ic_header").parent().addClass("ic_static_header");
		$wnd.$(".ic_static_header").css("width", page.css("width"));
		if ($wnd.isFrameInDifferentDomain || $wnd.isInIframe) {
			var headerScrollHandler = function(event) {
				if ((typeof event.data == 'string' || event.data instanceof String)
						&& event.data.indexOf('I_FRAME_SIZES:') === 0) {
					var scroll = $wnd.iframeSize.offsetTop;
					var playerOffset = $wnd.iframeSize.frameOffset || 64;
					if ($wnd.iframeSize.isEditorPreview) {
						playerOffset = 0;
					}
					var iframeScale = 1.0;
					if ($wnd.iframeSize.frameScale != null) {
						iframeScale = $wnd.iframeSize.frameScale;
					}
					var top = scroll > playerOffset ? (scroll - playerOffset)
							/ iframeScale
							: 0;
					$wnd.$(".ic_static_header").css("top", top);
				}
			};
			$wnd.addEventListener('message', headerScrollHandler);
			if (!@com.lorepo.icplayer.client.PlayerApp::iframeHandlers) @com.lorepo.icplayer.client.PlayerApp::iframeHandlers = [];
			@com.lorepo.icplayer.client.PlayerApp::iframeHandlers.push(headerScrollHandler);
		} else {
			var logoHeight = $wnd.$("#_icplayer").offset().top;

			if (logoHeight > 0) {
				$wnd.addEventListener('scroll', function() {
					var scroll = $wnd.scrollY;
					if (scroll < logoHeight) {
						$wnd.$(".ic_static_header").css("top",
								logoHeight - scroll);
					} else {
						$wnd.$(".ic_static_header").css("top", 0);
					}
				});
			} else {
				$wnd.$(".ic_static_header").css("top", 0);
			}
		}

		var pageHeight = page.css("height").replace("px", "");
		var height = parseInt(pageHeight, 10) + parseInt(top, 10);

		pagePanel.css("height", height);
		$wnd.$(".ic_content").parent().css("height", height);
	}-*/;

	public native static void setFooterWidth() /*-{
		var footer = $wnd.document.getElementsByClassName("ic_footer");
		var page = $wnd.document.getElementsByClassName("ic_page");

		if (footer.length > 0 && page.length > 0) {
			footer[0].parentNode.style.width = page[0].style.width;
		}
	}-*/;

	public static native void setStaticFooter(int headerHeight, boolean isHeaderStatic) /*-{
		var footer = $wnd.$(".ic_footer");
		if (footer.length == 0)
			return;
		var page = $wnd.$(".ic_page");

		footer.parent().addClass("ic_static_footer");
		footer.css("top", 0);

		var pageHeight = parseInt(page.css("height").replace('px', ''), 10);
		var icFooterHeight = parseInt(footer.css('height').replace('px', ''),
				10);

		page.css("marginBottom", icFooterHeight);

		if ($wnd.isFrameInDifferentDomain || $wnd.isInIframe) {
			var offsetIframe = $wnd.iframeSize.frameOffset;
			var sum = $wnd.iframeSize.windowInnerHeight - offsetIframe
					- icFooterHeight;

			if (sum >= 0) {
				$wnd.$(".ic_static_footer").css("top", sum + "px");
			}
			var footerScrollHandler = function(event) {
				if ((typeof event.data == 'string' || event.data instanceof String)
						&& event.data.indexOf('I_FRAME_SIZES:') === 0) {
					var scroll = $wnd.iframeSize.offsetTop;
					offsetIframe = $wnd.iframeSize.notScaledOffset;
					iframeScale = 1.0;
					if ($wnd.iframeSize.frameScale != null) {
						iframeScale = $wnd.iframeSize.frameScale;
					}
					sum = ($wnd.iframeSize.windowInnerHeight
							- icFooterHeight + scroll)
							/ iframeScale - offsetIframe;
					if (parseInt(sum) >= (parseInt($wnd.iframeSize.height) - parseInt(icFooterHeight))) {
						$wnd.$(".ic_static_footer").css("top",
								"auto");
					} else {
						$wnd.$(".ic_static_footer").css("top",
								sum + "px");
					}
				}
			};
			$wnd.addEventListener('message', footerScrollHandler);
			if (!@com.lorepo.icplayer.client.PlayerApp::iframeHandlers) @com.lorepo.icplayer.client.PlayerApp::iframeHandlers = [];
			@com.lorepo.icplayer.client.PlayerApp::iframeHandlers.push(footerScrollHandler);
		} else {
			var referrer = $doc.referrer;

			if (referrer.indexOf($wnd.location.origin) > -1) {
				var offsetIframe = $wnd.get_iframe() ? $wnd.get_iframe()
						.offset().top : 0;
				var sum = parseInt(window.top.innerHeight, 10) - offsetIframe
						- icFooterHeight;
				$wnd.$(".ic_static_footer").css("top", sum + "px");

				$wnd.parent.addEventListener('scroll', function() {
					var parentScroll = $wnd.parent.scrollY;
					sum = parseInt(window.top.innerHeight, 10) - offsetIframe
							- icFooterHeight + parentScroll;

					if (sum >= (($wnd.get_iframe() ? $wnd.get_iframe().height()
							: 0) - icFooterHeight)) {
						$wnd.$(".ic_static_footer").css("top", "auto");
					} else {
						$wnd.$(".ic_static_footer").css("top", sum + "px");
					}
				});
			}
		}

		if (isHeaderStatic) {
			var pagePanel = page.parent();
			var pageHeight = page.css("height").replace("px", "");
			var replacedHeaderHeight = headerHeight.replace("px", "");

			var height = parseInt(pageHeight, 10)
					+ parseInt(icFooterHeight, 10)
					+ parseInt(replacedHeaderHeight, 10);

			$wnd.$(".ic_content").parent().css("height", height);
		}
	}-*/;

	// moveStaticElementsWhenScaled should be called only once to start animation
	public static void prepareStaticScaledElements() {
		Utils.consoleLog("::: PlayerApp prepareStaticScaledElements Start ::: ");
		
		if (!isAnimationRunning) {
			isAnimationRunning = moveStaticElementsWhenScaled();
		} else {
			setStaticScaledFooterStyles();
		}
	}

	// when changing orientation ic_static_footer class can be removed and restored
	// if there is scaling - we need to readd necessary styles
	public native static void setStaticScaledFooterStyles() /*-{
		var footer = $wnd.document.getElementsByClassName('ic_static_footer');
		
		if (footer.length > 0) {
			var playerOffset = @com.lorepo.icplayer.client.utils.PlayerUtils::getAbsTopOffset()();
			var height = footer[0].offsetHeight, scale = $wnd.player
					.getPlayerServices().getScaleInformation().scaleY, windowHeight = $wnd.outerHeight
					|| $wnd.innerHeight;

			footer[0].style.position = 'absolute';
			footer[0].style.bottom = 'initial';
			footer[0].style.top = ((windowHeight / scale - height) - playerOffset) + 'px'; 
		}
	}-*/;

	/*
	 * This function move static header and footer when content is scaled -
	 * transform scale property causes css static position to stop working. It uses
	 * requestAnimationFrame and in every possible frame position of header and
	 * footer is updated. It also accounts for window height changes (e.g. when
	 * navigation bar is hidden/shown on Android) When values of scroll and window
	 * height are equal to previous ones it doesn't make computation in that tick.
	 */
	private static native boolean moveStaticElementsWhenScaled() /*-{
		// handling player placed in iframe is covered in setStaticFooter/Header function
		if ($wnd.isFrameInDifferentDomain || $wnd.isInIframe)
			return false;

		// some older browsers support requestAnimationFrame as experimental feature
		if (!$wnd.requestAnimationFrame) {
			$wnd.requestAnimationFrame = (function() {
				return $wnd.webkitRequestAnimationFrame
						|| $wnd.mozRequestAnimationFrame
						|| $wnd.oRequestAnimationFrame
						|| $wnd.msRequestAnimationFrame;
			})();
		}

		var previousScroll, previousWindowHeight, footer = $wnd.document
				.getElementsByClassName('ic_static_footer'), header = $wnd.document
				.getElementsByClassName('ic_static_header');

		if (header.length > 0) {
			header[0].style.position = 'absolute';
		}

		if (footer.length > 0) {
			footer[0].style.bottom = 'initial';
			footer[0].style.position = 'absolute';
		}

		function step() {

			var currentScale = $wnd.player.getPlayerServices()
					.getScaleInformation().scaleY, currentScroll = $wnd.pageYOffset, currentWindowHeight = $wnd.innerHeight;
					
			var playerOffset = @com.lorepo.icplayer.client.utils.PlayerUtils::getAbsTopOffset()() / currentScale;
			if ($wnd.window.visualViewport
					&& $wnd.window.visualViewport.scale == 1) {
				currentWindowHeight = $wnd.window.visualViewport.height;
			}
			// if values didn't change, don't make calculations
			if (previousScroll === currentScroll
					&& previousWindowHeight === currentWindowHeight) {
				$wnd.requestAnimationFrame(step);
				return false;
			}

			// update values
			previousScroll = currentScroll;
			previousWindowHeight = currentWindowHeight;

			var top = (currentScroll / currentScale);
			var headerTop = top - playerOffset;

			// on iOS there is 'bounce' area which can hide header and footer
			// when top is overscrolled it will be lower than 0
			// when bottom is overscrolled sum of window height and scroll will exceed body height
			var documentHeight = Math.max($wnd.document.body.scrollHeight,
					$wnd.document.body.offsetHeight,
					$wnd.document.documentElement.clientHeight,
					$wnd.document.documentElement.scrollHeight,
					$wnd.document.documentElement.offsetHeight);
			isOverscrolledBottom = (currentScroll + currentWindowHeight) > documentHeight;

			if (headerTop < 0) {
				headerTop = 0;
			}

			if (header.length > 0) {
				header[0].style.top = headerTop + 'px';
			}

			if (footer.length > 0) {
				var icFooterHeight = footer[0].clientHeight, footerTop = top
						+ (currentWindowHeight / currentScale) - icFooterHeight;

				// sets footertop to bottom of document when overscrolled
				if (isOverscrolledBottom) {
					footerTop = documentHeight / currentScale - icFooterHeight;
				}
				footer[0].style.top = (footerTop - playerOffset) + 'px';
			}

			// next frame
			$wnd.requestAnimationFrame(step);
		}

		// begin
		if (footer.length > 0 || header.length > 0) {
			$wnd.requestAnimationFrame(step);
			return true;
		}
		return false;
	}-*/;

	public static native int getHeaderHeight() /*-{
		return $wnd.$(".ic_header").css("height");
	}-*/;

	public static native String getStaticHeaderHeight() /*-{
		return $wnd.$(".ic_static_header").css("height").replace("px", "");
	}-*/;

	public static native String getStaticFooterHeight() /*-{
		return $wnd.$(".ic_footer").css("height").replace("px", "");
	}-*/;

	public static native boolean isStaticFooter() /*-{
		return $wnd.$(".ic_static_footer").length > 0;
	}-*/;

	public static native boolean isStaticHeader() /*-{
		return $wnd.$(".ic_static_header").length > 0;
	}-*/;

	public JavaScriptObject getContextMetadata() {
		return this.entryPoint.getContextMetadata();
	}

	/**
	 * Get base URL from context metadata what will be used to get relative content.
	 * Content base URL is used to create absolute links with addition of relative paths existing in content
	 * (urls in CSS values, properties values, HTML content, etc.).
	 * Content base URL is needed when files referenced by the content cannot be found using relative paths.
	 * e.g. /file/123 fill not exist on mLibro when referring to online resource on mcourser.
	 *
	 * @return Content base URL when given in ContextMetadata, otherwise null
	 */
	private String getContentBaseURL() {
		Utils.consoleLog("::: PlayerApp getContentBaseURL Start ::: ");
		JavaScriptObject contextMetadata = getContextMetadata();
		if (contextMetadata != null) {
			String contentBaseURL = JavaScriptUtils.getArrayItemByKey(contextMetadata, "contentBaseURL");
			String returnStr = contentBaseURL == "" ? null : contentBaseURL;
			
			Utils.consoleLog("::: PlayerApp getContentBaseURL 01 contentBaseURL [" + returnStr + "] ::: ");
			
			return returnStr;
		}
		Utils.consoleLog("::: PlayerApp getContentBaseURL 02 contentBaseURL [null] ::: ");
		return null;
	}

	public void setExternalVariables(JavaScriptObject contextData) {
		this.entryPoint.setExternalVariables(contextData);
	}

	public JavaScriptObject getExternalVariables() {
		Utils.consoleLog("::: PlayerApp getExternalVariables Start ::: ");
		return this.entryPoint.getExternalVariables();
	}

	/**
	 * Init player after content is loaded
	 */
	@SuppressWarnings("static-access")
	private void initPlayer(final boolean isCommonPage) {
		Utils.consoleLog("::: PlayerApp initPlayer Start ::: ");
		registerGetIframe(this);
		getIFrameSize(isCommonPage, this);
		this._initPlayer(isCommonPage);
		this.getIframe();
	}

	public static native void setLangAttribute(String lang) /*-{
		console.log("::: PlayerApp native setLangAttribute lang[" + lang + "]");
		$wnd.$("html").attr("lang", lang);
	}-*/;

	/**
	 * Init player after content is loaded
	 */
	private void _initPlayer(final boolean isCommonPage) {
		Utils.consoleLog("::: PlayerApp _initPlayer Start isCommonPage["+isCommonPage+"]::: ");
		
		PlayerView playerView = new PlayerView();
		playerController = new PlayerController(this.contentModel, playerView, bookMode, entryPoint);
		playerController.setPlayerConfig(playerConfig);
		playerController.setFirstPageAsCover(showCover);
		playerController.setAnalytics(analyticsId);
		playerController.getPlayerServices().setApplication(this);
		EnableTabindex.getInstance().create(contentModel.getMetadataValue("enableTabindex").compareTo("true") == 0);

		final PlayerView finalPlayerView = playerView;
		playerController.addPageLoadListener(new ILoadListener() {
			@Override
			public void onFinishedLoading(Object obj) {
				Utils.consoleLog("::: PlayerApp _initPlayer addPageLoadListener 01 : obj[" + obj + "]");
				if (contentModel.getMetadataValue("staticHeader").compareTo("true") == 0
						&& playerController.hasHeader()) {
					makeHeaderStatic();
				}

				if (contentModel.getMetadataValue("staticFooter").compareTo("true") == 0
						&& playerController.hasFooter()) {
					makeFooterStatic();
				}

				setLangAttribute(contentModel.getMetadataValue("lang"));

				if (contentModel.getMetadataValue("disableNavigationPanels").compareTo("true") == 0) {
					finalPlayerView.setDisableNavigationPanels(true);
				}

				Utils.consoleLog("::: PlayerApp _initPlayer addPageLoadListener 02 : type[" + type + "]");
				if (type.equals("question")) {
					entryPoint.onPageLoaded();
				} else if (type.equals("jimun")) {
					entryPoint.onPageLoadedJimun();
				} else if (type.equals("jimun")) {
					entryPoint.onPageLoadedAnswer();
				} else if (type.equals("solve")) {
					entryPoint.onPageLoadedSolve();
				}
			}

			@Override
			public void onError(String error) {
				JavaScriptUtils.log("Loading pages error: " + error);
			}
		});
		contentModel.setPlayerController(getPlayerServices());
		RootPanel.get(divId).clear();
		RootPanel.get(divId).add(playerView);
		this.loadActualLayoutCSSStyles();
		this.loadAttachedLibraries();

		ContentDataLoader loader = new ContentDataLoader(contentModel.getBaseUrl(), getContentBaseURL());
		loader.setDefaultLayoutID(contentModel.getActualSemiResponsiveLayoutID());

		loader.addAddons(contentModel.getAddonDescriptors().values());

		for (Page header : this.contentModel.getHeaders()) {
			loader.addPage(header);
		}
		for (Page footer : this.contentModel.getFooters()) {
			loader.addPage(footer);
		}

		loader.load(new ILoadListener() {
			@Override
			public void onFinishedLoading(Object obj) {
				Utils.consoleLog("::: PlayerApp _initPlayer load onFinishedLoading 03 : obj[" + obj + "]");
				loadFirstPage(isCommonPage);
			}

			@Override
			public void onError(String error) {
				Utils.consoleLog("::: PlayerApp _initPlayer load onFinishedLoading 04 error Loading ContentData have failed");
				JavaScriptUtils.log("Loading ContentData have failed, error: " + error);
			}
		});
	}

	private void loadActualLayoutCSSStyles() {
		Utils.consoleLog("::: PlayerApp loadActualLayoutCSSStyles Start ::: ");
		String actualCSSID = this.contentModel.getActualSemiResponsiveLayoutID();
		CssStyle actualStyle = contentModel.getStyle(actualCSSID);
		String cssValue = actualStyle.getValue();
		String css = URLUtils.resolveCSSURL(contentModel.getBaseUrl(), cssValue);
		DOMInjector.appendStyle(this.getCurrentUserStyles());
	}

	private void loadAttachedLibraries() {
		Utils.consoleLog("::: PlayerApp loadAttachedLibraries Start ::: ");
		Map<String, ScriptAsset> attachedLibraries = playerController.getAssetsService().getAttachedLibraries();
		String baseUrl = contentModel.getBaseUrl();
		String contentBaseURL = getContentBaseURL();
		for (ScriptAsset libraryAsset : attachedLibraries.values()) {
			String href = ExtendedRequestBuilder.signURL(libraryAsset.getHref());
			DOMInjector.injectLibrary(
				URLUtils.resolveURL(baseUrl, href, contentBaseURL),
				libraryAsset.getFileName(),
				libraryAsset.isModule()
			);
		}
	}

	private void makeHeaderStatic() {
		Utils.consoleLog("::: PlayerApp makeHeaderStatic ::: ");
		int headerHeight = getHeaderHeight();
		Utils.consoleLog("::: PlayerApp makeHeaderStatic headerHeight[" + headerHeight + "] ::: ");
		setPageTopAndStaticHeader(headerHeight);
		isStaticHeader = true;
	}

	private void makeFooterStatic() {
		Utils.consoleLog("::: PlayerApp makeFooterStatic ::: ");
		
		removeStaticFooter();

		final int screenHeight = getScreenHeight();
		final int pageHeight = getPageHeight();

		Utils.consoleLog("::: PlayerApp makeFooterStatic screenHeight[" + screenHeight + "] pageHeight[" + pageHeight + "] ::: ");
		
		// when changing layout on device orientation change, old width can be to big
		// for new layout
		setFooterWidth();
		if (screenHeight < pageHeight) {
			final int headerHeight = getHeaderHeight();
			setStaticFooter(headerHeight, isStaticHeader);
		}
	}

	private static native void getIframe() /*-{
		$wnd.get_iframe();
	}-*/;

	private void loadFirstPage(boolean isCommonPage) {
		Utils.consoleLog("::: PlayerApp loadFirstPage Start isCommonPage[" + isCommonPage + "] ::: ");
		
		if (loadedState != null) {
			Utils.consoleLog("::: PlayerApp loadFirstPage 01 loadedState[" + loadedState + "] ::: ");
			
			playerController.getPlayerServices().getStateService().loadFromString(loadedState.get("state"));
			playerController.getPlayerServices().getScoreService().loadFromString(loadedState.get("score"));
			playerController.getPlayerServices().getTimeService().loadFromString(loadedState.get("time"));
			playerController.getPlayerServices().getAdaptiveLearningService().loadFromString(loadedState.get("adaptiveLearning"));
			if (this.loadedState.get("isReportable") != null) {
				this.playerController.getPlayerServices().getReportableService()
						.loadFromString(this.loadedState.get("isReportable"));
			}
			if (this.loadedState.get("visitedPages") != null) {
				this.playerController.loadVisitedPagesFromString(this.loadedState.get("visitedPages"));
			}
		}
		if (pagesOpenActivitiesScores != null) {
			Utils.consoleLog("::: PlayerApp loadFirstPage 02 pagesOpenActivitiesScores[" + pagesOpenActivitiesScores + "] ::: ");
			playerController.getPlayerServices().getScoreService().setOpenActivitiesScores(this.pagesOpenActivitiesScores);
		}

		// All reportable values for pages should be loaded before start.
		Content playerModel = this.playerController.getModel();
		HashMap<String, String> states = this.playerController.getPlayerServices().getReportableService().getStates();
		for (int i = 0; i < playerModel.getPageCount(); i++) {
			this.setPageReportableFromMap(playerModel.getPage(i), states);
		}

		if (isCommonPage) {
			Utils.consoleLog("::: PlayerApp loadFirstPage 03  ::: ");
			playerController.switchToCommonPage(startPageIndex);
		} else {
			Utils.consoleLog("::: PlayerApp loadFirstPage 04  ::: ");
			playerController.initHeaders();
			playerController.switchToPage(startPageIndex);
		}
		Utils.consoleLog("::: PlayerApp loadFirstPage End  ::: ");
	}

	private void setPageReportableFromMap(IPage page, HashMap<String, String> isReportableMap) {
		Utils.consoleLog("::: PlayerApp setPageReportableFromMap Start ::: ");
		
		String key = page.getId();
		String isReportableStr = isReportableMap.get(key);
		if (isReportableStr == null) {
			return;
		}

		if (isReportableStr.toLowerCase() == "true") {
			page.setAsReportable();
		} else {
			page.setAsNonReportable();
		}
		
		Utils.consoleLog("::: PlayerApp setPageReportableFromMap End  ::: ");
	}

	public IPlayerServices getPlayerServices() {
		Utils.consoleLog("::: PlayerApp getPlayerServices Start ::: ");
		return playerController.getPlayerServices();
	}

	public void updateScore() {
		Utils.consoleLog("::: PlayerApp updateScore Start ::: ");
		playerController.updateScore();
	}

	public static class PlayerConfigOverlay extends JavaScriptObject {
		protected PlayerConfigOverlay() {
		}

		public final native PlayerEventsConfigOverlay getEvents() /*-{
			return this.events ? this.events : {};
		}-*/;

		/**
		 * Because GWT Overlay Types cannot be created with new keyword and it only can
		 * be created from JSNI we're returning empty JavaScript object that will be
		 * marshaled to PlayerConfig type.
		 * 
		 * @return
		 */
		public static final native PlayerConfigOverlay getEmpty() /*-{
			return {};
		}-*/;
	}

	public static class PlayerEventsConfigOverlay extends JavaScriptObject {
		protected PlayerEventsConfigOverlay() {
		}

		public final native String[] getDisabled() /*-{
			return this.disabled ? this.disabled : [];
		}-*/;
	}

	public void setConfig(JavaScriptObject config) {
		PlayerConfigOverlay overlayConfig = JSONUtils.parseOverlayType(config, PlayerConfigOverlay.class);

		this.playerConfig = PlayerConfig.fromOverlay(overlayConfig);
	}

	public void setState(String state) {
		Utils.consoleLog("::: PlayerApp setState Start state[" + state + "] ::: ");
		
		HashMap<String, String> data = JSONUtils.decodeHashMap(state);
		Utils.consoleLog("::: PlayerApp setState 01 data[" + data + "] ::: ");
		
		if (data.containsKey("state") && data.containsKey("score")) {
			Utils.consoleLog("::: PlayerApp setState 02 loadedState [" + data + "] ::: ");
			loadedState = data;
		}
		Utils.consoleLog("::: PlayerApp setState End ::: ");
	}

	public String getState() {
		Utils.consoleLog("::: PlayerApp getState Start ::: ");
		playerController.updateState();
		String state = playerController.getPlayerServices().getStateService().getAsString();
		String score = playerController.getPlayerServices().getScoreService().getAsString();
		String time = playerController.getPlayerServices().getTimeService().getAsString();
		String isReportable = playerController.getPlayerServices().getReportableService().getAsString();
		String visitedPages = playerController.getVisitedPagesAsString();
		String adaptivePageHistory = playerController.getAdaptiveLearningService().getStateAsString();

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("state", state);
		data.put("score", score);
		data.put("time", time);
		data.put("isReportable", isReportable);
		data.put("adaptiveLearning", adaptivePageHistory);
		data.put("visitedPages", visitedPages);

		return JSONUtils.toJSONString(data);
	}

	public void setBookMode() {
		Utils.consoleLog("::: PlayerApp setBookMode Start ::: ");
		bookMode = true;
	}

	public void showCover(boolean show) {
		Utils.consoleLog("::: PlayerApp showCover Start ::: ");
		showCover = show;
	}

	public JavaScriptObject getSemiResponsiveLayouts() {
		Utils.consoleLog("::: PlayerApp getSemiResponsiveLayouts Start ::: ");
		return this.contentModel.getSemiResponsiveLayoutsAsJS();
	}

	public boolean changeLayout(String layoutID) {
		Utils.consoleLog("::: PlayerApp getSemiResponsiveLayouts Start layoutID["+layoutID+"]::: ");
		
		boolean isLayoutChanged = false;
		boolean isAble = this.playerController.getPlayerServices().isAbleChangeLayout();
		this.lastSentLayoutID = layoutID;
		if (isAble) {
			isLayoutChanged = this.contentModel.setActualLayoutID(layoutID);
			if (isLayoutChanged) {
				this.loadActualLayoutCSSStyles();
				int pageIndex = this.playerController.getCurrentPageIndex();
				this.playerController.switchToPage(pageIndex);
			}
		}
		Utils.consoleLog("::: PlayerApp getSemiResponsiveLayouts End isLayoutChanged["+isLayoutChanged+"]::: ");
		return isLayoutChanged;
	}

	public void handleUpdatingMathJax() {
		String mathJaxRenderer = this.entryPoint.getMathJaxRendererOption();
		if (mathJaxRenderer.equals("MathML")) {
			this.updateMathJax();
		} else {
			this.updateMathJaxWithTimeout(this);
		}
	}

	//It is necessery to run updateMathJax with timeout to properly display element for HTML & CSS rendering
	public static native void updateMathJaxWithTimeout(PlayerApp x) /*-{
		setTimeout(function() {
			x.@com.lorepo.icplayer.client.PlayerApp::updateMathJax()();
		}, 200);
	}-*/;

	public void updateMathJax() {
		if (this.playerController != null) {
			this.playerController.updateMathJaxInCurrentPage();
		}
	}

	public void updateLayout() {
		Utils.consoleLog("::: PlayerApp updateLayout Start ::: ");
		changeLayout(this.lastSentLayoutID);
	}

	public boolean changeLayoutByName(String layoutName) {
		Utils.consoleLog("::: PlayerApp changeLayoutByName Start layoutName["+layoutName+"]::: ");
		String layoutID = this.contentModel.getLayoutIDByName(layoutName);
		return this.changeLayout(layoutID);
	}
	
	public void generatePrintableHTML(PrintableParams params) {
		Utils.consoleLog("::: PlayerApp generatePrintableHTML Start ::: ");
		printableParser = new PrintableContentParser();

		if (this.printableOrder != null) {
			printableParser.setPrintableOrder(this.printableOrder);
		}
		
		if (params.seed != -1) {
			printableParser.setRandomSeed(params.seed);
		}
		
		printableParser.setDPI(params.dpi);
		printableParser.setListener(params.listener);
		printableParser.setRandomizePages(params.randomizePages);
		printableParser.setRandomizeModules(params.randomizeModules);
		printableParser.setShowAnswers(params.showAnswers);
		printableParser.setState(loadedState);
		printableParser.generatePrintableHTML(contentModel, pagesSubset);
	};

	public void setPrintableOrder(JavaScriptObject order) {
		Utils.consoleLog("::: PlayerApp setPrintableOrder Start ::: ");
		this.printableOrder = PrintableOrderParser.toHashMap(order);
	}

	private String getCurrentUserStyles() {
		Utils.consoleLog("::: PlayerApp getCurrentUserStyles Start ::: ");
		String actualCSSID = this.contentModel.getActualSemiResponsiveLayoutID();
		CssStyle actualStyle = contentModel.getStyle(actualCSSID);
		String cssValue = actualStyle.getValue();
		String css = URLUtils.resolveCSSURL(contentModel.getBaseUrl(), cssValue, getContentBaseURL());
		
		Utils.consoleLog("::: PlayerApp getCurrentUserStyles End css["+css+"]::: ");
		return css;
	}
	
	public String getCurrentStyles () {
		Utils.consoleLog("::: PlayerApp getCurrentStyles Start ::: ");
		ContentDataLoader loader = new ContentDataLoader(contentModel.getBaseUrl(), getContentBaseURL());
		loader.setDefaultLayoutID(contentModel.getActualSemiResponsiveLayoutID());

		loader.addAddons(contentModel.getAddonDescriptors().values());
		
		Utils.consoleLog("::: PlayerApp getCurrentStyles End CurrentStyles["+ loader.getAddonsCSS() + "\n" + this.getCurrentUserStyles() +"]::: ");
		return loader.getAddonsCSS() + "\n" + this.getCurrentUserStyles();
	}
	
//::: Restored by DF: 커스텀 변경  ::: preloadAllPages 메소드 PageFactoryQNote관련 변경 ▼▼▼
//	public void preloadAllPages(final ILoadListener listener) {
//		List<Page> pages = contentModel.getPages().getAllPages();
//		boolean allPagesLoaded = true;
//		for (Page page: pages) {
//			if (!page.isLoaded()) {
//				allPagesLoaded = false;
//				String baseUrl = contentModel.getBaseUrl();
//				String url = URLUtils.resolveURL(baseUrl, page.getHref());
//				PageFactory factory = new PageFactory((Page) page);
//				factory.load(url, new IProducingLoadingListener() {
//					@Override
//					public void onFinishedLoading(Object producedItem) {
//						List<Page> pages = contentModel.getPages().getAllPages();
//						boolean allPagesLoaded = true;
//						for(Page p: pages) {
//							if (!p.isLoaded()) {
//								allPagesLoaded = false;
//								break;
//							}
//						}
//						if (allPagesLoaded) {
//							listener.onFinishedLoading("All pages have been loaded!");
//						}
//					}
//
//					@Override
//					public void onError(String error) {
//						listener.onError(error);
//					}
//				});
//			}
//		}
//		if (allPagesLoaded) {
//			listener.onFinishedLoading("All pages have been loaded!");
//		}
//	};
	public void preloadAllPages(final ILoadListener listener) {
		Utils.consoleLog("::: PlayerApp preloadAllPages Start ::: ");
		
		List<Page> pages = contentModel.getPages().getAllPages();
		boolean allPagesLoaded = true;
		
		for (final Page page : pages) {
			if (!page.isLoaded()) {
				allPagesLoaded = false;
				String baseUrl = contentModel.getBaseUrl();
				String url = URLUtils.resolveURL(baseUrl, page.getHref());
				
				if (Utils.isLoadSeperate) {
					Utils.consoleLog("::: PlayerApp preloadAllPages 01  new PageFactoryQNote ::: ");
					PageFactoryQNote factory = new PageFactoryQNote(page);
					factory.load(url, new PagePreloadListener(listener));
				} else {
					Utils.consoleLog("::: PlayerApp preloadAllPages 02  new PageFactory ::: ");
					PageFactory factory = new PageFactory(page);
					factory.load(url, new PagePreloadListener(listener));
				}
			}
		}
		
		if (allPagesLoaded) {
			listener.onFinishedLoading("All pages have been loaded!");
		}
		Utils.consoleLog("::: PlayerApp preloadAllPages End ::: ");
	}
//::: Restored by DF: 커스텀 변경  ::: preloadAllPages 메소드 PageFactoryQNote관련 변경 ▲▲▲

//::: Restored by DF: 커스텀 추가  ::: PagePreloadListener 클래스 추가 ▼▼▼
	private class PagePreloadListener implements IProducingLoadingListener {
		private final ILoadListener listener;
		PagePreloadListener(ILoadListener listener) {
			this.listener = listener;
		}
		public void onFinishedLoading(Object producedItem) {
			Utils.consoleLog("::: PlayerApp PagePreloadListener onFinishedLoading Start ::: ");
			List<Page> pages = contentModel.getPages().getAllPages();
			boolean allPagesLoaded = true;
			for (Page p : pages) {
				if (!p.isLoaded()) {
					allPagesLoaded = false;
					break;
				}
			}
			if (allPagesLoaded) {
				listener.onFinishedLoading("All pages have been loaded!");
			}
		}
		public void onError(String error) {
			Utils.consoleLog("::: PlayerApp PagePreloadListener onError Start ::: ");
			listener.onError(error);
		}
	}
//::: Restored by DF: 커스텀 추가  ::: PagePreloadListener 클래스 추가 ▲▲▲

	public List<ScoreWithMetadata> getScoreWithMetadata() {
		Utils.consoleLog("::: PlayerApp getScoreWithMetadata Start ::: ");
		playerController.updateState();
		return playerController.getScoreWithMetadataService().getScoreWithMetadata();
	}

    public void setScoreWithMetadata(String state) {
    	Utils.consoleLog("::: PlayerApp setScoreWithMetadata Start state["+state+"]::: ");
		playerController.getScoreWithMetadataService().setScoreWithMetadata(state);
		playerController.updateState();
	}

	public void clearBeforeReload() {
    	Utils.consoleLog("::: PlayerApp clearBeforeReload Start ::: ");
		clearGlobalAddonVariables();
		removeStaticFooter();
		clearMediaRecorders();
		getPlayerServices().getCommands().closePopup();
		playerController.disableKeyboardNavigation();
		playerController.setTextReading(false);
		playerController.clearKeyboardNavigationListeners();
		playerController.getView().hideNavigationPanels();
		clearIframeHandlers();
	}

	public void setNVDAAvailability(boolean shouldUseNVDA) {
		Utils.consoleLog("::: PlayerApp setNVDAAvailability Start ::: ");
		playerController.setNVDAAvailability(shouldUseNVDA);
	}

	private native void clearGlobalAddonVariables()/*-{
		$wnd.savedPanel = null; //IWB_Toolbar
		$wnd.crossLessonAccessCache = null; //Cross_Lesson
	}-*/;

	private native void clearMediaRecorders()/*-{
		// unless removed manually, it may take some time for the recorder to be destroyed
		$wnd.$('.ic_player .addon_Media_Recorder').remove();
	}-*/;

	private native void clearIframeHandlers()/*-{
		var iframeHandlers = @com.lorepo.icplayer.client.PlayerApp::iframeHandlers;
		@com.lorepo.icplayer.client.PlayerApp::iframeHandlers = [];
		if (iframeHandlers) {
			for(var i = 0; i < iframeHandlers.length; i++) {
				$wnd.removeEventListener("message", iframeHandlers[i]);
			}
		}
	}-*/;
	
	public void setOpenActivitiesScores(HashMap<String, PageOpenActivitiesScore> pagesOpenActivitiesScores) {
		Utils.consoleLog("::: PlayerApp setOpenActivitiesScores Start ::: ");
		this.pagesOpenActivitiesScores = pagesOpenActivitiesScores;
		if (playerController != null) {
			playerController.getPlayerServices().getScoreService().setOpenActivitiesScores(this.pagesOpenActivitiesScores);
		}
	}

	public String getMathJaxRendererOption() {
		return this.entryPoint.getMathJaxRendererOption();
	}
}
