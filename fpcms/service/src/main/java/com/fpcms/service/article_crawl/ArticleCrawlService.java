package com.fpcms.service.article_crawl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.fpcms.common.util.ApplicationContextUtil;
import com.fpcms.common.util.Constants;
import com.fpcms.common.util.GoogleTranslateUtil;
import com.fpcms.common.webcrawler.htmlparser.HtmlPage;
import com.fpcms.common.webcrawler.htmlparser.HtmlPageCrawler;
import com.fpcms.common.webcrawler.htmlparser.SinglePageCrawler;
import com.fpcms.common.webcrawler.htmlparser.HtmlPage.Anchor;
import com.fpcms.model.CmsContent;
import com.fpcms.service.CmsContentService;

/**
 * 从其它网站进行文章采集的service
 * 
 * @author badqiu
 *
 */
public class ArticleCrawlService implements ApplicationContextAware,InitializingBean{
	
	private List<SinglePageCrawler> singlePageCrawlerList = new ArrayList<SinglePageCrawler>();
	private HtmlPageCrawler htmlPageCrawler = new HtmlPageCrawlerImpl();
	private CmsContentService cmsContentService;
	private ApplicationContext applicationContext;
	
	public void loadSinglePageCrawlerList() {
		singlePageCrawlerList = ApplicationContextUtil.getBeans(applicationContext,SinglePageCrawler.class);
	}

	public void setCmsContentService(CmsContentService cmsContentService) {
		this.cmsContentService = cmsContentService;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void crawlAllSite() {
		for(SinglePageCrawler crawler : singlePageCrawlerList) {
			crawler.setHtmlPageCrawler(htmlPageCrawler);
			crawler.execute();
		}
	}
	
	public List<String> getInvalidUrlList() {
		List<String> invalidUrlList = new ArrayList<String>();
		for(SinglePageCrawler crawler : getSinglePageCrawlerList()) {
			for(String url : crawler.getUrlList()) {
				try {
					List<Anchor> list = crawler.getShoudVisitAnchorList(url);
					if(CollectionUtils.isEmpty(list)) {
						invalidUrlList.add(url);
					}
				}catch(Exception e) {
					invalidUrlList.add(url);
				}
			}
		}
		return invalidUrlList;
	}
	
	public List<SinglePageCrawler> getSinglePageCrawlerList() {
		return singlePageCrawlerList;
	}
	
	private class HtmlPageCrawlerImpl implements HtmlPageCrawler {

		@Override
		public boolean shoudVisitPage(Anchor a) {
			Date start = DateUtils.addDays(new Date(),-160);
			Date end = new Date();
			int count = cmsContentService.countBySourceUrl(start, end, a.getHref());
			if(count > 0) {
				return false;
			}
			return true;
		}

		@Override
		public void visit(HtmlPage page) {
			if(StringUtils.isBlank(page.getContent()) || StringUtils.isBlank(page.getTitle())) {
				return;
			}
			//过滤 \u003c 等unicode非法字符
			if(page.getTitle().contains("\\u") || page.getContent().contains("\\u")) {
				return;
			}
			//过滤 url
			if(page.getContent().contains("http://")) {
				return;
			}
			//过滤 www.
			if(page.getContent().contains("www.")) {
				return;
			}
			
			CmsContent c = new CmsContent();
			c.setContent(GoogleTranslateUtil.translate(page.getContent(),page.getSourceLang(),"zh-CN"));
			c.setTitle(GoogleTranslateUtil.translate(page.getTitle(),page.getSourceLang(),"zh-CN"));
			c.setSourceUrl(page.getAnchor().getHref());
			c.setSite(Constants.CRAWL_SITE);
			c.setChannelCode(Constants.CRAWL_CHANNEL_CODE);
			c.setAuthor(Constants.CRAWL_AUTHOR);
			cmsContentService.create(c);
			
		}
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(applicationContext,"applicationContext must be not null");
		Assert.notNull(cmsContentService,"cmsContentService must be not null");
		loadSinglePageCrawlerList();
		Assert.notEmpty(singlePageCrawlerList,"singlePageCrawlerList must be not empty");
	}
	
}
