package com.liferay.demo.autotagging.tagger;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

public interface AutoTaggerApi extends ModelListener<AssetEntry> {
    void doMatch(AssetEntry entry, String message);
}
