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
public class AutoTaggerBaseModelListener extends BaseModelListener<JournalArticle> implements AutoTaggerApi {

	private static Log _log = LogFactoryUtil.getLog(AutoTaggerBaseModelListener.class);

	@Override
	public void onAfterCreate(JournalArticle article) throws ModelListenerException {
		//super.onAfterCreate(entry);

		/*String message = entry.getTitleCurrentValue();
		message += " " + entry.getDescription();

		/*try {

			Document xml = SAXReaderUtil.read(new StringReader(article.getContent()));
			List<Node> fields = xml.selectNodes("/root/dynamic-element/dynamic-content");
			for (Node field : fields) {
				_log.debug("Adding text from field '" + field.getParent().attributeValue("name") + "'");
				message += " " + field.getText();
			}

		} catch (DocumentException e) {
			e.printStackTrace();
		}*/

		//TODO put it on the message bus since OnAfterCreate is a bit misleading... it's not all materialized in the DB yet.


		//Fire and forget principle
		//Listeners should handle this
		Message message = new Message();
		message.put("key", article.getPrimaryKey());
		_MessageBus.sendMessage("Autotagger", message);

		//doMatch(entry, message);
	}


	public void doMatch(AssetEntry entry, String message) {
		try {
			_log.debug("Entry type: " + entry.getClassName());

			if (mustbeTagged(entry)) {
				String result = _AutoTaggingService.Match(message);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = null;
				try {
					jsonNode = objectMapper.readTree(result);
				} catch (IOException e) {
					e.printStackTrace();
				}

				List<JsonNode> tags = jsonNode.findValues("tag");

				if (tags.size() > 0) {

					ServiceContext serviceContext = new ServiceContext();
					serviceContext.setCompanyId(entry.getCompanyId());

					for (JsonNode tag : tags) {
						AssetTag assetTag;
						if (AssetTagLocalServiceUtil.hasTag(entry.getGroupId(), tag.asText())) {
							_log.debug("Adding tag: " + tag.asText());
							assetTag = AssetTagLocalServiceUtil.getTag(entry.getGroupId(), tag.asText());
						} else {
							_log.debug("Creating tag: " + tag.asText());
							assetTag = AssetTagLocalServiceUtil.addTag(entry.getUserId(), entry.getGroupId(), tag.asText(), serviceContext);
						}

						AssetTagLocalServiceUtil.addAssetEntryAssetTag(entry.getEntryId(), assetTag);
					}
				}
			}
		} catch (Exception ex) {
			_log.error("Error: " + ex.getMessage());
		}
	}

	public boolean mustbeTagged(AssetEntry entry) throws PortalException {
		//only autotag if there's an autotag tag or if it's empty
		String triggerTagName = "autotag";
		if (triggerTagName.isEmpty()) {
			_log.debug("No trigger tagname found");
			return true;
		} else {
			_log.debug("Checking entry for tag " + triggerTagName);
			AssetTag triggerTag = AssetTagLocalServiceUtil.getTag(entry.getGroupId(), triggerTagName);
			_log.debug("Entry has triggertag: " + entry.getTags().contains(triggerTag));
			return entry.getTags().contains(triggerTag);
		}
	}

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	protected AutoTaggingService _AutoTaggingService;

	@Reference
	MessageBus _MessageBus;

}