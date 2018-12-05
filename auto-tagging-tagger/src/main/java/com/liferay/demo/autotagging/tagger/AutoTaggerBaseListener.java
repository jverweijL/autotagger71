package com.liferay.demo.autotagging.tagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.demo.autotagging.api.AutoTaggingService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;

import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;


/**
 * @author jverweij
 */
@Component(
		immediate = true,
		name = "AutoTaggerBaseListener",
		property = {
				// TODO enter required service properties
		},
		service = ModelListener.class
)
public class AutoTaggerBaseListener extends BaseModelListener<AssetEntry> implements AutoTaggerApi {

	private static Log _log = LogFactoryUtil.getLog(AutoTaggerBaseListener.class);

	@Override
	public void onAfterCreate(AssetEntry entry) throws ModelListenerException {
		super.onAfterCreate(entry);

		String message = entry.getTitleCurrentValue();
		message += " " + entry.getDescription();
		message += " " + entry.getSummary();

		if (entry.getClassName().equalsIgnoreCase(JournalArticle.class.getName())) {
			JournalArticleResource journalArticleResource = null;
			try {
				journalArticleResource = JournalArticleResourceLocalServiceUtil.getArticleResource(entry.getClassPK());
				JournalArticle journalArticle = JournalArticleLocalServiceUtil.getLatestArticle(entry.getGroupId(), journalArticleResource.getArticleId());
				_log.debug(journalArticle.getStatus());
				_log.debug(journalArticle.getContent());

				Document xml = SAXReaderUtil.read(new StringReader(journalArticle.getContent()));
				List<Node> fields = xml.selectNodes("/root/dynamic-element/dynamic-content");
				for (Node field : fields) {
					_log.debug("Adding text from field '" + field.getParent().attributeValue("name") + "'");
					message += " " +  field.getText();
				}
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}

		doMatch(entry,message);
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
			_log.debug("Entry has tag: " + entry.getTags().contains(triggerTag));
			return entry.getTags().contains(triggerTag);
		}
	}

	@Reference(cardinality= ReferenceCardinality.MANDATORY)
	protected AutoTaggingService _AutoTaggingService;
}