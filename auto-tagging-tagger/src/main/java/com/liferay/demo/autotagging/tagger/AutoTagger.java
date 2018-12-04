package com.liferay.demo.autotagging.tagger;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;

import com.liferay.portal.kernel.model.ModelListener;
import org.osgi.service.component.annotations.Component;


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

		/*if (entry.getClassName().equalsIgnoreCase(JournalArticle.class.getName())) {*/
			try {

				if (mustbeTagged(entry)) {

					//AutoTaggingApi ats = new AutoTaggingService();
					//ats.Match("try to find some bonsai somewhere in the green  tree forest");
					/*String[] tags = fetchTags(entry.getTitleCurrentValue());

					if (tags != null && tags.length > 0) {
						ServiceContext serviceContext = new ServiceContext();
						serviceContext.setCompanyId(entry.getCompanyId());

						//loop over comma-separated items in case of multiple tags..
						for (String tagName: tags) {
							//String tagName = tags[0];
							_log.debug("tagname: " + tagName);
							AssetTag assetTag;
							if (AssetTagLocalServiceUtil.hasTag(entry.getGroupId(), tagName)) {
								assetTag = AssetTagLocalServiceUtil.getTag(entry.getGroupId(), tagName);
							} else {
								_log.debug("create tagname: " + tagName);
								assetTag = AssetTagLocalServiceUtil.addTag(entry.getUserId(), entry.getGroupId(), tagName, serviceContext);
							}

							long[] tagIds = { assetTag.getTagId() };
							_log.debug("tag: " + assetTag.getName());
							_log.debug("entryID: " + entry.getEntryId());

							// connect the tag to the asset
							AssetTagLocalServiceUtil.addAssetEntryAssetTag(entry.getEntryId(), assetTag);
						}
					}*/
				}
			} catch (Exception ex) {
				_log.error("Error: " + ex.getMessage());
			}
		/*}*/
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
}