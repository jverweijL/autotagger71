<%@ include file="init.jsp" %>


<%--<portlet:renderURL var="addBusinessRule">
    <portlet:param name="jspPage" value="/edit.jsp"/>
    <portlet:param name="backURL" value="<%= currentURL%>" />
</portlet:renderURL>--%>

<portlet:renderURL var="addBusinessRule">
    <portlet:param name="mvcRenderCommandName" value="/edit" />
    <portlet:param name="redirect" value="<%= currentURL%>" />
</portlet:renderURL>

<portlet:actionURL var="deleteBusinessruleActionURL" name="/delete">
    <portlet:param name="redirect" value="<%= currentURL%>" />
</portlet:actionURL>

<%--<portlet:actionURL var="shareholderActionURL"
                   name="<%=ShareholderPortletKeys.ADD_SHAREHOLDER_VIEW%>">
    <portlet:param name="redirect" value="${param.redirect}" />
</portlet:actionURL>--%>

${ping}

<div class="sheet">
    <div class="sheet-header">
        <h4 class="sheet-title">Rules</h4>
    </div>
    <%--<aui:button cssClass="add-contact" icon="icon-plus-sign" id="addContact" value="add-contact" />--%>
    <%--<aui:button onClick="${addBusinessRule}" icon="plus" primary="true" />--%>
    <div class="position-relative float-right" style="top: -70px;">
        <a href="${addBusinessRule}">
            <clay:button ariaLabel="Add rule" icon="plus" />
        </a>
    </div>
    <div class="sheet-section">
        <div class="table-responsive">
            <table class="table table-autofit table-hover">
                <thead>
                    <tr class="d-flex">
                        <th class="col-2">Actions</th>
                        <th class="col-10">Tagname</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${rules}" var="rule">
                    <tr class="d-flex">
                        <td class="col-2">
                            <div class="dropdown dropdown-action">
                                <a aria-expanded="false" aria-haspopup="true" class="component-action dropdown-toggle" data-toggle="dropdown" href="#1" id="dropdownAction1" role="button">
                                    <svg aria-hidden="true" class="lexicon-icon lexicon-icon-ellipsis-v">
                                        <use href="<%= themeDisplay.getPathThemeImages() %>/clay/icons.svg#ellipsis-v" />
                                    </svg>
                                </a>
                                <ul aria-labelledby="" class="dropdown-menu dropdown-menu-right">
                                    <li><a class="dropdown-item" href="${addBusinessRule}&<%=renderResponse.getNamespace()%>id=${rule.key}" role="button">Edit</a></li>
                                    <li><a class="dropdown-item" href="#1" role="button">Test</a></li>
                                    <li><a class="dropdown-item" href="${deleteBusinessruleActionURL}&<%=renderResponse.getNamespace()%>id=${rule.key}" onclick="return confirm('Are you sure?')" role="button">Delete</a></li>
                                </ul>
                            </div>
                        </td>
                        <td class="col-10">
                            <a class="table-link" href="${rule.key}">${rule.value}</a>
                        </td>
                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>