<%@ include file="init.jsp" %>

<portlet:actionURL var="businessruleActionURL" name="/edit">
    <%--<portlet:param name="redirect" value="${param.redirect}" />--%>
</portlet:actionURL>

<aui:form action="<%= businessruleActionURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveRule();" %>'>
    <aui:input name="redirect" type="hidden" value="${param.redirect}" />
    <aui:input name="id" type="hidden" value="${id}" />
    <div class="sheet sheet-lg">
        <div class="sheet-header">
            <h2 class="sheet-title">${title}</h2>
            <div class="sheet-text">The businessrule is based on an elasticsearch query where you can reference to the <em>message</em> field. In the example you see the autotagger will look for the words 'bonsai or 'tree' with a match query and it is <a href="http://jsonlint.com">valid json</a>. More info on elasticsearch queries can be found <a href=" " target="_blank">here</a>.</div>
        </div>
        <div class="sheet-section">
            <div class="form-group">
                <label for="tagname">Tagname</label>
                <input class="form-control" id="<%=renderResponse.getNamespace()%>tagname"  name="<%=renderResponse.getNamespace()%>tagname" type="text" value="${tagname}"/>
                <%--<input class="field form-control" id="_autotagging_tagname" name="_autotagging_tagname" type="text" value="">
                <aui:input name="tagname" />--%>
            </div>
            <div class="form-group">
                <label for="businessrule">Businessrule</label>
                <textarea class="form-control" id="<%=renderResponse.getNamespace()%>businessrule" name="<%=renderResponse.getNamespace()%>businessrule" placeholder='{"match" : {"message" : "bonsai tree"}}'>${businessrule}</textarea>
            </div>
        </div>
        <div class="sheet-footer sheet-footer-btn-block-sm-down">
            <div class="btn-group">
                <div class="btn-group-item">
                    <button class="btn btn-primary" type="submit">${button}</button>
                </div>
                <div class="btn-group-item">
                    <a href="${redirect}">
                        <button class="btn btn-secondary" type="button">Cancel</button>
                    </a>
                </div>
            </div>
        </div>
    </div>
</aui:form>

<aui:script>
    function <portlet:namespace />saveRule() {
        var form = AUI.$(document.<portlet:namespace />fm);

        console.log("client validation here...");

        submitForm(form);
    }
</aui:script>