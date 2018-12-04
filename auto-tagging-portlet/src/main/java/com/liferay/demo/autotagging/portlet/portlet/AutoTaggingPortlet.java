package com.liferay.demo.autotagging.portlet;

import com.liferay.demo.autotagging.api.api.AutoTaggingApi;
import com.liferay.demo.autotagging.portlet.constants.AutoTaggingPortletKeys;

import java.io.IOException;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

//import com.liferay.demo.autotagging.service.AutoTaggingService;

/**
 * @author jverweij
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=AutoTagging",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + AutoTaggingPortletKeys.PORTLETNAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class AutoTaggingPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		renderRequest.setAttribute("ping",_AutoTaggingService.Ping());
		renderRequest.setAttribute("rules",_AutoTaggingService.List());
		//renderRequest.setAttribute("count",_AutoTaggingService.List().size());


		super.doView(renderRequest, renderResponse);
	}

	@Reference(cardinality= ReferenceCardinality.MANDATORY)
	protected AutoTaggingApi _AutoTaggingService;
}