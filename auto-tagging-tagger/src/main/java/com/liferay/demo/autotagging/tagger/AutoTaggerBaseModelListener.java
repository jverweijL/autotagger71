package com.liferay.demo.autotagging.tagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.demo.autotagging.api.AutoTaggingService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.BaseModelListener;

import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import java.io.IOException;
import java.util.List;


/**
 * @author jverweij
 */
@Component(
		immediate = true,
		name = "AutoTaggerBaseModelListener",
		property = {
				// TODO enter required service properties
		},
		service = ModelListener.class
)
public class AutoTaggerBaseModelListener extends BaseModelListener<JournalArticle> {

	private static Log _log = LogFactoryUtil.getLog(AutoTaggerBaseModelListener.class);

	@Override
	public void onAfterCreate(JournalArticle article) throws ModelListenerException {
		super.onAfterCreate(article);

		//put it on the message bus since OnAfterCreate is a bit misleading... it's not all materialized in the DB yet.
		//Fire and forget principle, listeners should handle this
		Message message = new Message();
		message.put("articleId", article.getArticleId());
		message.put("groupId", article.getGroupId());
		_MessageBus.sendMessage(AutoTaggerConfigurator.DESTINATION, message);
	}

	@Reference
	MessageBus _MessageBus;

}