package com.liferay.demo.autotagging.portlet.portlet.command;

import com.liferay.demo.autotagging.api.AutoTaggingService;
import com.liferay.demo.autotagging.portlet.constants.AutoTaggingPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + AutoTaggingPortletKeys.PORTLETNAME,
                "mvc.command.name=/delete"
        },
        service = MVCActionCommand.class)
public class DeleteBusinessruleMVCActionCommand extends BaseMVCActionCommand {
    @Override
    protected void doProcessAction(
            ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {

        /*List<String> errors = new ArrayList<String>();
        ThemeDisplay themeDisplay =
                (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

        ServiceContext serviceContext =
                ServiceContextFactory.getInstance(actionRequest);
*/
        System.out.println("Removing " + ParamUtil.getString(actionRequest, "id"));
        _autotaggingService.Delete(ParamUtil.getString(actionRequest, "id"));

        //hideDefaultSuccessMessage(actionRequest);
        sendRedirect(actionRequest, actionResponse);
    }

    @Reference
    protected AutoTaggingService _autotaggingService;
}