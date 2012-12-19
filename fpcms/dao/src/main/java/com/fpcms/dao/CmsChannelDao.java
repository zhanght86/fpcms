/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2012
 */

package com.fpcms.dao;

import java.util.List;

import com.duowan.common.util.page.Page;
import com.fpcms.model.CmsChannel;
import com.fpcms.query.CmsChannelQuery;

/**
 * tableName: cms_channel
 * [CmsChannel] 的Dao操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public interface CmsChannelDao {
	
	public void insert(CmsChannel entity);
	
	public int update(CmsChannel entity);

	public int deleteById(long id);
	
	public CmsChannel getById(long id);
	

	public Page<CmsChannel> findPage(CmsChannelQuery query);

	public List<CmsChannel> findAll();	
	
	public List<CmsChannel> findChildsByChannelId(long channelId);
}