package com.liferay.demo.autotagging.portlet.portlet.command;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.demo.autotagging.api.api.AutoTaggingApi;
import com.liferay.demo.autotagging.portlet.constants.AutoTaggingPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + AutoTaggingPortletKeys.PORTLETNAME,
                "mvc.command.name=/edit"
        },
        service = MVCRenderCommand.class)
public class EditBusinessruleMVCRenderCommand implements MVCRenderCommand {

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        String id = ParamUtil.getString(renderRequest, "id", "");
        System.out.println("id: " + id);

        Locale locale = renderRequest.getLocale();
        ResourceBundle resourceBundle = ResourceBundleUtil.getBundle("content.Language", locale, getClass());

        String title = LanguageUtil.get(resourceBundle, "businessrule.title.create");
        String button  = LanguageUtil.get(resourceBundle, "businessrule.button.create");
        if (!id.isEmpty()) {
            title = LanguageUtil.get(resourceBundle, "businessrule.title.edit");
            button  = LanguageUtil.get(resourceBundle, "businessrule.button.edit");

            String tagger = _autotaggingService.Get(id);
            System.out.println(tagger);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(tagger);
            } catch (IOException e) {
                e.printStackTrace();
            }

            renderRequest.setAttribute("id", id);
            renderRequest.setAttribute("tagname", jsonNode.get("tag").asText());
            renderRequest.setAttribute("businessrule", jsonNode.get("query").toString());
        }

        renderRequest.setAttribute("redirect", renderRequest.getParameter("redirect"));
        renderRequest.setAttribute("title", title);
        renderRequest.setAttribute("button",button);

        return "/edit.jsp";
    }

    @Reference
    protected AutoTaggingApi _autotaggingService;

    /*@Reference(cardinality=ReferenceCardinality.MANDATORY)
    protected ShareholderService _shareholderService;*/
}