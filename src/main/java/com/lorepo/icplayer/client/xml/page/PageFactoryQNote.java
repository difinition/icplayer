// Source code is decompiled from a .class file using FernFlower decompiler.
package com.lorepo.icplayer.client.xml.page;

import com.lorepo.icplayer.client.model.page.Page;
import com.lorepo.icplayer.client.xml.IParser;
import com.lorepo.icplayer.client.xml.IProducingLoadingListener;
import com.lorepo.icplayer.client.xml.XMLVersionAwareFactoryQNote;
import com.lorepo.icplayer.client.xml.page.parsers.IPageParser;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v0;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v1;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v2;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v3;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v4;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v5;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v6;
import com.lorepo.icplayer.client.xml.page.parsers.PageParser_v7;
import java.util.Iterator;

public class PageFactoryQNote extends XMLVersionAwareFactoryQNote {
   Page producedPage = null;
   static String defaultLayoutID = null;

   public PageFactoryQNote(Page page) {
      this.producedPage = page;
      this.addParser(new PageParser_v0());
      this.addParser(new PageParser_v1());
      this.addParser(new PageParser_v2());
      this.addParser(new PageParser_v3());
      this.addParser(new PageParser_v4());
      this.addParser(new PageParser_v5());
      this.addParser(new PageParser_v6());
      this.addParser(new PageParser_v7());
      if (defaultLayoutID != null) {
         this.setDefaultLayoutID(defaultLayoutID);
      }

   }

   public void addParser(IPageParser parser) {
      parser.setPage(this.producedPage);
      this.parsersMap.put(parser.getVersion(), parser);
   }

   public void setDefaultLayoutID(String layoutID) {
      defaultLayoutID = layoutID;
      Iterator<String> iter = this.parsersMap.keySet().iterator();

      while(iter.hasNext()) {
         IParser parser = (IParser)this.parsersMap.get(iter.next());
         if (parser instanceof IPageParser) {
            ((IPageParser)parser).setDefaultLayoutID(layoutID);
         }
      }

   }

   public void load(String fetchUrl, IProducingLoadingListener listener) {
      this.producedPage.setBaseURL(fetchUrl);
      super.load(fetchUrl, listener);
   }
}
