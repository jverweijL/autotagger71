package com.liferay.demo.autotagging.tagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.demo.autotagging.api.AutoTaggingService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
		name = "AutoTagger",
		property = {
				// TODO enter required service properties
		},
		service = ModelListener.class
)
public class AutoTagger extends BaseModelListener<AssetEntry> {

	private static Log _log = LogFactoryUtil.getLog(AutoTagger.class);

	@Override
	public void onAfterCreate(AssetEntry entry) throws ModelListenerException {
		super.onAfterCreate(entry);

		try {
			_log.debug("Entry type: " + entry.getClassName());

			if (mustbeTagged(entry)) {
				// TODO only on actual entries, not structures!!!
				//TODO get all text from AssetEntry
				String result = _AutoTaggingService.Match("try to find some bonsai somewhere in the green  tree forest");
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



	private boolean mustbeTagged(AssetEntry entry) throws PortalException {
		//only autotag if there's an autotag tag or if it's empty
		//String triggerTagName = props.getProperty(HAS_TAG_PROPERTY, "");
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